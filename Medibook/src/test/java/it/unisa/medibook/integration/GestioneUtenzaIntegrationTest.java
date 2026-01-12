package it.unisa.medibook.integration;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.UtenteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.service.GestioneUtenza;
import it.unisa.medibook.service.PasswordService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GestioneUtenzaIntegrationTest {

    @Autowired private GestioneUtenza gestioneUtenza;
    @Autowired private UtenteRepository utenteRepository;
    @Autowired private PazienteRepository pazienteRepository;
    @Autowired private PrenotazioneRepository prenotazioneRepository;

    // Se hai prenotazioni o referti, autowirali qui per pulirli
    // @Autowired private PrenotazioneRepository prenotazioneRepository;

    // Usiamo il servizio VERO, non un Mock
    @Autowired private PasswordService passwordService;


    @BeforeEach
    void setUp() {
        // Replica la logica di "eliminaPaziente" del Service:
        // Pulisci prima le prenotazioni per evitare vincoli di Foreign Key
        prenotazioneRepository.deleteAll();

        // Ora puoi pulire i pazienti senza errori
        pazienteRepository.deleteAll();
        utenteRepository.deleteAll();
    }

    // --- TEST LOGIN (Senza Mock) ---

    @Test
    void login_Successo_E_Fallimento() {
        // 1. SETUP: Creiamo un utente reale con password hashata vera
        String rawPass = "password_segreta_123";

        Utente u = new Utente();
        u.setEmail("mariorossi@gmail.com");
        u.setPassword(passwordService.hash(rawPass)); // Hashiamo realmente
        u.setRuolo("PAZIENTE");
        utenteRepository.save(u);

        // 2. TEST: Login Password Errata
        Utente resultErrato = gestioneUtenza.login("mariorossi@gmail.com", "password_sbagliata");
        assertNull(resultErrato, "Login con password errata deve restituire null");

        // 3. TEST: Login Successo
        Utente resultCorretto = gestioneUtenza.login("mariorossi@gmail.com", rawPass);
        assertNotNull(resultCorretto, "Login corretto deve restituire l'utente");
        assertEquals("mariorossi@gmail.com", resultCorretto.getEmail());
    }

    // --- TEST REGISTRAZIONE ---

    @Test
    void registraPaziente_Validazioni() {
        // A. Codice Fiscale Corto
        Paziente pCfCorto = new Paziente();
        pCfCorto.setCodiceFiscale("RSSMRA80"); // Troppo corto

        Exception exCf = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pCfCorto, "password123");
        });
        // Nota: Verifica che il messaggio contenga effettivamente "Codice Fiscale"
        // assertTrue(exCf.getMessage().contains("Codice Fiscale"));

        // VERIFICA FINALE: Il DB deve essere vuoto
        assertEquals(0, utenteRepository.count(), "Nessun utente doveva essere salvato in caso di errore");
    }

    @Test
    void registraPaziente_Duplicati() throws Exception {
        // 1. SETUP
        Paziente esistente = new Paziente();
        esistente.setNome("Vecchio");
        esistente.setCognome("Utente");
        esistente.setEmail("esistente@test.it");
        esistente.setCodiceFiscale("VCHTNT80A01H501X");
        esistente.setPassword("old_hash");
        esistente.setRuolo("PAZIENTE");
        pazienteRepository.save(esistente);

        // 2. TEST: Email Duplicata
        Paziente pEmailDup = new Paziente();
        pEmailDup.setEmail("esistente@test.it");
        pEmailDup.setCodiceFiscale("NUOVOC80A01H501Y");

        Exception exEmail = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pEmailDup, "password123");
        });
        assertEquals("Email già presente nel sistema.", exEmail.getMessage());

        // 3. TEST: Codice Fiscale Duplicato
        Paziente pCfDup = new Paziente();
        pCfDup.setEmail("nuova@test.it");
        pCfDup.setCodiceFiscale("VCHTNT80A01H501X"); // CF uguale a 'esistente'

        Exception exCf = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pCfDup, "password123");
        });
        assertEquals("Codice Fiscale già presente.", exCf.getMessage());
    }

    @Transactional
    public void registraPaziente(Paziente p, String passwordInChiaro) throws Exception {

        if (p.getCodiceFiscale() == null || p.getCodiceFiscale().length() != 16) {
            throw new Exception("Codice Fiscale non valido");
        }

        if (p.getEmail() == null || !p.getEmail().contains("@") || !p.getEmail().contains(".")) {
            throw new Exception("Formato email non valido");
        }

        if (passwordInChiaro == null || passwordInChiaro.length() < 8) {
            throw new Exception("Lunghezza Password non valida (min. 8 caratteri)");
        }

        if (utenteRepository.findByEmail(p.getEmail()).isPresent()) {
            throw new Exception("Email già presente nel sistema.");
        }

        // --- CORREZIONE QUI SOTTO ---
        // Usiamo .isPresent() invece di != null
        if (pazienteRepository.findByCodiceFiscale(p.getCodiceFiscale()).isPresent()) {
            throw new Exception("Codice Fiscale già presente.");
        }
        // ----------------------------

        p.setRuolo("PAZIENTE");
        p.setPassword(passwordService.hash(passwordInChiaro));

        pazienteRepository.save(p);
    }
}