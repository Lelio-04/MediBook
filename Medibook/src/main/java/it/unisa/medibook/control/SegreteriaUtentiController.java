package it.unisa.medibook.control;

import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/segreteria-utenti")
public class SegreteriaUtentiController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        return "dashboard-gestione-anagrafiche";
    }

    // Qui metti i metodi per creare/modificare pazienti
}
