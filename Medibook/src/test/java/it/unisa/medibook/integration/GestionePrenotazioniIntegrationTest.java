package it.unisa.medibook.integration;

import it.unisa.medibook.config.DatabaseSeeder;
import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.service.EmailService;
import it.unisa.medibook.service.GestionePrenotazioni;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GestionePrenotazioniIntegrationTest {

    @Autowired private GestionePrenotazioni gestionePrenotazioni;
    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private PazienteRepository pazienteRepository;
    @Autowired private EmailService emailService;

    private Medico medicoReale;
    private Paziente pazienteReale;

    @BeforeEach
    void setUp() {
        // 1. Pulizia preventiva
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
        // Setup: L'ID 9999 non esiste nel mock
        when(prenotazioneRepository.findById(9999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.modificaPrenotazione(9999, LocalDate.now().plusDays(5), LocalTime.of(10, 0), "PRENOTATA");
        });

        // Oracolo
        assertEquals("Prenotazione non trovata", exception.getMessage());
    }

    // TC_MOD_2: Slot Orario Occupato
    @Test
    void modificaPrenotazione_SlotOccupato() {
        // Setup: Esiste la prenotazione 101 per il Medico 1
        Medico m = new Medico(); m.setId(1);
        Prenotazione p = new Prenotazione();
        p.setId(101); p.setMedico(m);
        p.setData(LocalDate.now().plusDays(1)); p.setOra(LocalTime.of(9, 0));

        LocalDate nuovaData = LocalDate.of(2026, 5, 22);
        LocalTime nuovaOra = LocalTime.of(15, 0);

        when(prenotazioneRepository.findById(101)).thenReturn(Optional.of(p));
        // Simuliamo che lo slot sia occupato da un'ALTRA prenotazione (IdNot 101)
        when(prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(1, nuovaData, nuovaOra, 101)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.modificaPrenotazione(101, nuovaData, nuovaOra, "PRENOTATA");
        });

        // Oracolo
        assertEquals("Errore: Orario non disponibile.", exception.getMessage());
    }

    // TC_MOD_4: Modifica con Successo
    @Test
    void modificaPrenotazione_Successo() throws Exception {
        // Setup
        Medico m = new Medico(); m.setId(1);
        Prenotazione p = new Prenotazione();
        p.setId(101); p.setMedico(m);
        p.setData(LocalDate.now().plusDays(1));
        p.setOra(LocalTime.of(9, 0));

        LocalDate nuovaData = LocalDate.of(2026, 5, 30);
        LocalTime nuovaOra = LocalTime.of(11, 0);

        when(prenotazioneRepository.findById(101)).thenReturn(Optional.of(p));
        when(prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(1, nuovaData, nuovaOra, 101)).thenReturn(false);
        when(prenotazioneRepository.save(any(Prenotazione.class))).thenAnswer(i -> i.getArguments()[0]);

        // Execution
        Prenotazione aggiornata = gestionePrenotazioni.modificaPrenotazione(101, nuovaData, nuovaOra, "PRENOTATA");

        // Oracolo
        assertNotNull(aggiornata);
        assertEquals(nuovaData, aggiornata.getData());
        assertEquals(nuovaOra, aggiornata.getOra());
    }
    // --- UC6: GESTIONE VISITA (Aggiornamento Stato) ---

    // TC_VIS_1: Esecuzione Visita (Da PRENOTATA a EFFETTUATA)
    @Test
    void aggiornaStatoVisita_Esecuzione() {
        // Setup: Visita 501 in stato PRENOTATA
        Prenotazione p = new Prenotazione();
        p.setId(501);
        p.setStato("PRENOTATA");

        when(prenotazioneRepository.findById(501)).thenReturn(Optional.of(p));

        // Execution
        gestionePrenotazioni.aggiornaStatoVisita(501, "EFFETTUATA");

        // Oracolo: Verifichiamo che lo stato sia cambiato e il repository abbia salvato
        assertEquals("EFFETTUATA", p.getStato(), "TC_VIS_1 Fallito: Lo stato non è passato a EFFETTUATA");
        verify(prenotazioneRepository, times(1)).save(p);
    }

    // TC_VIS_2: Annullamento Visita
    @Test
    void aggiornaStatoVisita_Annullamento() {
        // Setup: Visita 502 in stato PRENOTATA
        Prenotazione p = new Prenotazione();
        p.setId(502);
        p.setStato("PRENOTATA");

        when(prenotazioneRepository.findById(502)).thenReturn(Optional.of(p));

        // Execution
        gestionePrenotazioni.aggiornaStatoVisita(502, "ANNULLATA");

        // Oracolo
        assertEquals("ANNULLATA", p.getStato(), "TC_VIS_2 Fallito: Lo stato non è passato a ANNULLATA");
        verify(prenotazioneRepository, times(1)).save(p);
    }

    // TC_VIS_3: Modifica Visita già processata (Vincolo di integrità)
    @Test
    void aggiornaStatoVisita_GiaConclusa() {
        // Setup: Visita 600 già CONCLUSA
        Prenotazione p = new Prenotazione();
        p.setId(600);
        p.setStato("CONCLUSA");

        when(prenotazioneRepository.findById(600)).thenReturn(Optional.of(p));

        // Se nel tuo Service hai aggiunto un controllo di guardia per impedire la modifica:
        // gestionePrenotazioni.aggiornaStatoVisita(600, "EFFETTUATA");

        // Se il service non ha l'if di controllo, lo stato cambierebbe.
        // Per rispettare l'oracolo del TCS, assicurati che il service gestisca il blocco,
        // altrimenti il test fallirà evidenziando un bug logico!

        // Esempio di verifica se il metodo è stato effettivamente chiamato:
        gestionePrenotazioni.aggiornaStatoVisita(600, "EFFETTUATA");

        // Se il vincolo è rispettato, lo stato dovrebbe essere rimasto CONCLUSA
        assertNotEquals("EFFETTUATA", p.getStato(), "TC_VIS_3 Fallito: Il sistema ha permesso di modificare una visita già CONCLUSA");
    }
}