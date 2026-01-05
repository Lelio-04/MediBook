package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
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

    // --- HOME PAGE E LOGIN ---

    @GetMapping("/")
    public String showHome() {
        return "home";
    }

    @GetMapping("/accedi")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String redirect, // <--- 1. Parametro opzionale aggiunto
                               HttpSession session,
                               Model model) {

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            session.setAttribute("utente", utente);

            // --- 2. LOGICA DEL REDIRECT ---
            // Se c'è un indirizzo "in memoria" (es. prenotazione interrotta), andiamo lì.
            if (redirect != null && !redirect.trim().isEmpty()) {
                return "redirect:" + redirect;
            }

            // Altrimenti, comportamento standard in base al ruolo
            if ("MEDICO".equals(utente.getRuolo())) {
                return "redirect:/medico";
            } else if ("SEGRETERIA".equals(utente.getRuolo())) {
                return "redirect:/segreteria";
            } else if ("PAZIENTE".equals(utente.getRuolo())) {
                return "redirect:/paziente";
            }
        }

        model.addAttribute("errore", "Credenziali non valide!");
        // Se il login fallisce, rimandiamo indietro anche il parametro redirect
        // così l'utente non lo perde al secondo tentativo
        if (redirect != null) {
            model.addAttribute("redirect", redirect);
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- REGISTRAZIONE ---

    @GetMapping("/registrazione")
    public String showRegister() {
        return "registrazione";
    }

    @PostMapping("/registrazione")
    public String performRegister(
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String codiceFiscale,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        Paziente p = new Paziente();
        p.setNome(nome);
        p.setCognome(cognome);
        p.setCodiceFiscale(codiceFiscale);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setPassword(password);

        try {
            gestioneUtenza.registraPaziente(p);
            model.addAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            return "login";

        } catch (Exception e) {
            model.addAttribute("errore", "Errore: " + e.getMessage());
            return "registrazione";
        }
    }
}