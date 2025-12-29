package it.unisa.medibook.business;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.storage.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class GestionePrenotazioni {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    /**
     * 1. Funzionalità Segreteria: Modifica Data e Ora.
     * Permette alla segreteria di spostare un appuntamento.
     */
    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra) {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();
            p.setData(nuovaData);
            p.setOra(nuovaOra);
            // Salva le modifiche nel database
            return prenotazioneRepository.save(p);
        }
        return null; // O gestire l'errore se l'ID non esiste
    }

    /**
     * 2. Funzionalità Medico: Modifica Stato Visita.
     * Permette al medico di segnare una visita come "EFFETTUATA" o "ANNULLATA".
     * Questo riflette l'uso dell'Observer menzionato nell'ODD per i cambi di stato[cite: 75].
     */
    public Prenotazione aggiornaStatoVisita(Integer id, String nuovoStato) {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();
            p.setStato(nuovoStato);
            return prenotazioneRepository.save(p);
        }
        return null;
    }

    /**
     * 3. Funzionalità Medico: Visione Visite.
     * Restituisce la lista delle prenotazioni di un medico specifico.
     * Corrisponde a doRetrieveByMedico[cite: 137].
     */
    public List<Prenotazione> visualizzaVisiteMedico(Integer medicoId) {
        return prenotazioneRepository.findByMedicoId(medicoId);
    }
}