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

    @GetMapping("/")
    public String showHome() {
        return "home";
    }

    @GetMapping("/accedi")
    public String showLogin(@RequestParam(required = false) String redirect,
                            @RequestParam(required = false) String cerca, // Manteniamo il parametro ricerca
                            Model model) {
        model.addAttribute("redirect", redirect);
        model.addAttribute("cerca", cerca);
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String redirect,
                               @RequestParam(required = false) String cerca,
                               HttpSession session,
                               Model model) {

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            session.setAttribute("utente", utente);
            String ruolo = utente.getRuolo().toUpperCase();

            // --- LOGICA DI REINDIRIZZAMENTO FILTRATA ---

            // 1. Se c'è un redirect (es. dalla ricerca), lo seguiamo SOLO se l'utente è un PAZIENTE
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.equals("null")) {
                if ("PAZIENTE".equals(ruolo)) {
                    return "redirect:" + redirect;
                }
                // Se NON è un paziente (è medico/segreteria), ignoriamo il redirect della ricerca
                // e proseguiamo sotto verso la loro dashboard naturale.
            }

            // 2. Smistamento forzato in base al ruolo (Pattern Strategy)
            switch (ruolo) {
                case "MEDICO":
                    return "redirect:/medico";
                case "SEGRETERIA":
                    return "redirect:/segreteria";
                case "PAZIENTE":
                    // Se è un paziente e non c'era un redirect specifico, mandalo alla sua area
                    String queryCerca = (cerca != null && !cerca.isEmpty()) ? "?cerca=" + cerca : "";
                    return "redirect:/paziente" + queryCerca;
                default:
                    return "redirect:/";
            }
        }

        model.addAttribute("errore", "Credenziali non valide!");
        model.addAttribute("redirect", redirect);
        model.addAttribute("cerca", cerca);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Rimuove i dati e invalida la sessione
        session.invalidate();
        // Reindirizza alla home page pulita
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