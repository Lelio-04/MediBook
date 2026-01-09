package it.unisa.medibook.config;

import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.*;
import it.unisa.medibook.modelService.PasswordService; // <--- 1. IMPORT NECESSARIO
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

    @Autowired
    private SegreteriaUtentiRepository segreteriaUtentiRepository;

    @Autowired
    private SegreteriaPrenotazioniRepository segreteriaPrenotazioniRepository;

    @Autowired
    private PasswordService passwordService; // <--- 2. INIEZIONE PASSWORD SERVICE

    @Override
    public void run(String... args) throws Exception {
        // Controllo se esiste già almeno un utente per evitare duplicati
        if (utenteRepository.count() > 0) {
            return;
        }

        System.out.println("--- Inizializzazione Dati di Prova (Seeding con HASH) ---");

        // 1. CREAZIONE MEDICO 1
        Medico medico = new Medico();
        medico.setEmail("rossi@medibook.it");
        // HASH PASSWORD
        medico.setPassword(passwordService.hash("password"));
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
        // HASH PASSWORD
        medico2.setPassword(passwordService.hash("password"));
        medico2.setRuolo("MEDICO");
        medico2.setNome("Flavio");
        medico2.setCognome("Verdi");
        medico2.setSpecializzazione("Oncologia");
        medico2.setNumeroAlbo("67890");
        medico2.setTurni("2:10:00-18:00,5:09:00-12:00");
        medicoRepository.save(medico2);

        //3 . Creazione medico 3
        Medico medico3 = new Medico();
        medico3.setEmail("crisci.lelio04@gmail.com");
        // HASH PASSWORD
        medico3.setPassword(passwordService.hash("password3"));
        medico3.setRuolo("MEDICO");
        medico3.setNome("Lelio");
        medico3.setCognome("Crisci");
        medico3.setSpecializzazione("Dermatologia");
        medico3.setNumeroAlbo("12345");
        medico3.setTurni("1:09:00-13:00,3:15:00-19:00,4:15:00-19:00");
        medicoRepository.save(medico3);

        // --- 3. CREAZIONE SEGRETERIA UTENTI ---
        SegreteriaUtenti segUtenti = new SegreteriaUtenti();
        segUtenti.setEmail("segreteria.utenti@medibook.it");
        // HASH PASSWORD
        segUtenti.setPassword(passwordService.hash("admin"));
        segUtenti.setRuolo("SEGRETERIA");
        segUtenti.setNome("Anna");
        segUtenti.setCognome("Bianchi");

        segreteriaUtentiRepository.save(segUtenti);
        System.out.println("-> Creata Segreteria Utenti: segreteria.utenti@medibook.it / admin");

        // --- 4. CREAZIONE SEGRETERIA PRENOTAZIONI ---
        SegreteriaPrenotazioni segPrenotazioni = new SegreteriaPrenotazioni();
        segPrenotazioni.setEmail("segreteria.prenotazioni@medibook.it");
        // HASH PASSWORD
        segPrenotazioni.setPassword(passwordService.hash("admin"));
        segPrenotazioni.setRuolo("SEGRETERIA");
        segPrenotazioni.setNome("Carla");
        segPrenotazioni.setCognome("Neri");

        segreteriaPrenotazioniRepository.save(segPrenotazioni);
        System.out.println("-> Creata Segreteria Prenotazioni: segreteria.prenotazioni@medibook.it / admin");

        // 5. CREAZIONE PAZIENTE
        Paziente paziente = new Paziente();
        paziente.setEmail("paziente1@gmail.com");
        // HASH PASSWORD
        paziente.setPassword(passwordService.hash("password"));
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

        System.out.println("--- Dati inseriti correttamente (Password Cifrate) ---");
    }
}