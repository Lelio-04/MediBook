package it.unisa.medibook.modelService;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelStorage.RefertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GestioneReferti {

    @Autowired
    private RefertoRepository refertoRepository;

    /**
     * Operazione: caricaReferto(r: Referto, p: Prenotazione): Boolean
     * * Implementa i vincoli OCL dell'ODD:
     * 1. Pre: r e p non nulli
     * 2. Pre: p.stato deve essere 'EFFETTUATA'
     * 3. Post: r è associato a p e salvato
     */
    public boolean caricaReferto(Referto r, Prenotazione p) throws Exception {

        // -- Pre-condizione OCL: Oggetti non nulli --
        if (r == null || p == null) {
            throw new IllegalArgumentException("Violazione OCL: Referto e Prenotazione non possono essere nulli.");
        }

        // -- Pre-condizione OCL: Stato visita --
        if (!"EFFETTUATA".equalsIgnoreCase(p.getStato())) {
            throw new Exception("Violazione OCL: Impossibile inserire referto. La visita deve essere conclusa (Stato richiesto: EFFETTUATA, Stato attuale: " + p.getStato() + ").");
        }

        // Associa il referto alla prenotazione (Link)
        r.setPrenotazione(p);

        // Salva nel DB
        refertoRepository.save(r);

        return true;
    }

    // Metodo di lettura (usato per far vedere il referto al paziente)
    public Referto visualizzaReferto(Integer prenotazioneId) {
        return refertoRepository.findByPrenotazioneId(prenotazioneId);
    }
}