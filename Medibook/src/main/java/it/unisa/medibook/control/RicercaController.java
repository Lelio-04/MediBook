package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.modelStorage.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RicercaController {

    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping("/cerca")
    public String cercaMedici(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/";
        }

        // 1. RIMUOVI "Dr." o "Dott." dalla stringa di ricerca
        // "Dr. Mario Rossi" diventa "Mario Rossi"
        String queryPulita = q.replaceAll("(?i)^dr\\.?\\s+|^dott\\.?\\s+", "");

        // 2. Cerca nel DB usando la stringa pulita
        List<Medico> risultati = medicoRepository.cercaGlobale(queryPulita);

        // 3. Passa i risultati
        model.addAttribute("medici", risultati);

        // IMPORTANTE: Nel model rimettiamo 'q' originale per mostrarlo nella barra di ricerca all'utente
        model.addAttribute("query", q);

        return "risultatiRicerca";
    }
}