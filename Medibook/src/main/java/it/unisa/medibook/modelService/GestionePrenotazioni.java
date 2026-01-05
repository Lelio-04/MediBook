package it.unisa.medibook.modelService;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
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

    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra) throws Exception {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();

            if (nuovaData.isBefore(LocalDate.now())) {
                throw new Exception("Errore: Non è possibile spostare una visita nel passato.");
            }

            boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(
                    p.getMedico().getId(),
                    nuovaData,
                    nuovaOra,
                    id
            );

            if (slotOccupato) {
                throw new Exception("Errore: Orario non disponibile. Il medico ha già una visita in questo orario.");
            }

            p.setData(nuovaData);
            p.setOra(nuovaOra);
            return prenotazioneRepository.save(p);
        }
        throw new Exception("Prenotazione non trovata");
    }

    public Prenotazione aggiornaStatoVisita(Integer id, String nuovoStato) {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();
            p.setStato(nuovoStato);
            return prenotazioneRepository.save(p);
        }
        return null;
    }

    public List<Prenotazione> visualizzaVisiteMedico(Integer medicoId) {
        return prenotazioneRepository.findByMedicoId(medicoId);
    }

    public List<Prenotazione> visualizzaVisitePaziente(Integer pazienteId) {
        return prenotazioneRepository.findByPazienteId(pazienteId);
    }

    public Prenotazione nuovaPrenotazione(Integer pazienteId, Integer medicoId, LocalDate data, LocalTime ora) throws Exception {

        if (data.isBefore(LocalDate.now())) {
            throw new Exception("Errore: Non puoi prenotare nel passato!");
        }

        boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOra(medicoId, data, ora);

        if (slotOccupato) {
            throw new Exception("Errore: Orario non disponibile per questo medico.");
        }

        Optional<Paziente> pazienteOpt = pazienteRepository.findById(pazienteId);
        Optional<Medico> medicoOpt = medicoRepository.findById(Long.valueOf(medicoId));

        if (pazienteOpt.isPresent() && medicoOpt.isPresent()) {
            Prenotazione p = new Prenotazione();
            p.setData(data);
            p.setOra(ora);
            p.setStato("PRENOTATA");
            p.setPaziente(pazienteOpt.get());
            p.setMedico(medicoOpt.get());

            return prenotazioneRepository.save(p);
        } else {
            throw new Exception("Errore: Utente o Medico non trovato.");
        }
    }

    public List<Medico> dammiTuttiIMedici() {
        return medicoRepository.findAll();
    }

    // *** NUOVO METODO AGGIUNTO ***
    public Medico getMedicoById(Integer id) {
        // Usa Long.valueOf se l'ID nel DB è Long, altrimenti toglilo
        return medicoRepository.findById(Long.valueOf(id)).orElse(null);
    }
    // *** AGGIUNGI QUESTO METODO IN FONDO ALLA CLASSE GestionePrenotazioni ***

    public List<Prenotazione> visualizzaVisitePerCalendario(Integer medicoId) {
        // Recuperiamo solo quelle con stato "PRENOTATA"
        // (Ignoriamo quelle CANCELLATE o già CONCLUSE per non affollare il calendario)
        return prenotazioneRepository.findByMedicoIdAndStato(medicoId, "PRENOTATA");
    }
}