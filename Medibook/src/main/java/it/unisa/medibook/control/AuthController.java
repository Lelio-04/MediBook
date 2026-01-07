package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.SegreteriaUtenti;       // Importa la classe specifica
import it.unisa.medibook.model.SegreteriaPrenotazioni; // Importa la classe specifica
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
                            @RequestParam(required = false) String cerca,
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

            // --- 1. GESTIONE SEGRETERIE (Separation of Duties) ---
            // Controlliamo il TIPO specifico della classe per indirizzare all'area corretta

            if (utente instanceof SegreteriaUtenti) {
                // Chi gestisce le anagrafiche va qui
                return "redirect:/segreteria-utenti/dashboard";
            }

            if (utente instanceof SegreteriaPrenotazioni) {
                // Chi gestisce l'agenda va qui
                return "redirect:/segreteria-prenotazioni/dashboard";
            }

            // --- 2. GESTIONE ALTRI RUOLI (Medico, Paziente) ---
            String ruolo = utente.getRuolo().toUpperCase();

            // Logica Redirect per Paziente (es. se arrivava da una ricerca)
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.equals("null")) {
                if ("PAZIENTE".equals(ruolo)) {
                    return "redirect:" + redirect;
                }
            }

            // Smistamento Standard
            switch (ruolo) {
                case "MEDICO":
                    return "redirect:/medico"; // Assicurati di avere un MedicoController

                // NOTA: Il case "SEGRETERIA" generico è stato RIMOSSO

                case "PAZIENTE":
                    String queryCerca = (cerca != null && !cerca.isEmpty()) ? "?cerca=" + cerca : "";
                    return "redirect:/paziente" + queryCerca;

                default:
                    return "redirect:/";
            }
        }

        // Login fallito
        model.addAttribute("errore", "Credenziali non valide!");
        model.addAttribute("redirect", redirect);
        model.addAttribute("cerca", cerca);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- REGISTRAZIONE (Solo per Pazienti) ---

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
        // p.setRuolo("PAZIENTE"); // Impostalo se non lo fa il costruttore o il DB

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