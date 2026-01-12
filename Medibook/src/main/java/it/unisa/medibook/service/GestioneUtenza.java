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
    private PazienteRepository pazienteRepository;

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


        p.setTelefono(telefono);
        p.setIndirizzo(indirizzo);


        if (nuovaPassword != null && !nuovaPassword.trim().isEmpty()) {
            p.setPassword(passwordService.hash(nuovaPassword));
        }

        return pazienteRepository.save(p);
    }


    public Utente login(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email).orElse(null);


        if (utente != null && passwordService.check(password, utente.getPassword())) {
            return utente;
        }
        return null;
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


        if (pazienteRepository.findByCodiceFiscale(p.getCodiceFiscale()).isPresent()) {
            throw new Exception("Codice Fiscale già presente.");
        }

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


    // Gestisce sia la creazione che la modifica di un paziente dalla segreteria
    @Transactional
    public void salvaOAggiornaPaziente(Paziente p) throws Exception {
        if (p.getId() == null) {
            // NUOVO PAZIENTE
            String passwordProvvisoria = "Medibook123";
            p.setPassword(passwordService.hash(passwordProvvisoria));
            p.setRuolo("PAZIENTE");

            pazienteRepository.save(p);

            emailService.inviaEmailBenvenuto(p.getEmail(), p.getNome(), p.getCognome(), passwordProvvisoria);
        } else {
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