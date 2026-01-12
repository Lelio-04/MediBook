package it.unisa.medibook.integration;

import it.unisa.medibook.config.DatabaseSeeder;
import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.modelStorage.RefertoRepository;
import it.unisa.medibook.service.EmailService;
import it.unisa.medibook.service.GestionePrenotazioni;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GestionePrenotazioniIntegrationTest {

    @Autowired private GestionePrenotazioni gestionePrenotazioni;
    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private PazienteRepository pazienteRepository;
    @Autowired private RefertoRepository refertoRepository;
    private Medico medicoReale;
    private Paziente pazienteReale;

    @BeforeEach
    void setUp() {
        // 1. Pulizia preventiva
        refertoRepository.deleteAll();
        prenotazioneRepository.deleteAll();
        medicoRepository.deleteAll();
        pazienteRepository.deleteAll();

        // 2. Setup Dati Base (Attori)
        // Creiamo e SALVIAMO i dati nel DB H2. Niente "when(...)".

        medicoReale = new Medico();
        medicoReale.setNome("Mario");
        medicoReale.setCognome("Rossi");
        medicoReale.setEmail("mario.rossi@test.it");
        medicoReale.setPassword("password");
        medicoReale.setRuolo("MEDICO");
        medicoReale.setSpecializzazione("Cardiologia");
        medicoReale.setNumeroAlbo("ALBO123"); // Campo obbligatorio!
        medicoReale = medicoRepository.save(medicoReale); // <--- Salvataggio vero

        pazienteReale = new Paziente();
        pazienteReale.setNome("Luigi");
        pazienteReale.setCognome("Verdi");
        pazienteReale.setEmail("luigi.verdi@test.it");
        pazienteReale.setCodiceFiscale("VRDLGU80A01H501U");
        pazienteReale.setRuolo("PAZIENTE");
        pazienteReale.setPassword("password");
        pazienteReale = pazienteRepository.save(pazienteReale); // <--- Salvataggio vero
    }

    @Test
    void nuovaPrenotazione_SlotOccupato() {
        // --- 1. SETUP SPECIFICO DEL TEST ---
        LocalDate data = LocalDate.now().plusDays(5);
        LocalTime ora = LocalTime.of(11, 0);

        // Per simulare lo slot occupato "senza mock", dobbiamo
        // inserire FISICAMENTE una prenotazione nel DB che disturbi.
        Prenotazione occupante = new Prenotazione();
        occupante.setMedico(medicoReale);   // Usiamo il medico creato nel setUp
        occupante.setPaziente(pazienteReale); // Usiamo il paziente creato nel setUp
        occupante.setData(data);
        occupante.setOra(ora);
        occupante.setStato("PRENOTATA");

        prenotazioneRepository.save(occupante); // <--- ECCO IL "MOCK" REALE: Scriviamo nel DB

        // --- 2. AZIONE (Tentativo di sovrascrittura) ---
        // Proviamo a prenotare lo stesso medico, stessa ora
        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.nuovaPrenotazione(pazienteReale.getId(), medicoReale.getId(), data, ora);
        });

        // --- 3. ORACOLO ---
        String messaggio = exception.getMessage().toLowerCase();
        assertTrue(messaggio.contains("non disponibile") || messaggio.contains("occupato"),
                "Messaggio atteso errore slot occupato. Trovato: " + exception.getMessage());
    }
    // TC_PRE_1: Data nel passato
    @Test
    void nuovaPrenotazione_DataPassata() {
        // Setup: data antecedente a oggi
        LocalDate dataPassata = LocalDate.now().minusDays(1);
        LocalTime ora = LocalTime.of(10, 30);

        // Oracolo: il service deve lanciare l'eccezione specifica
        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.nuovaPrenotazione(1, 1, dataPassata, ora);
        });

        assertEquals("Errore: Non puoi prenotare nel passato!", exception.getMessage());
    }

    // TC_PRE_2: Slot occupato

    // TC_PRE_4: Prenotazione Corretta (Successo)
    @Test
    void testNuovaPrenotazione_Successo() throws Exception {
        LocalDate data = LocalDate.now().plusDays(10);
        LocalTime ora = LocalTime.of(10, 0);

        // AZIONE: Chiamata al servizio reale -> Scrive su DB H2
        gestionePrenotazioni.nuovaPrenotazione(pazienteReale.getId(), medicoReale.getId(), data, ora);

        // ORACOLO: Verifica stato DB tramite Repository
        List<Prenotazione> lista = prenotazioneRepository.findAll();
        assertEquals(1, lista.size(), "Dovrebbe esserci esattamente una prenotazione nel DB");
        assertEquals("PRENOTATA", lista.get(0).getStato());
        assertEquals(medicoReale.getId(), lista.get(0).getMedico().getId());
    }

    // TC_VIS_1: Cambio stato in EFFETTUATA
    @Test
    void aggiornaStatoVisita_Effettuata() {
        // 1. SETUP: Creiamo una prenotazione reale nel DB
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoReale);    // Usiamo il medico creato nel setUp()
        p.setPaziente(pazienteReale); // Usiamo il paziente creato nel setUp()
        p.setData(LocalDate.now());
        p.setOra(LocalTime.of(12, 0));
        p.setStato("PRENOTATA");

        // Salviamo e aggiorniamo l'oggetto 'p' con l'ID generato dal DB
        p = prenotazioneRepository.save(p);

        // 2. AZIONE: Chiamiamo il servizio reale usando l'ID vero
        gestionePrenotazioni.aggiornaStatoVisita(p.getId(), "EFFETTUATA");

        // 3. ORACOLO: Rileggiamo dal Database per confermare la persistenza
        // (Non controlliamo solo l'oggetto in memoria, ma proprio il record su DB)
        Prenotazione prenotazioneAggiornata = prenotazioneRepository.findById(p.getId()).orElseThrow();

        assertEquals("EFFETTUATA", prenotazioneAggiornata.getStato(),
                "Lo stato nel database dovrebbe essere aggiornato a EFFETTUATA");
    }
    // --- UC4: MODIFICA PRENOTAZIONE ---

    // TC_MOD_1: ID Prenotazione Errato
    @Test
    void modificaPrenotazione_IdInesistente() {
        // 1. SETUP: Non dobbiamo salvare nulla.
        // Scegliamo un ID arbitrario che siamo sicuri non esista nel DB H2
        Integer idInesistente = 999999;

        // 2. AZIONE & ORACOLO
        // Il service cercherà davvero questo ID nel DB, non lo troverà e lancerà l'eccezione
        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.modificaPrenotazione(
                    idInesistente,
                    LocalDate.now().plusDays(5),
                    LocalTime.of(10, 0),
                    "PRENOTATA"
            );
        });

        // 3. VERIFICA MESSAGGIO
        assertEquals("Prenotazione non trovata", exception.getMessage());
    }

    // TC_MOD_2: Slot Orario Occupato
    @Test
    void modificaPrenotazione_SlotOccupato() {
        // 1. SETUP: Definiamo data e orari
        LocalDate dataTarget = LocalDate.now().plusDays(5);
        LocalTime oraAttuale = LocalTime.of(9, 0);
        LocalTime oraOccupata = LocalTime.of(11, 0); // L'orario dove vogliamo spostarci

        // 2. Creiamo la prenotazione che VOGLIAMO MODIFICARE (Prenotazione A)
        Prenotazione pDaModificare = new Prenotazione();
        pDaModificare.setMedico(medicoReale);
        pDaModificare.setPaziente(pazienteReale);
        pDaModificare.setData(dataTarget);
        pDaModificare.setOra(oraAttuale); // Ore 09:00
        pDaModificare.setStato("PRENOTATA");
        pDaModificare = prenotazioneRepository.save(pDaModificare); // Otteniamo l'ID reale

        // 3. Creiamo l'OSTACOLO (Prenotazione B)
        // Questa prenotazione occupa lo slot delle 11:00 per lo stesso medico
        Prenotazione pOstacolo = new Prenotazione();
        pOstacolo.setMedico(medicoReale);
        pOstacolo.setPaziente(pazienteReale);
        pOstacolo.setData(dataTarget);
        pOstacolo.setOra(oraOccupata); // Ore 11:00 (BLOCCATO)
        pOstacolo.setStato("PRENOTATA");
        prenotazioneRepository.save(pOstacolo);

        // 4. AZIONE & ORACOLO
        // Tentiamo di spostare la prenotazione A alle ore 11:00 (che è occupata da B)
        // Dobbiamo usare una variabile final o effettivamente final per la lambda
        Integer idDaModificare = pDaModificare.getId();

        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.modificaPrenotazione(
                    idDaModificare,
                    dataTarget,
                    oraOccupata, // Tentativo di spostamento alle 11:00
                    "PRENOTATA"
            );
        });

        // 5. VERIFICA MESSAGGIO
        String msg = exception.getMessage().toLowerCase();
        assertTrue(msg.contains("non disponibile") || msg.contains("occupato"),
                "Dovrebbe segnalare che l'orario è occupato. Messaggio trovato: " + exception.getMessage());
    }

    // TC_MOD_4: Modifica con Successo
    @Test
    void modificaPrenotazione_Successo() throws Exception {
        // 1. SETUP: Creiamo la prenotazione nello stato INIZIALE
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoReale);     // Usiamo il medico reale del setUp
        p.setPaziente(pazienteReale); // Usiamo il paziente reale del setUp
        p.setData(LocalDate.now().plusDays(1)); // Domani
        p.setOra(LocalTime.of(9, 0));           // Ore 09:00
        p.setStato("PRENOTATA");

        // Salviamo nel DB per ottenere l'ID reale
        p = prenotazioneRepository.save(p);

        // 2. DEFINIZIONE NUOVI DATI
        LocalDate nuovaData = LocalDate.now().plusDays(10); // Spostiamo tra 10 giorni
        LocalTime nuovaOra = LocalTime.of(11, 0);           // Spostiamo alle 11:00

        // 3. AZIONE: Chiamiamo il servizio reale
        Prenotazione aggiornata = gestionePrenotazioni.modificaPrenotazione(
                p.getId(),
                nuovaData,
                nuovaOra,
                "PRENOTATA"
        );

        // 4. ORACOLO: Verifica doppia

        // A. Verifica sull'oggetto restituito
        assertNotNull(aggiornata);
        assertEquals(nuovaData, aggiornata.getData());
        assertEquals(nuovaOra, aggiornata.getOra());

        // B. Verifica sul DATABASE (La prova del nove)
        // Rileggiamo il record fresco dal DB per essere sicuri che l'UPDATE SQL sia partito
        Prenotazione checkDb = prenotazioneRepository.findById(p.getId()).orElseThrow();

        assertEquals(nuovaData, checkDb.getData(), "La data nel DB deve essere aggiornata");
        assertEquals(nuovaOra, checkDb.getOra(), "L'ora nel DB deve essere aggiornata");
    }
    // --- UC6: GESTIONE VISITA (Aggiornamento Stato) ---

    // TC_VIS_1: Esecuzione Visita (Da PRENOTATA a EFFETTUATA)
    @Test
    void aggiornaStatoVisita_Esecuzione() {
        // 1. SETUP: Creiamo una visita reale in stato PRENOTATA
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoReale);     // Usiamo il medico creato nel setUp
        p.setPaziente(pazienteReale); // Usiamo il paziente creato nel setUp
        p.setData(LocalDate.now());  // Data di oggi (visita in corso)
        p.setOra(LocalTime.of(10, 0));
        p.setStato("PRENOTATA");

        // Salviamo nel DB H2 per ottenere un ID reale
        p = prenotazioneRepository.save(p);

        // 2. AZIONE: Il medico segna la visita come effettuata
        gestionePrenotazioni.aggiornaStatoVisita(p.getId(), "EFFETTUATA");

        // 3. ORACOLO: Rileggiamo dal DB per confermare il cambio stato
        Prenotazione check = prenotazioneRepository.findById(p.getId()).orElseThrow();

        assertEquals("EFFETTUATA", check.getStato(),
                "Lo stato nel database dovrebbe essere aggiornato a EFFETTUATA");
    }

    // TC_VIS_2: Annullamento Visita
    @Test
    void aggiornaStatoVisita_Annullamento() {
        // 1. SETUP: Creiamo una visita reale in stato PRENOTATA
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoReale);     // Usiamo il medico creato nel setUp
        p.setPaziente(pazienteReale); // Usiamo il paziente creato nel setUp
        p.setData(LocalDate.now().plusDays(2)); // Una data futura ha senso per un annullamento
        p.setOra(LocalTime.of(16, 0));
        p.setStato("PRENOTATA");

        // La salviamo nel DB H2 per ottenere un ID reale
        p = prenotazioneRepository.save(p);

        // 2. AZIONE: Chiamiamo il servizio reale
        gestionePrenotazioni.aggiornaStatoVisita(p.getId(), "ANNULLATA");

        // 3. ORACOLO: Rileggiamo dal DB per confermare che lo stato sia cambiato
        Prenotazione check = prenotazioneRepository.findById(p.getId()).orElseThrow();

        assertEquals("ANNULLATA", check.getStato(),
                "Lo stato nel database dovrebbe essere passato ad ANNULLATA");
    }

    // TC_VIS_3: Modifica Visita già processata (Vincolo di integrità)
    @Test
    void aggiornaStatoVisita_GiaConclusa() {
        // 1. SETUP: Creiamo una visita già in stato terminale (CONCLUSA)
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoReale);
        p.setPaziente(pazienteReale);
        p.setData(LocalDate.now().minusDays(5)); // Mettiamo una data passata per realismo
        p.setOra(LocalTime.of(9, 0));
        p.setStato("CONCLUSA");

        // Salvataggio nel DB reale H2
        p = prenotazioneRepository.save(p);

        // 2. AZIONE: Tentiamo di forzare il cambio stato a "EFFETTUATA"
        // Avvolgiamo in try-catch: se il tuo service lancia un'eccezione (es. "Non modificabile"),
        // va bene lo stesso, l'importante è che il dato nel DB non cambi.
        try {
            gestionePrenotazioni.aggiornaStatoVisita(p.getId(), "EFFETTUATA");
        } catch (Exception e) {
            // Ignoriamo l'errore java, ci interessa verificare la persistenza dei dati
        }

        // 3. ORACOLO: Rileggiamo dal DB per vedere se lo stato è cambiato
        Prenotazione check = prenotazioneRepository.findById(p.getId()).orElseThrow();

        // Se la logica è corretta, lo stato deve essere RIMASTO "CONCLUSA"
        assertEquals("CONCLUSA", check.getStato(),
                "Errore: Il sistema ha permesso di modificare una visita che era già CONCLUSA");
    }
}