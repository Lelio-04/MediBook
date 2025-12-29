package it.unisa.medibook.presentation;

import it.unisa.medibook.business.GestioneReferti;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.storage.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/referti")
@CrossOrigin(origins = "http://localhost:3000") // Abilita il Frontend React
public class RefertoService {

    @Autowired
    private GestioneReferti gestioneReferti;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    // Endpoint per CARICARE un referto
    // POST /api/referti/carica
    // Body JSON: { "prenotazioneId": 1, "contenuto": "Tutto ok..." }
    @PostMapping("/carica")
    public ResponseEntity<?> caricaReferto(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Estrazione dati grezzi (Compito del Controller)
            Integer id = (Integer) payload.get("prenotazioneId");
            String contenuto = (String) payload.get("contenuto");

            // 2. Recupero Prenotazione dal DB (Compito del Controller)
            Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);
            if (pOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Prenotazione non trovata");
            }
            Prenotazione p = pOpt.get();

            // 3. Creazione Oggetto Referto (Compito del Controller)
            Referto r = new Referto(contenuto, LocalDate.now());

            // 4. Passaggio al Business Layer (Rispetta la firma dell'ODD)
            gestioneReferti.caricaReferto(r, p);

            return ResponseEntity.ok("Referto salvato con successo!");

        } catch (Exception e) {
            // Ritorna errore 400 se i controlli OCL falliscono
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        }
    }

    // Endpoint per LEGGERE un referto
    // GET /api/referti/visita/{id}
    @GetMapping("/visita/{prenotazioneId}")
    public ResponseEntity<?> getReferto(@PathVariable Integer prenotazioneId) {
        Referto r = gestioneReferti.visualizzaReferto(prenotazioneId);

        if (r != null) {
            return ResponseEntity.ok(r);
        } else {
            return ResponseEntity.status(404).body("Referto non ancora disponibile.");
        }
    }
}