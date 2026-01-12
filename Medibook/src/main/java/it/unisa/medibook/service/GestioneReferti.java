package it.unisa.medibook.service;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelStorage.PrenotazioneRepository; // <--- Serve questo
import it.unisa.medibook.modelStorage.RefertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    // Metodo di lettura
    public Referto visualizzaReferto(Integer prenotazioneId) {
        return refertoRepository.findByPrenotazioneId(prenotazioneId);
    }
    @Transactional
    public void salvaNuovoReferto(Integer prenotazioneId, String contenuto) throws Exception {
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new Exception("Prenotazione non trovata"));

        Referto r = new Referto();
        r.setContenuto(contenuto);
        r.setDataCaricamento(LocalDateTime.now());
        r.setPrenotazione(p);

        refertoRepository.save(r);

        // Aggiorniamo anche lo stato della prenotazione a CONCLUSA
        p.setStato("CONCLUSA");
        prenotazioneRepository.save(p);
    }
}