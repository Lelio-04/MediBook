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
        Utente u = (Utente) session.getAttribute("utenteLoggato");

        // CONTROLLO FONDAMENTALE: Se non è SegreteriaUtenti, lo cacci via

        return "dashboard-gestione-anagrafiche";
    }

    // Qui metti i metodi per creare/modificare pazienti
}
