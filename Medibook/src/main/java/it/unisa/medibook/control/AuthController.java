package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.SegreteriaPrenotazioni;
import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.service.GestioneUtenza;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // UNICO PUNTO DI CONTATTO: IL SERVICE
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

        // 1. DELEGA AL SERVICE: "Dammi l'utente se le credenziali sono giuste"
        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            session.setAttribute("utente", utente);

            // --- CONTROLLO PRIMO ACCESSO (Logica di Routing) ---
            // Nota: Controlliamo la password IN CHIARO qui perché il service ci ha detto che è valida.
            // Se la password valida è quella di default, forziamo il cambio.
            if (password.equals("Medibook123")) {
                return "redirect:/cambio-password-obbligatorio";
            }

            // --- LOGICA DI REINDIRIZZAMENTO (Routing) ---
            if (utente instanceof SegreteriaUtenti) return "redirect:/segreteria-utenti/dashboard";
            if (utente instanceof SegreteriaPrenotazioni) return "redirect:/segreteria-prenotazioni/dashboard";

            String ruolo = utente.getRuolo().toUpperCase();

            // Gestione redirect pendente (es. stavo prenotando e mi hai chiesto il login)
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.equals("null")) {
                if ("PAZIENTE".equals(ruolo)) return "redirect:" + redirect;
            }

            switch (ruolo) {
                case "MEDICO": return "redirect:/medico";
                case "PAZIENTE":
                    String queryCerca = (cerca != null && !cerca.isEmpty()) ? "?cerca=" + cerca : "";
                    return "redirect:/paziente" + queryCerca;
                default: return "redirect:/";
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

    // --- REGISTRAZIONE ---

    @GetMapping("/registrazione")
    public String showRegister() {
        return "registrazione";
    }

    @PostMapping("/registrazione")
    public String performRegister(
            @RequestParam String nome, @RequestParam String cognome,
            @RequestParam String codiceFiscale, @RequestParam String telefono,
            @RequestParam String email, @RequestParam String password,
            Model model) {

        // Creiamo il DTO/Oggetto base
        Paziente p = new Paziente();
        p.setNome(nome);
        p.setCognome(cognome);
        p.setCodiceFiscale(codiceFiscale);
        p.setTelefono(telefono);
        p.setEmail(email);

        try {
            // DELEGA TOTALE AL SERVICE (Incluso Hash e Controlli Duplicati)
            gestioneUtenza.registraPaziente(p, password);

            model.addAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            return "login";

        } catch (Exception e) {
            // Il service ci lancia eccezioni parlanti (es. "Email già presente")
            model.addAttribute("errore", "Errore: " + e.getMessage());
            return "registrazione";
        }
    }

    // --- CAMBIO PASSWORD OBBLIGATORIO ---

    @GetMapping("/cambio-password-obbligatorio")
    public String showCambioPassword() {
        return "cambio_password_obbligatorio";
    }

    @PostMapping("/aggiorna-password-iniziale")
    public String performCambioPassword(@RequestParam String nuovaPassword,
                                        @RequestParam String confermaPassword,
                                        HttpSession session,
                                        Model model) {

        Utente utenteSessione = (Utente) session.getAttribute("utente");
        if (utenteSessione == null) return "redirect:/accedi";

        // 1. Validazione base (UI Logic)
        if (!nuovaPassword.equals(confermaPassword)) {
            model.addAttribute("errore", "Le due password non coincidono.");
            return "cambio_password_obbligatorio";
        }

        if (nuovaPassword.equals("Medibook123")) {
            model.addAttribute("errore", "La nuova password deve essere diversa da quella provvisoria.");
            return "cambio_password_obbligatorio";
        }

        try {
            // 2. Delega al Service (Business Logic & DB)
            Utente utenteAggiornato = gestioneUtenza.cambioPasswordObbligatorio(utenteSessione.getId(), nuovaPassword);

            // 3. Aggiorna sessione
            session.setAttribute("utente", utenteAggiornato);

            // 4. Redirect
            String ruolo = utenteAggiornato.getRuolo().toUpperCase();
            return switch (ruolo) {
                case "PAZIENTE" -> "redirect:/paziente?msg=PasswordAggiornata";
                case "MEDICO" -> "redirect:/medico?msg=PasswordAggiornata";
                default -> "redirect:/";
            };

        } catch (Exception e) {
            model.addAttribute("errore", "Errore: " + e.getMessage());
            return "cambio_password_obbligatorio";
        }
    }
}