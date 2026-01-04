package it.unisa.medibook.business;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.storage.MedicoRepository;
import it.unisa.medibook.storage.PazienteRepository;
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

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PazienteRepository pazienteRepository;

    /**
     * 1. Funzionalità Segreteria: Modifica Data e Ora.
     * Include validazione: No date passate, No slot occupati.
     */
    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra) throws Exception {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();

            // --- CONTROLLO 1: Data nel passato ---
            if (nuovaData.isBefore(LocalDate.now())) {
                throw new Exception("Errore: Non è possibile spostare una visita nel passato.");
            }

            // --- CONTROLLO 2: Slot Occupato ---
            // Verifica se il medico è occupato, ESCLUDENDO la prenotazione attuale (altrimenti andrebbe in conflitto con se stessa)
            boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(
                    p.getMedico().getId(),
                    nuovaData,
                    nuovaOra,
                    id
            );

            if (slotOccupato) {
                throw new Exception("Errore: Orario non disponibile. Il medico ha già una visita in questo orario.");
            }

            // Se passa i controlli, salva
            p.setData(nuovaData);
            p.setOra(nuovaOra);
            return prenotazioneRepository.save(p);
        }
        throw new Exception("Prenotazione non trovata");
    }

    /**
     * 2. Funzionalità Medico: Modifica Stato Visita (EFFETTUATA / ANNULLATA).
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
     */
    public List<Prenotazione> visualizzaVisiteMedico(Integer medicoId) {
        return prenotazioneRepository.findByMedicoId(medicoId);
    }

    /**
     * 4. Funzionalità Paziente: Visualizza storico visite.
     */
    public List<Prenotazione> visualizzaVisitePaziente(Integer pazienteId) {
        return prenotazioneRepository.findByPazienteId(pazienteId);
    }

    /**
     * 5. Funzionalità Paziente: Nuova Prenotazione.
     * Crea una nuova visita controllando la disponibilità.
     */
    public Prenotazione nuovaPrenotazione(Integer pazienteId, Integer medicoId, LocalDate data, LocalTime ora) throws Exception {

        // --- CONTROLLO 1: Data nel passato ---
        if (data.isBefore(LocalDate.now())) {
            throw new Exception("Errore: Non puoi prenotare nel passato!");
        }

        // --- CONTROLLO 2: Slot Occupato ---
        // Qui usiamo il metodo senza "IdNot" perché è una prenotazione nuova
        boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOra(medicoId, data, ora);

        if (slotOccupato) {
            throw new Exception("Errore: Orario non disponibile per questo medico.");
        }

        // Recupero le entità
        Optional<Paziente> pazienteOpt = pazienteRepository.findById(pazienteId);
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);

        if (pazienteOpt.isPresent() && medicoOpt.isPresent()) {
            Prenotazione p = new Prenotazione();
            p.setData(data);
            p.setOra(ora);
            p.setStato("DA_CONFERMARE");
            p.setPaziente(pazienteOpt.get());
            p.setMedico(medicoOpt.get());

            return prenotazioneRepository.save(p);
        } else {
            throw new Exception("Errore: Utente o Medico non trovato.");
        }
    }

    /**
     * Utility: Restituisce la lista di tutti i medici (per il menu a tendina della prenotazione).
     */
    public List<Medico> dammiTuttiIMedici() {
        return medicoRepository.findAll();
    }
}