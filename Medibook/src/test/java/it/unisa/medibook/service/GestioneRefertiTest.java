package it.unisa.medibook.service;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.modelStorage.RefertoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneRefertiTest {

    @Mock
    private RefertoRepository refertoRepository;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @InjectMocks
    private GestioneReferti gestioneReferti;

    // TC_REF_1: Stato visita non valido (es. PRENOTATA invece di EFFETTUATA)
    @Test
    void salvaNuovoReferto_StatoNonValido() {
        // Setup: Prenotazione 701 ancora in stato PRENOTATA
        Prenotazione p = new Prenotazione();
        p.setId(701);
        p.setStato("PRENOTATA");

        when(prenotazioneRepository.findById(701)).thenReturn(Optional.of(p));

        // Oracolo: Il service deve impedire l'operazione (Assicurati di aver aggiunto l'if nel Service!)
        Exception exception = assertThrows(Exception.class, () -> {
            gestioneReferti.salvaNuovoReferto(701, "Contenuto valido");
        });

        // Verifichiamo che non sia stata salvata la modifica
        verify(refertoRepository, never()).save(any(Referto.class));
    }

    // TC_REF_2: Referto Vuoto
    @Test
    void salvaNuovoReferto_ContenutoVuoto() {
        // Setup: Prenotazione 702 in stato corretto (EFFETTUATA)
        Prenotazione p = new Prenotazione();
        p.setId(702);
        p.setStato("EFFETTUATA");

        when(prenotazioneRepository.findById(702)).thenReturn(Optional.of(p));

        // Oracolo: Il sistema segnala che il campo è obbligatorio
        Exception exception = assertThrows(Exception.class, () -> {
            gestioneReferti.salvaNuovoReferto(702, ""); // Stringa vuota
        });

        verify(refertoRepository, never()).save(any(Referto.class));
    }

    // TC_REF_3: Salvataggio Corretto (Successo)
    @Test
    void salvaNuovoReferto_Successo() throws Exception {
        // Setup: Prenotazione 703 pronta per referto
        Prenotazione p = new Prenotazione();
        p.setId(703);
        p.setStato("EFFETTUATA");

        when(prenotazioneRepository.findById(703)).thenReturn(Optional.of(p));

        // Execution
        gestioneReferti.salvaNuovoReferto(703, "Esame obiettivo negativo. Si consiglia controllo.");

        // ORACOLO (Post-condizioni OCL):
        // 1. Verifichiamo che il referto sia stato salvato
        verify(refertoRepository, times(1)).save(any(Referto.class));

        // 2. Verifichiamo che la visita sia passata a CONCLUSA
        assertEquals("CONCLUSA", p.getStato(), "TC_REF_3 Fallito: Lo stato della visita non è passato a CONCLUSA");
        verify(prenotazioneRepository, times(1)).save(p);
    }
}