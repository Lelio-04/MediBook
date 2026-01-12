package it.unisa.medibook.service;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GestionePrenotazioniTest {

    @Mock
    private PrenotazioneRepository prenotazioneRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private PazienteRepository pazienteRepository;

    @InjectMocks
    private GestionePrenotazioni gestionePrenotazioni;

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
    @Test
    void nuovaPrenotazione_SlotOccupato() {
        // Setup: data futura ma slot già esistente nel mock
        LocalDate dataFutura = LocalDate.now().plusDays(5);
        LocalTime ora = LocalTime.of(11, 0);

        // Simuliamo che il medico sia già impegnato
        when(prenotazioneRepository.existsByMedicoIdAndDataAndOra(1, dataFutura, ora)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            gestionePrenotazioni.nuovaPrenotazione(1, 1, dataFutura, ora);
        });

        assertEquals("Errore: Orario non disponibile per questo medico.", exception.getMessage());
    }

    // TC_PRE_4: Prenotazione Corretta (Successo)
    @Test
    void nuovaPrenotazione_Successo() throws Exception {
        // Setup dati validi
        LocalDate dataValida = LocalDate.now().plusDays(10);
        LocalTime oraValida = LocalTime.of(15, 30);

        Paziente p = new Paziente();
        p.setId(1);
        Medico m = new Medico();
        m.setId(1);

        // Simuliamo il comportamento dei repository
        when(prenotazioneRepository.existsByMedicoIdAndDataAndOra(1, dataValida, oraValida)).thenReturn(false);
        when(pazienteRepository.findById(1)).thenReturn(Optional.of(p));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(m));

        // Mock del salvataggio: restituisce l'oggetto passato
        when(prenotazioneRepository.save(any(Prenotazione.class))).thenAnswer(i -> i.getArguments()[0]);

        // Esecuzione
        Prenotazione risultante = gestionePrenotazioni.nuovaPrenotazione(1, 1, dataValida, oraValida);

        // Oracolo
        assertNotNull(risultante);
        assertEquals("PRENOTATA", risultante.getStato());
        assertEquals(dataValida, risultante.getData());
    }

    // TC_VIS_1: Cambio stato in EFFETTUATA
    @Test
    void aggiornaStatoVisita_Effettuata() {
        Prenotazione p = new Prenotazione();
        p.setId(1);
        p.setStato("PRENOTATA");

        when(prenotazioneRepository.findById(1)).thenReturn(Optional.of(p));

        gestionePrenotazioni.aggiornaStatoVisita(1, "EFFETTUATA");

        assertEquals("EFFETTUATA", p.getStato());
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