package it.unisa.medibook.config;

import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.*;
import it.unisa.medibook.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private UtenteRepository utenteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private PazienteRepository pazienteRepository;
    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private SegreteriaUtentiRepository segreteriaUtentiRepository;
    @Autowired private SegreteriaPrenotazioniRepository segreteriaPrenotazioniRepository;
    @Autowired private PasswordService passwordService;

    @Override
    public void run(String... args) throws Exception {
        // Controllo se esiste già dati per evitare duplicati in avvio senza truncate
        if (utenteRepository.count() > 0) return;

        System.out.println("--- Inizializzazione Dati di Prova (MediBook Seeding) ---");

        // -------------------------------------------------------------------------
        // 1. ATTORI (UTENTI)
        // -------------------------------------------------------------------------

        // MEDICO 1: Rossi (Mattutino) -> Per test Slot Occupato
        Medico rossi = new Medico();
        rossi.setEmail("rossi@medibook.it");
        rossi.setPassword(passwordService.hash("password"));
        rossi.setRuolo("MEDICO");
        rossi.setNome("Mario");
        rossi.setCognome("Rossi");
        rossi.setSpecializzazione("Cardiologia");
        rossi.setNumeroAlbo("12345");
        // Turni di mattina (09:00 - 13:00)
        rossi.setTurni("1:09:00-13:00,2:09:00-13:00,3:09:00-13:00,4:09:00-13:00,5:09:00-13:00");
        medicoRepository.save(rossi);

        // MEDICO 2: Verdi (Pomeridiano) -> Per test Prenotazione Successo
        Medico verdi = new Medico();
        verdi.setEmail("verdi@medibook.it");
        verdi.setPassword(passwordService.hash("password"));
        verdi.setRuolo("MEDICO");
        verdi.setNome("Flavio");
        verdi.setCognome("Verdi");
        verdi.setSpecializzazione("Oncologia");
        verdi.setNumeroAlbo("67890");
        // Turni di pomeriggio (14:00 - 18:00)
        verdi.setTurni("1:14:00-18:00,2:14:00-18:00,3:14:00-18:00,4:14:00-18:00,5:14:00-18:00");
        medicoRepository.save(verdi);

        // MEDICO 3: Crisci
        Medico crisci = new Medico();
        crisci.setEmail("crisci.lelio04@gmail.com");
        crisci.setPassword(passwordService.hash("password3"));
        crisci.setRuolo("MEDICO");
        crisci.setNome("Lelio");
        crisci.setCognome("Crisci");
        crisci.setSpecializzazione("Dermatologia");
        crisci.setNumeroAlbo("11122");
        crisci.setTurni("1:09:00-13:00,3:15:00-19:00");
        medicoRepository.save(crisci);

        // SEGRETERIA UTENTI
        SegreteriaUtenti segUtenti = new SegreteriaUtenti();
        segUtenti.setEmail("segreteria.utenti@medibook.it");
        segUtenti.setPassword(passwordService.hash("admin"));
        segUtenti.setRuolo("SEGRETERIA");
        segUtenti.setNome("Anna");
        segUtenti.setCognome("Bianchi");
        segreteriaUtentiRepository.save(segUtenti);

        // SEGRETERIA PRENOTAZIONI
        SegreteriaPrenotazioni segPrenotazioni = new SegreteriaPrenotazioni();
        segPrenotazioni.setEmail("segreteria.prenotazioni@medibook.it");
        segPrenotazioni.setPassword(passwordService.hash("admin"));
        segPrenotazioni.setRuolo("SEGRETERIA");
        segPrenotazioni.setNome("Carla");
        segPrenotazioni.setCognome("Neri");
        segreteriaPrenotazioniRepository.save(segPrenotazioni);

        // PAZIENTE 1: Luca Verdi (Generico)
        Paziente luca = new Paziente();
        luca.setEmail("paziente1@gmail.com");
        luca.setPassword(passwordService.hash("password"));
        luca.setRuolo("PAZIENTE");
        luca.setNome("Luca");
        luca.setCognome("Verdi");
        luca.setCodiceFiscale("VRDLCU80A01H501U");
        luca.setTelefono("3331234567");
        pazienteRepository.save(luca);

        // PAZIENTE 2: Mario Rossi (Per LOGIN nei test Selenium)
        Paziente mario = new Paziente();
        mario.setEmail("mariorossi@gmail.com");
        mario.setPassword(passwordService.hash("mario123456")); // <--- PASSWORD NOTA PER TEST
        mario.setRuolo("PAZIENTE");
        mario.setNome("Mario");
        mario.setCognome("Rossi");
        mario.setCodiceFiscale("RSSMRA80A01H501U");
        pazienteRepository.save(mario);

        // PAZIENTI TEST AGGIUNTIVI
        Paziente pazienteTest = new Paziente();
        pazienteTest.setEmail("paziente@test.it");
        pazienteTest.setPassword(passwordService.hash("PasswordCorretta123"));
        pazienteTest.setRuolo("PAZIENTE");
        pazienteTest.setNome("Test");
        pazienteTest.setCognome("User");
        pazienteTest.setCodiceFiscale("RSSMRA80A01H501L");
        pazienteRepository.save(pazienteTest);

        Paziente pazienteTestCPW = new Paziente();
        pazienteTestCPW.setEmail("paziente@testcpw.it");
        pazienteTestCPW.setPassword(passwordService.hash("Medibook123"));
        pazienteTestCPW.setRuolo("PAZIENTE");
        pazienteTestCPW.setNome("Samuele");
        pazienteTestCPW.setCognome("Test");
        pazienteTestCPW.setCodiceFiscale("TSTSMU80A01H501U");
        pazienteRepository.save(pazienteTestCPW);


        // -------------------------------------------------------------------------
        // 2. PRENOTAZIONI (SCENARI DI TEST)
        // -------------------------------------------------------------------------

        // [UC3] TC_PRE_2: SLOT OCCUPATO
        // Prenotazione per DOMANI alle 10:00 con Rossi.
        // Se Mario Rossi prova a prenotare questo slot, fallirà perché occupato da Luca.
        Prenotazione pOccupata = new Prenotazione();
        pOccupata.setMedico(rossi);
        pOccupata.setPaziente(luca);
        pOccupata.setData(LocalDate.now().plusDays(1)); // DOMANI
        pOccupata.setOra(LocalTime.of(10, 0));
        pOccupata.setStato("PRENOTATA");
        prenotazioneRepository.save(pOccupata);
        System.out.println("-> [UC3] Creata prenotazione occupata: Domani 10:00 (Dr. Rossi)");

        // [UC9] TEST REFERTI
        // p701: Stato non valido
        Prenotazione p701 = new Prenotazione();
        p701.setData(LocalDate.now());
        p701.setOra(LocalTime.of(15, 0));
        p701.setStato("PRENOTATA");
        p701.setMedico(rossi);
        p701.setPaziente(luca);
        prenotazioneRepository.save(p701);

        // p702: Referto vuoto (Stato EFFETTUATA)
        Prenotazione p702 = new Prenotazione();
        p702.setData(LocalDate.now());
        p702.setOra(LocalTime.of(16, 0));
        p702.setStato("EFFETTUATA");
        p702.setMedico(rossi);
        p702.setPaziente(luca);
        prenotazioneRepository.save(p702);

        // p703: Referto successo (Stato EFFETTUATA)
        Prenotazione p703 = new Prenotazione();
        p703.setData(LocalDate.now());
        p703.setOra(LocalTime.of(17, 0));
        p703.setStato("EFFETTUATA");
        p703.setMedico(rossi);
        p703.setPaziente(luca);
        prenotazioneRepository.save(p703);

        // [UC GESTIONE VISITE]
        // Visite per testare la dashboard del Medico
        Prenotazione vG1 = new Prenotazione();
        vG1.setData(LocalDate.now());
        vG1.setOra(LocalTime.of(11, 0));
        vG1.setStato("PRENOTATA");
        vG1.setMedico(rossi);
        vG1.setPaziente(luca);
        prenotazioneRepository.save(vG1);

        Prenotazione vG2 = new Prenotazione();
        vG2.setData(LocalDate.now());
        vG2.setOra(LocalTime.of(12, 0));
        vG2.setStato("PRENOTATA");
        vG2.setMedico(rossi);
        vG2.setPaziente(luca);
        prenotazioneRepository.save(vG2);

        Prenotazione vConclusa = new Prenotazione();
        vConclusa.setData(LocalDate.now().minusDays(2));
        vConclusa.setOra(LocalTime.of(9, 0));
        vConclusa.setStato("CONCLUSA");
        vConclusa.setMedico(rossi);
        vConclusa.setPaziente(pazienteTest);
        prenotazioneRepository.save(vConclusa);

        // [UC4] MODIFICA PRENOTAZIONE
        // TC_MOD_3: Visita passata/effettuata (Non modificabile)
        Prenotazione pStorico = new Prenotazione();
        pStorico.setData(LocalDate.now().minusDays(5));
        pStorico.setOra(LocalTime.of(9, 0));
        pStorico.setStato("EFFETTUATA"); // <--- Mantenuto come richiesto
        pStorico.setMedico(rossi);
        pStorico.setPaziente(luca);
        prenotazioneRepository.save(pStorico);

        // TC_MOD_2/4: Visita futura modificabile
        Prenotazione pDaModificare = new Prenotazione();
        pDaModificare.setData(LocalDate.now().plusDays(10));
        pDaModificare.setOra(LocalTime.of(10, 0));
        pDaModificare.setStato("PRENOTATA");
        pDaModificare.setMedico(rossi);
        pDaModificare.setPaziente(luca);
        prenotazioneRepository.save(pDaModificare);

        // TC_MOD_2: Conflitto (Slot occupato tra 15gg alle 15:00)
        Prenotazione pConflitto = new Prenotazione();
        pConflitto.setData(LocalDate.now().plusDays(15));
        pConflitto.setOra(LocalTime.of(15, 0));
        pConflitto.setStato("PRENOTATA");
        pConflitto.setMedico(rossi);
        pConflitto.setPaziente(mario);
        prenotazioneRepository.save(pConflitto);

        System.out.println("--- Dati inseriti correttamente (UC3, UC4, UC9 supportati) ---");
    }
}