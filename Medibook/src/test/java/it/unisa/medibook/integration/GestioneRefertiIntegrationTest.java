package it.unisa.medibook.integration;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.modelStorage.RefertoRepository;
import it.unisa.medibook.service.GestioneReferti;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback automatico: ogni test parte con DB pulito
class GestioneRefertiIntegrationTest {

    @Autowired private GestioneReferti gestioneReferti;
    @Autowired private RefertoRepository refertoRepository;
    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private PazienteRepository pazienteRepository;

    private Medico medicoTest;
    private Paziente pazienteTest;

    @BeforeEach
    void setUp() {
        // 1. Pulizia preventiva
        refertoRepository.deleteAll();
        prenotazioneRepository.deleteAll();
        medicoRepository.deleteAll();
        pazienteRepository.deleteAll();

        // 2. Creazione attori necessari per i vincoli di Foreign Key
        Medico m = new Medico();
        m.setNome("Dr"); m.setCognome("House");
        m.setEmail("house@test.it"); m.setPassword("pw");
        m.setRuolo("MEDICO"); m.setSpecializzazione("Diagnostica");
        m.setNumeroAlbo("HSE123");
        medicoTest = medicoRepository.save(m);

        Paziente p = new Paziente();
        p.setNome("Paziente"); p.setCognome("Zero");
        p.setEmail("zero@test.it"); p.setPassword("pw");
        p.setRuolo("PAZIENTE"); p.setCodiceFiscale("PZNZER80A01H501K");
        pazienteTest = pazienteRepository.save(p);
    }

    // TC_REF_1: Stato visita non valido (es. PRENOTATA)
    @Test
    void salvaNuovoReferto_StatoNonValido() {
        // 1. SETUP: Creiamo visita in stato "PRENOTATA" (non refertabile)
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoTest);
        p.setPaziente(pazienteTest);
        p.setData(LocalDate.now());
        p.setOra(LocalTime.of(10, 0));
        p.setStato("PRENOTATA");
        p = prenotazioneRepository.save(p);

        // 2. AZIONE & ORACOLO
        // Il service deve lanciare eccezione perché non è EFFETTUATA
        Integer idVisita = p.getId();
        Exception ex = assertThrows(Exception.class, () -> {
            gestioneReferti.salvaNuovoReferto(idVisita, "Contenuto valido ma stato errato");
        });

        // 3. VERIFICA DB: Nessun referto deve essere stato creato
        assertEquals(0, refertoRepository.count(), "Non dovrebbe esistere alcun referto nel DB");
    }

    // TC_REF_2: Referto Vuoto
    @Test
    void salvaNuovoReferto_ContenutoVuoto() {
        // 1. SETUP: Visita in stato "EFFETTUATA" (pronta per referto)
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoTest);
        p.setPaziente(pazienteTest);
        p.setData(LocalDate.now());
        p.setOra(LocalTime.of(11, 0));
        p.setStato("EFFETTUATA");
        p = prenotazioneRepository.save(p);

        // 2. AZIONE & ORACOLO
        // Tentiamo di salvare un referto vuoto
        Integer idVisita = p.getId();
        Exception ex = assertThrows(Exception.class, () -> {
            gestioneReferti.salvaNuovoReferto(idVisita, ""); // Stringa vuota
        });

        // 3. VERIFICA DB: Nessun referto creato
        assertEquals(0, refertoRepository.count());
    }

    // TC_REF_3: Salvataggio Corretto (Successo)
    @Test
    void salvaNuovoReferto_Successo() throws Exception {
        // 1. SETUP: Visita "EFFETTUATA"
        Prenotazione p = new Prenotazione();
        p.setMedico(medicoTest);
        p.setPaziente(pazienteTest);
        p.setData(LocalDate.now());
        p.setOra(LocalTime.of(12, 0));
        p.setStato("EFFETTUATA");
        p = prenotazioneRepository.save(p);

        // 2. AZIONE
        String testoReferto = "Esame obiettivo negativo. Tutto ok.";
        gestioneReferti.salvaNuovoReferto(p.getId(), testoReferto);

        // 3. ORACOLO (Post-condizioni)

        // A. Verifica che il referto esista nel DB
        List<Referto> referti = refertoRepository.findAll();
        assertEquals(1, referti.size(), "Deve esserci 1 referto salvato");
        assertEquals(testoReferto, referti.get(0).getContenuto());
        assertEquals(p.getId(), referti.get(0).getPrenotazione().getId());

        // B. Verifica che lo stato della visita sia cambiato in CONCLUSA
        Prenotazione pAggiornata = prenotazioneRepository.findById(p.getId()).orElseThrow();
        assertEquals("CONCLUSA", pAggiornata.getStato(),
                "Lo stato della visita deve passare a CONCLUSA dopo il referto");
    }
}