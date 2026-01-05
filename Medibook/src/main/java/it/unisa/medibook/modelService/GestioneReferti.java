package it.unisa.medibook.modelService;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelStorage.PrenotazioneRepository; // <--- Serve questo
import it.unisa.medibook.modelStorage.RefertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestioneReferti {

    @Autowired
    private RefertoRepository refertoRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository; // <--- Aggiunto per aggiornare lo stato

    /**
     * Operazione: caricaReferto(r: Referto, p: Prenotazione): Boolean
     * * Implementa i vincoli OCL dell'ODD:
     * 1. Pre: r e p non nulli
     * 2. Pre: p.stato deve essere 'EFFETTUATA'
     * 3. Post: r è associato a p e salvato
     * 4. Post: p passa allo stato 'CONCLUSA'
     */
    @Transactional // <--- FONDAMENTALE: Garantisce l'atomicità (o tutto o niente)
    public boolean caricaReferto(Referto r, Prenotazione p) throws Exception {

        // -- Pre-condizione OCL: Oggetti non nulli --
        if (r == null || p == null) {
            throw new IllegalArgumentException("Violazione OCL: Referto e Prenotazione non possono essere nulli.");
        }

        // -- Pre-condizione OCL: Stato visita --
        // Nota: Assicurati che nel DB lo stato sia scritto esattamente così (case sensitive)
        if (!"EFFETTUATA".equalsIgnoreCase(p.getStato())) {
            throw new Exception("Violazione OCL: Impossibile inserire referto. La visita deve essere stata effettuata (Stato attuale: " + p.getStato() + ").");
        }

        // 1. Associa il referto alla prenotazione (Link Java)
        r.setPrenotazione(p);

        // 2. Salva il referto nel DB
        refertoRepository.save(r);

        // 3. Post-condizione: Aggiorna lo stato della prenotazione
        // Chiudiamo il processo: ora la visita è completa al 100%
        p.setStato("CONCLUSA");
        p.setReferto(r); // Aggiorniamo la relazione bidirezionale lato Java

        // Salviamo anche la prenotazione modificata
        prenotazioneRepository.save(p);

        return true;
    }

    // Metodo di lettura
    public Referto visualizzaReferto(Integer prenotazioneId) {
        return refertoRepository.findByPrenotazioneId(prenotazioneId);
    }
}