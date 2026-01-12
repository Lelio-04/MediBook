package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Recensione;
import it.unisa.medibook.service.GestioneMedico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RicercaController {

    @Autowired
    private GestioneMedico gestioneMedico; // <--- Unico Service di riferimento

    // --- METODO DI RICERCA ---
    @GetMapping("/cerca")
    public String cercaMedici(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/";
        }

        // 1. La logica di pulizia "Dr./Dott." e la query DB sono ora nel Service
        List<Medico> risultati = gestioneMedico.ricercaAvanzata(q);

        // 2. Passiamo i risultati alla vista
        model.addAttribute("medici", risultati);

        // Manteniamo la query originale per la barra di ricerca dell'utente
        model.addAttribute("query", q);

        return "risultatiRicerca";
    }

    // --- DETTAGLI MEDICO E RECENSIONI ---
    @GetMapping("/medico/{id}")
    public String dettagliMedico(@PathVariable Integer id, Model model) {
        // 1. Recupero Medico tramite Service
        Medico medico = gestioneMedico.getMedicoById(id);
        if (medico == null) return "redirect:/";

        // 2. Recupero Recensioni tramite Service
        List<Recensione> recensioni = gestioneMedico.getRecensioniPerMedico(id);

        // 3. Calcolo Media tramite Service
        double media = gestioneMedico.calcolaMediaVoti(recensioni);

        model.addAttribute("medico", medico);
        model.addAttribute("recensioni", recensioni);
        model.addAttribute("mediaVoti", String.format("%.1f", media));

        return "dettagli_medico";
    }
}