package it.unisa.medibook.config;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.storage.MedicoRepository;
import it.unisa.medibook.storage.PazienteRepository;
import it.unisa.medibook.storage.PrenotazioneRepository;
import it.unisa.medibook.storage.UtenteRepository;
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

    @Override
    public void run(String... args) throws Exception {
        // Se ci sono già utenti, non fare nulla (evita duplicati al riavvio)
        if (utenteRepository.count() > 0) {
            return;
        }

        System.out.println("--- Inizializzazione Dati di Prova (Seeding) ---");

        // 1. CREAZIONE MEDICO
        Medico medico = new Medico();
        medico.setEmail("rossi@medibook.it");
        medico.setPassword("password");
        medico.setRuolo("MEDICO");
        // NUOVI CAMPI OBBLIGATORI
        medico.setNome("Mario");
        medico.setCognome("Rossi");
        medico.setSpecializzazione("Cardiologia");
        medico.setNumeroAlbo("12345");

        medicoRepository.save(medico);

        // 2. CREAZIONE SEGRETERIA
        Utente segreteria = new Utente();
        segreteria.setEmail("segreteria@medibook.it");
        segreteria.setPassword("admin");
        segreteria.setRuolo("SEGRETERIA");
        utenteRepository.save(segreteria);

        // 3. CREAZIONE PAZIENTE
        Paziente paziente = new Paziente();
        paziente.setEmail("paziente1@gmail.com");
        paziente.setPassword("password");
        paziente.setRuolo("PAZIENTE");
        paziente.setNome("Luca");
        paziente.setCognome("Verdi");
        paziente.setCodiceFiscale("VRDLCU80A01H501U");
        paziente.setTelefono("3331234567");
        pazienteRepository.save(paziente);

        // 4. CREAZIONE PRENOTAZIONE DI PROVA
        Prenotazione p1 = new Prenotazione();
        p1.setMedico(medico);
        p1.setPaziente(paziente);
        p1.setData(LocalDate.now().plusDays(1)); // Domani
        p1.setOra(LocalTime.of(10, 0));
        p1.setStato("DA_CONFERMARE");
        prenotazioneRepository.save(p1);

        System.out.println("--- Dati inseriti correttamente ---");
    }
}