package it.unisa.medibook.service;

import it.unisa.medibook.model.Paziente; // <--- Importante: Importa la classe Paziente
import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository; // <--- Importante: Repository specifico
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.modelStorage.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestioneUtenza {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PazienteRepository pazienteRepository; // <--- AGGIUNTA 1: Ci serve per salvare i pazienti

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService;

    @Transactional
    public Paziente aggiornaProfiloPaziente(Integer idUtente, String telefono, String indirizzo, String nuovaPassword) throws Exception {
        Paziente p = pazienteRepository.findById(idUtente)
                .orElseThrow(() -> new Exception("Utente non trovato"));

        // Aggiorna dati base
        p.setTelefono(telefono);
        p.setIndirizzo(indirizzo);

        // Gestione cambio password sicura
        if (nuovaPassword != null && !nuovaPassword.trim().isEmpty()) {
            p.setPassword(passwordService.hash(nuovaPassword));
        }

        return pazienteRepository.save(p);
    }

    /**
     * AGGIUNTA 2: Metodo per registrare un nuovo paziente.
     * Imposta il ruolo fisso e salva nel DB.
     */
    // Assicurati di avere @Autowired private MedicoRepository medicoRepository;
    public Utente login(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email).orElse(null);

        // Verifica: Esiste? La password è corretta?
        if (utente != null && passwordService.check(password, utente.getPassword())) {
            return utente;
        }
        return null;
    }

    // --- 2. REGISTRAZIONE PAZIENTE (Hash incluso) ---
    @Transactional
    public void registraPaziente(Paziente p, String passwordInChiaro) throws Exception {
        // Controllo duplicati
        if (utenteRepository.findByEmail(p.getEmail()).isPresent()) {
            throw new Exception("Email già presente nel sistema.");
        }
        if (pazienteRepository.existsByCodiceFiscale(p.getCodiceFiscale())) { // Assicurati di avere questo metodo nella repo o fallo a mano
            throw new Exception("Codice Fiscale già presente.");
        }

        // Imposta sicurezza
        p.setRuolo("PAZIENTE");
        p.setPassword(passwordService.hash(passwordInChiaro));

        pazienteRepository.save(p);
    }

    // --- 3. CAMBIO PASSWORD OBBLIGATORIO ---
    @Transactional
    public Utente cambioPasswordObbligatorio(Integer idUtente, String nuovaPassword) throws Exception {
        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new Exception("Utente non trovato"));

        utente.setPassword(passwordService.hash(nuovaPassword));
        return utenteRepository.save(utente);
    }

    // Serve per il profilo
    public Paziente getPazienteById(Integer id) {
        return pazienteRepository.findById(id).orElse(null);
    }
    public List<Paziente> dammiTuttiIPazienti() {
        return pazienteRepository.findAll();
    }
    @Transactional
    public void eliminaPaziente(Integer id) {
        List<Prenotazione> visite = prenotazioneRepository.findByPazienteId(id);
        if (!visite.isEmpty()) {
            prenotazioneRepository.deleteAll(visite);
        }
        pazienteRepository.deleteById(id);
    }

    /**
     * Gestisce sia la creazione che la modifica di un paziente dalla segreteria
     */
    @Transactional
    public void salvaOAggiornaPaziente(Paziente p) throws Exception {
        if (p.getId() == null) {
            // NUOVO PAZIENTE
            String passwordProvvisoria = "Medibook123";
            p.setPassword(passwordService.hash(passwordProvvisoria));
            p.setRuolo("PAZIENTE");

            pazienteRepository.save(p);

            // Invio Email (può rimanere qui o essere delegato)
            emailService.inviaEmailBenvenuto(p.getEmail(), p.getNome(), p.getCognome(), passwordProvvisoria);
        } else {
            // MODIFICA ESISTENTE
            Paziente esistente = pazienteRepository.findById(p.getId())
                    .orElseThrow(() -> new Exception("Paziente non trovato"));

            esistente.setNome(p.getNome());
            esistente.setCognome(p.getCognome());
            esistente.setCodiceFiscale(p.getCodiceFiscale());
            esistente.setTelefono(p.getTelefono());
            esistente.setIndirizzo(p.getIndirizzo());

            pazienteRepository.save(esistente);
        }
    }
}