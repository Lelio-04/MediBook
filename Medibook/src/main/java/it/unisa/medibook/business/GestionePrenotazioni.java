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
            * Implementa le pre-condizioni dell'ODD:
            * - La data non può essere nel passato (TC_MOD_1).
            * - Lo slot non deve essere occupato (TC_MOD_2).
            */
    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra) throws Exception {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();

            // --- CONTROLLO 1: Data nel passato ---
            // ODD Invariante: una prenotazione non può essere spostata nel passato
            if (nuovaData.isBefore(LocalDate.now())) {
                throw new Exception("Errore: Non è possibile spostare una visita nel passato.");
            }

            // --- CONTROLLO 2: Slot Occupato ---
            // Verifica nel DB se il medico ha già una visita in quell'orario
            boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(
                    p.getMedico().getId(), // ID del medico della prenotazione
                    nuovaData,
                    nuovaOra,
                    id // Escludiamo l'ID della prenotazione corrente
            );

            if (slotOccupato) {
                throw new Exception("Errore: Orario non disponibile. Lo slot selezionato è già occupato.");
            }

            // Se passa i controlli, salva
            p.setData(nuovaData);
            p.setOra(nuovaOra);
            return prenotazioneRepository.save(p);
        }
        return null; // O throw new Exception("Prenotazione non trovata");
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