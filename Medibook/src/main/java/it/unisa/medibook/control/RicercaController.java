package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Recensione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // <--- Importante per leggere l'ID dall'URL
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RicercaController {

    @Autowired
    private MedicoRepository medicoRepository;

    // --- METODO DI RICERCA ESISTENTE ---
    @GetMapping("/cerca")
    public String cercaMedici(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/";
        }

        // 1. RIMUOVI "Dr." o "Dott." dalla stringa di ricerca
        String queryPulita = q.replaceAll("(?i)^dr\\.?\\s+|^dott\\.?\\s+", "");

        // 2. Cerca nel DB usando la stringa pulita
        List<Medico> risultati = medicoRepository.cercaGlobale(queryPulita);

        // 3. Passa i risultati
        model.addAttribute("medici", risultati);

        // IMPORTANTE: Nel model rimettiamo 'q' originale per mostrarlo nella barra di ricerca all'utente
        model.addAttribute("query", q);

        return "risultatiRicerca";
    }

    @Autowired
    private RecensioneRepository recensioneRepository; // Aggiungi questo

    @GetMapping("/medico/{id}")
    public String dettagliMedico(@PathVariable Long id, Model model) {
        Medico medico = medicoRepository.findById(id).orElse(null);
        if (medico == null) return "redirect:/";

        // Recupera le recensioni
        List<Recensione> recensioni = recensioneRepository.findByMedicoId(Math.toIntExact(id));
        model.addAttribute("medico", medico);
        model.addAttribute("recensioni", recensioni);

        // Calcolo media voti (opzionale ma carino)
        double media = 0.0;
        if (!recensioni.isEmpty()) {
            media = recensioni.stream().mapToInt(Recensione::getVoto).average().orElse(0.0);
        }
        model.addAttribute("mediaVoti", String.format("%.1f", media));

        return "dettagli_medico";
    }

}
