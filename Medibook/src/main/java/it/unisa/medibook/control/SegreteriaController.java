package it.unisa.medibook.control;

import it.unisa.medibook.modelService.GestionePrenotazioni;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/segreteria") // Tutte le rotte qui iniziano con /segreteria
public class SegreteriaController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @GetMapping("") // Risponde a /segreteria
    public String dashboardSegreteria(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // TODO: In futuro qui potrai mettere un menu per scegliere il medico
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(1);
        model.addAttribute("visite", visite);

        return "segreteria";
    }

    @PostMapping("/modifica") // Risponde a /segreteria/modifica
    public String modificaPrenotazione(@RequestParam Integer id,
                                       @RequestParam String data,
                                       @RequestParam String ora,
                                       RedirectAttributes redirectAttributes) {
        try {
            LocalDate dataL = LocalDate.parse(data);
            LocalTime oraL = LocalTime.parse(ora);
            gestionePrenotazioni.modificaPrenotazione(id, dataL, oraL);
        } catch (Exception e) {
            System.out.println("Errore modifica: " + e.getMessage());
        }
        return "redirect:/segreteria";
    }
}