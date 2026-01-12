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
    private GestioneMedico gestioneMedico;

    // --- METODO DI RICERCA ---
    @GetMapping("/cerca")
    public String cercaMedici(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/";
        }


        List<Medico> risultati = gestioneMedico.ricercaAvanzata(q);


        model.addAttribute("medici", risultati);


        model.addAttribute("query", q);

        return "risultatiRicerca";
    }

    // --- DETTAGLI MEDICO E RECENSIONI ---
    @GetMapping("/medico/{id}")
    public String dettagliMedico(@PathVariable Integer id, Model model) {

        Medico medico = gestioneMedico.getMedicoById(id);
        if (medico == null) return "redirect:/";


        List<Recensione> recensioni = gestioneMedico.getRecensioniPerMedico(id);


        double media = gestioneMedico.calcolaMediaVoti(recensioni);

        model.addAttribute("medico", medico);
        model.addAttribute("recensioni", recensioni);
        model.addAttribute("mediaVoti", String.format("%.1f", media));

        return "dettagli_medico";
    }
}