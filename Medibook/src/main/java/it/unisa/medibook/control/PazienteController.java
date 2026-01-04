package it.unisa.medibook.control;

import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.model.Paziente;
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

@Controller
@RequestMapping("/paziente") // Tutte le rotte qui iniziano con /paziente
public class PazienteController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @GetMapping("") // Risponde a /paziente
    public String dashboardPaziente(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Paziente) {
            Paziente paziente = (Paziente) utente;
            model.addAttribute("nomePaziente", paziente.getNome() + " " + paziente.getCognome());
            model.addAttribute("storicoVisite", gestionePrenotazioni.visualizzaVisitePaziente(paziente.getId()));
        }

        model.addAttribute("listaMedici", gestionePrenotazioni.dammiTuttiIMedici());
        return "paziente";
    }

    @PostMapping("/prenota") // Risponde a /paziente/prenota
    public String prenotaVisita(@RequestParam Integer idMedico,
                                @RequestParam String data,
                                @RequestParam String ora,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo di sicurezza: l'utente esiste?
        if (utente == null) return "redirect:/";

        try {
            LocalDate dataL = LocalDate.parse(data);
            LocalTime oraL = LocalTime.parse(ora);

            gestionePrenotazioni.nuovaPrenotazione(utente.getId(), idMedico, dataL, oraL);
            redirectAttributes.addAttribute("success", "true");

        } catch (Exception e) {
            System.out.println("Errore prenotazione: " + e.getMessage());
        }

        return "redirect:/paziente";
    }
}