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
    private PrenotazioneRepository prenotazioneRepository;

    public Referto visualizzaReferto(Integer prenotazioneId) {
        return refertoRepository.findByPrenotazioneId(prenotazioneId);
    }
    @Transactional
    public void salvaNuovoReferto(Integer prenotazioneId, String contenuto) throws Exception {
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new Exception("Prenotazione non trovata"));



        if (!"EFFETTUATA".equals(p.getStato())) {
            throw new Exception("Devi prima impostare la visita come Effettuata");
        }

        // 2. Controllo Contenuto Vuoto (TC_REF_2)
        if (contenuto == null || contenuto.trim().isEmpty()) {
            throw new Exception("Il contenuto del referto è obbligatorio");
        }

        Referto r = new Referto();
        r.setContenuto(contenuto);
        r.setDataCaricamento(java.time.LocalDateTime.now());
        r.setPrenotazione(p);

        refertoRepository.save(r);

        p.setStato("CONCLUSA");
        prenotazioneRepository.save(p);
    }
}