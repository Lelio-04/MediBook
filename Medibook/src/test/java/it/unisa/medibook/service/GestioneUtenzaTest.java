package it.unisa.medibook.service;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.UtenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneUtenzaTest {

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private PazienteRepository pazienteRepository;

    @InjectMocks
    private GestioneUtenza gestioneUtenza;

    @Test
    void login() {
        // --- TC_AUC_1: Login con Password Errata ---
        Utente utenteTest = new Utente();
        utenteTest.setEmail("paziente@test.it");
        utenteTest.setPassword("hash_corretto_nel_db");

        when(utenteRepository.findByEmail("paziente@test.it")).thenReturn(Optional.of(utenteTest));
        when(passwordService.check("PasswordSbagliata789", "hash_corretto_nel_db")).thenReturn(false);

        Utente resultPassErrata = gestioneUtenza.login("paziente@test.it", "PasswordSbagliata789");
        assertNull(resultPassErrata, "TC_AUC_2 Fallito: Il login dovrebbe restituire null per password errata");

        // --- TC_AUC_2: Login con Successo ---
        Utente utenteSuccesso = new Utente();
        utenteSuccesso.setEmail("mariorossi@gmail.com");
        utenteSuccesso.setPassword("hash_mario_123");

        when(utenteRepository.findByEmail("mariorossi@gmail.com")).thenReturn(Optional.of(utenteSuccesso));
        when(passwordService.check("mario123456", "hash_mario_123")).thenReturn(true);

        Utente resultSuccesso = gestioneUtenza.login("mariorossi@gmail.com", "mario123456");
        assertNotNull(resultSuccesso, "TC_AUC_3 Fallito: Il login dovrebbe restituire l'oggetto utente");
    }

    @Test
    void registraPaziente() {
        // --- Test: Validazione Lunghezza Codice Fiscale (TC_REG_3 - Lato Server) ---
        Paziente pCfCorto = new Paziente();
        pCfCorto.setCodiceFiscale("RSSMRA80");

        Exception exCfLen = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pCfCorto, "password123");
        });
        assertEquals("Codice Fiscale non valido", exCfLen.getMessage());

        // --- TC_REG_1: Formato Email Errato ---
        Paziente pEmailBad = new Paziente();
        pEmailBad.setCodiceFiscale("RSSMRA80A01H501U");
        pEmailBad.setEmail("email_senza_chiocciola");

        Exception exEmailFmt = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pEmailBad, "password123");
        });
        assertEquals("Formato email non valido", exEmailFmt.getMessage());

        // --- TC_REG_4: Lunghezza Password non valida ---
        Paziente pPassShort = new Paziente();
        pPassShort.setCodiceFiscale("RSSMRA80A01H501U");
        pPassShort.setEmail("test@valid.it");

        Exception exPass = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pPassShort, "short");
        });
        assertEquals("Lunghezza Password non valida (min. 8 caratteri)", exPass.getMessage());

        // --- TC_REG_2: Email Duplicata (Database) ---
        Paziente pDuplicato = new Paziente();
        pDuplicato.setEmail("mariorossi@gmail.com");
        pDuplicato.setCodiceFiscale("RSSMRA80A01H501U");

        // Mock: L'email esiste già
        when(utenteRepository.findByEmail("mariorossi@gmail.com")).thenReturn(Optional.of(new Utente()));

        Exception exEmailDup = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pDuplicato, "PasswordSicura123");
        });
        assertEquals("Email già presente nel sistema.", exEmailDup.getMessage());

        // --- Test: Codice Fiscale Duplicato (Database) ---
        Paziente pCfDuplicato = new Paziente();
        pCfDuplicato.setEmail("nuovautente@gmail.com");
        pCfDuplicato.setCodiceFiscale("RSSMRA80A01H501U");

        // Setup Mock
        when(utenteRepository.findByEmail("nuovautente@gmail.com")).thenReturn(Optional.empty());
        // Simula che il CF esista già (restituisce un Optional con dentro un paziente)
        when(pazienteRepository.findByCodiceFiscale("RSSMRA80A01H501U")).thenReturn(Optional.of(new Paziente()));

        Exception exCfDup = assertThrows(Exception.class, () -> {
            gestioneUtenza.registraPaziente(pCfDuplicato, "PasswordSicura123");
        });
        assertEquals("Codice Fiscale già presente.", exCfDup.getMessage());

        // --- TC_REG_5: Registrazione con Successo ---
        Paziente nuovoP = new Paziente();
        nuovoP.setNome("Mario");
        nuovoP.setCognome("Rossi");
        nuovoP.setEmail("mariorossi80@gmail.com");
        nuovoP.setCodiceFiscale("VRDLCU80A01H501U");

        // Mock: Tutto libero
        when(utenteRepository.findByEmail("mariorossi80@gmail.com")).thenReturn(Optional.empty());

        // CORREZIONE QUI: Restituire Optional.empty() invece di null
        when(pazienteRepository.findByCodiceFiscale("VRDLCU80A01H501U")).thenReturn(Optional.empty());

        when(passwordService.hash("PasswordSicura123")).thenReturn("hash_sicuro");

        assertDoesNotThrow(() -> {
            gestioneUtenza.registraPaziente(nuovoP, "PasswordSicura123");
        });

        // Verifica: il repository.save è stato chiamato?
        verify(pazienteRepository, times(1)).save(nuovoP);

        // Verifica post-condizioni
        assertEquals("PAZIENTE", nuovoP.getRuolo());
        assertEquals("hash_sicuro", nuovoP.getPassword());
    }

    @Test
    void cambioPasswordObbligatorio() {
        // --- TC_CPW_1: Cambio Password Corretto ---
        Utente utente = new Utente();
        utente.setId(1);
        utente.setEmail("paziente@test.it");
        utente.setPassword("hash_provvisorio");

        // Setup Mock
        when(utenteRepository.findById(1)).thenReturn(Optional.of(utente));
        when(passwordService.hash("NuovaPassword2026!")).thenReturn("nuovo_hash_sicuro");
        when(utenteRepository.save(any(Utente.class))).thenReturn(utente);

        assertDoesNotThrow(() -> {
            gestioneUtenza.cambioPasswordObbligatorio(1, "NuovaPassword2026!");
        });

        assertEquals("nuovo_hash_sicuro", utente.getPassword());

        // --- Scenario Errore: Utente non trovato ---
        when(utenteRepository.findById(999)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            gestioneUtenza.cambioPasswordObbligatorio(999, "NuovaPassword2026!");
        });
        assertEquals("Utente non trovato", ex.getMessage());
    }
}