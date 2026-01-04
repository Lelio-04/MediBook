package it.unisa.medibook.control;


import it.unisa.medibook.modelService.GestioneUtenza;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private GestioneUtenza gestioneUtenza;

    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            session.setAttribute("utente", utente);

            if ("MEDICO".equals(utente.getRuolo())) {
                return "redirect:/medico";
            } else if ("SEGRETERIA".equals(utente.getRuolo())) {
                return "redirect:/segreteria";
            } else if ("PAZIENTE".equals(utente.getRuolo())) {
                return "redirect:/paziente";
            }
        }

        model.addAttribute("errore", "Credenziali non valide!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}