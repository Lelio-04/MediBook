package it.unisa.medibook.config;

import it.unisa.medibook.model.*;
import it.unisa.medibook.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Questa classe viene eseguita automaticamente all'avvio di Spring Boot.
 * Serve a popolare il database con dati di prova se è vuoto.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PazienteRepository pazienteRepository;
    @Autowired
    private SegreteriaRepository segreteriaRepository;
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    @Autowired
    private UtenteRepository utenteRepository; // Serve per il controllo generale

    @Override
    public void run(String... args) throws Exception {
        // Controllo se esiste già qualche utente. Se sì, non faccio nulla per non duplicare.
        if (utenteRepository.count() == 0) {
            System.out.println("--- Inizializzazione Dati di Prova (Seeding) ---");

            // --- 1. CREAZIONE MEDICI ---
            Medico m1 = new Medico();
            m1.setEmail("rossi@medibook.it");
            m1.setPassword("password");
            m1.setRuolo("MEDICO");
            m1.setSpecializzazione("Cardiologia");
            m1.setNumeroAlbo("NUM-11111");

            Medico m2 = new Medico();
            m2.setEmail("bianchi@medibook.it");
            m2.setPassword("password");
            m2.setRuolo("MEDICO");
            m2.setSpecializzazione("Dermatologia");
            m2.setNumeroAlbo("NUM-22222");

            medicoRepository.save(m1);
            medicoRepository.save(m2);
            System.out.println("-> Medici inseriti");

            // --- 2. CREAZIONE PAZIENTI ---
            Paziente p1 = new Paziente();
            p1.setEmail("paziente1@gmail.com");
            p1.setPassword("password");
            p1.setRuolo("PAZIENTE");
            p1.setNome("Luca");
            p1.setCognome("Verdi");
            p1.setCodiceFiscale("VRDLC80A01H501Z"); // CF finto ma realistico
            p1.setIndirizzo("Via Roma 10, Salerno");
            p1.setTelefono("3331234567");

            Paziente p2 = new Paziente();
            p2.setEmail("paziente2@gmail.com");
            p2.setPassword("password");
            p2.setRuolo("PAZIENTE");
            p2.setNome("Maria");
            p2.setCognome("Neri");
            p2.setCodiceFiscale("NRIMRA85B02H501Q");
            p2.setIndirizzo("Corso Vittorio Emanuele, Salerno");
            p2.setTelefono("3339876543");

            pazienteRepository.save(p1);
            pazienteRepository.save(p2);
            System.out.println("-> Pazienti inseriti");

            // --- 3. CREAZIONE SEGRETERIA ---
            Segreteria s1 = new Segreteria();
            s1.setEmail("segreteria@medibook.it");
            s1.setPassword("admin");
            s1.setRuolo("SEGRETERIA");

            Segreteria s2 = new Segreteria();
            s2.setEmail("accettazione@medibook.it");
            s2.setPassword("admin");
            s2.setRuolo("SEGRETERIA");

            segreteriaRepository.save(s1);
            segreteriaRepository.save(s2);
            System.out.println("-> Segreteria inserita");

            // --- 4. CREAZIONE PRENOTAZIONI ---

            // Caso A: Visita futura (da gestire per la Segreteria)
            Prenotazione pren1 = new Prenotazione();
            pren1.setData(LocalDate.now().plusDays(2)); // Fra 2 giorni
            pren1.setOra(LocalTime.of(10, 30));
            pren1.setStato("DA_CONFERMARE");
            pren1.setMedico(m1); // Dott. Rossi
            pren1.setPaziente(p1); // Luca Verdi

            // Caso B: Visita passata o da refertare (per il Medico)
            Prenotazione pren2 = new Prenotazione();
            pren2.setData(LocalDate.now()); // Oggi
            pren2.setOra(LocalTime.of(16, 00));
            pren2.setStato("ATTIVA");
            pren2.setMedico(m2); // Dott. Bianchi
            pren2.setPaziente(p2); // Maria Neri

            prenotazioneRepository.save(pren1);
            prenotazioneRepository.save(pren2);
            System.out.println("-> Prenotazioni inserite");

            System.out.println("--- Seeding Completato con Successo! ---");
        }
    }
}