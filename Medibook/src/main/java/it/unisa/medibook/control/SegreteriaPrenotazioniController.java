package it.unisa.medibook.control;
import it.unisa.medibook.model.SegreteriaPrenotazioni;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/segreteria-prenotazioni")
public class SegreteriaPrenotazioniController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        return "dashboard-agenda"; // Quella con la tabella delle visite
    }

    // Qui metti i metodi per modificare date e orari
}