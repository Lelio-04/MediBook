package it.unisa.medibook.config;

import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PazienteRepository pazienteRepository;
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    // --- NUOVI REPOSITORY SPECIFICI ---
    @Autowired
    private SegreteriaUtentiRepository segreteriaUtentiRepository;

    @Autowired
    private SegreteriaPrenotazioniRepository segreteriaPrenotazioniRepository;

    @Override
    public void run(String... args) throws Exception {
        // Controllo se esiste già almeno un utente per evitare duplicati
        if (utenteRepository.count() > 0) {
            return;
        }

        System.out.println("--- Inizializzazione Dati di Prova (Seeding) ---");

        // 1. CREAZIONE MEDICO 1
        Medico medico = new Medico();
        medico.setEmail("rossi@medibook.it");
        medico.setPassword("password");
        medico.setRuolo("MEDICO");
        medico.setNome("Mario");
        medico.setCognome("Rossi");
        medico.setSpecializzazione("Cardiologia");
        medico.setNumeroAlbo("12345");
        medico.setTurni("1:09:00-13:00,3:15:00-19:00,4:15:00-19:00");
        medicoRepository.save(medico);

        // 2. CREAZIONE MEDICO 2
        Medico medico2 = new Medico();
        medico2.setEmail("verdi@medibook.it");
        medico2.setPassword("password");
        medico2.setRuolo("MEDICO");
        medico2.setNome("Flavio");
        medico2.setCognome("Verdi");
        medico2.setSpecializzazione("Oncologia");
        medico2.setNumeroAlbo("67890");
        medico2.setTurni("2:10:00-18:00,5:09:00-12:00");
        medicoRepository.save(medico2);

        // --- 3. CREAZIONE SEGRETERIA UTENTI (Gestione Anagrafiche) ---
        SegreteriaUtenti segUtenti = new SegreteriaUtenti();
        segUtenti.setEmail("segreteria.utenti@medibook.it"); // Email specifica
        segUtenti.setPassword("admin");
        segUtenti.setRuolo("SEGRETERIA"); // Il ruolo stringa rimane generico, la classe fa la differenza
        segUtenti.setNome("Anna");
        segUtenti.setCognome("Bianchi");

        segreteriaUtentiRepository.save(segUtenti);
        System.out.println("-> Creata Segreteria Utenti: segreteria.utenti@medibook.it / admin");

        // --- 4. CREAZIONE SEGRETERIA PRENOTAZIONI (Gestione Agenda) ---
        SegreteriaPrenotazioni segPrenotazioni = new SegreteriaPrenotazioni();
        segPrenotazioni.setEmail("segreteria.prenotazioni@medibook.it"); // Email specifica
        segPrenotazioni.setPassword("admin");
        segPrenotazioni.setRuolo("SEGRETERIA");
        segPrenotazioni.setNome("Carla");
        segPrenotazioni.setCognome("Neri");

        segreteriaPrenotazioniRepository.save(segPrenotazioni);
        System.out.println("-> Creata Segreteria Prenotazioni: segreteria.prenotazioni@medibook.it / admin");

        // 5. CREAZIONE PAZIENTE
        Paziente paziente = new Paziente();
        paziente.setEmail("paziente1@gmail.com");
        paziente.setPassword("password");
        paziente.setRuolo("PAZIENTE");
        paziente.setNome("Luca");
        paziente.setCognome("Verdi");
        paziente.setCodiceFiscale("VRDLCU80A01H501U");
        paziente.setTelefono("3331234567");

        pazienteRepository.save(paziente);

        // 6. CREAZIONE PRENOTAZIONE DI PROVA
        Prenotazione p1 = new Prenotazione();
        p1.setMedico(medico);
        p1.setPaziente(paziente);
        p1.setData(LocalDate.now().plusDays(1)); // Domani
        p1.setOra(LocalTime.of(10, 0));
        p1.setStato("PRENOTATA");

        prenotazioneRepository.save(p1);

        System.out.println("--- Dati inseriti correttamente ---");
    }
}