package it.unisa.medibook.presentation;

import it.unisa.medibook.business.GestionePrenotazioni;
import it.unisa.medibook.model.Prenotazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "http://localhost:3000")
public class PrenotazioneService {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    // 1. SEGRETERIA: Modifica Data e Ora
    // Endpoint: PUT /api/prenotazioni/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaPrenotazione(
            @PathVariable Integer id,
            @RequestBody Map<String, String> dati) {

        try {
            // Converte le stringhe JSON in oggetti Java
            LocalDate data = LocalDate.parse(dati.get("data"));
            LocalTime ora = LocalTime.parse(dati.get("ora"));

            Prenotazione p = gestionePrenotazioni.modificaPrenotazione(id, data, ora);

            if (p != null) return ResponseEntity.ok(p);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Dati non validi: " + e.getMessage());
        }
    }

    // 2. MEDICO: Cambia Stato Visita
    // Endpoint: PATCH /api/prenotazioni/{id}/stato
    // Esempio URL: /api/prenotazioni/5/stato?nuovoStato=EFFETTUATA
    @PatchMapping("/{id}/stato")
    public ResponseEntity<?> cambiaStato(
            @PathVariable Integer id,
            @RequestParam String nuovoStato) {

        Prenotazione p = gestionePrenotazioni.aggiornaStatoVisita(id, nuovoStato);

        if (p != null) return ResponseEntity.ok(p);
        return ResponseEntity.notFound().build();
    }

    // 3. MEDICO: Visualizza le sue visite
    // Endpoint: GET /api/prenotazioni/medico/{idMedico}
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<Prenotazione>> getVisiteMedico(@PathVariable Integer idMedico) {
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(idMedico);
        return ResponseEntity.ok(visite);
    }
}