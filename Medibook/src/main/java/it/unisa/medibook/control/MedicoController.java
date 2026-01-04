package it.unisa.medibook.control;

import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/medico") // Tutte le rotte qui iniziano con /medico
public class MedicoController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @GetMapping("") // Risponde a /medico
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Medico) {
            Medico medico = (Medico) utente;
            model.addAttribute("nomeMedico", medico.getCognome());
            List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(medico.getId());
            model.addAttribute("visite", visite);
        }

        return "medico";
    }

    @PostMapping("/cambiaStato") // Risponde a /medico/cambiaStato
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato) {
        gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        return "redirect:/medico";
    }
}