package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.SegreteriaPrenotazioni;
import it.unisa.medibook.modelService.GestioneUtenza;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelStorage.UtenteRepository; // <--- AGGIUNTO
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

    // Ci serve il repository per salvare la nuova password direttamente da qui
    // senza dover modificare la classe GestioneUtenza.java
    @Autowired
    private UtenteRepository utenteRepository;

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

            // ====================================================================
            // 1. CONTROLLO PRIMO ACCESSO (Password Provvisoria)
            // ====================================================================
            // Se la password è quella di default, blocchiamo tutto e forziamo il cambio.
            if (password.equals("Medibook123")) {
                return "redirect:/cambio-password-obbligatorio";
            }
            // ====================================================================


            // --- 2. GESTIONE SEGRETERIE (Separation of Duties) ---
            if (utente instanceof SegreteriaUtenti) {
                return "redirect:/segreteria-utenti/dashboard";
            }

            if (utente instanceof SegreteriaPrenotazioni) {
                return "redirect:/segreteria-prenotazioni/dashboard";
            }

            // --- 3. GESTIONE ALTRI RUOLI (Medico, Paziente) ---
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
                    return "redirect:/medico";
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

    // --- REGISTRAZIONE (Solo per Pazienti autonomi) ---

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
        p.setPassword(password); // Salviamo in chiaro come richiesto

        try {
            gestioneUtenza.registraPaziente(p);
            model.addAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("errore", "Errore: " + e.getMessage());
            return "registrazione";
        }
    }

    // ====================================================================
    // NUOVE ROTTE PER IL CAMBIO PASSWORD OBBLIGATORIO
    // ====================================================================

    @GetMapping("/cambio-password-obbligatorio")
    public String showCambioPassword() {
        // Mostra la pagina JSP che forza il cambio password
        return "cambio_password_obbligatorio";
    }

    @PostMapping("/aggiorna-password-iniziale")
    public String performCambioPassword(@RequestParam String nuovaPassword,
                                        @RequestParam String confermaPassword,
                                        HttpSession session,
                                        Model model) {

        // 1. Recupera l'utente dalla sessione (è stato salvato nel login appena fatto)
        Utente utente = (Utente) session.getAttribute("utente");

        // Se per qualche motivo la sessione è scaduta, rimanda al login
        if (utente == null) return "redirect:/accedi";

        // 2. Validazioni
        if (!nuovaPassword.equals(confermaPassword)) {
            model.addAttribute("errore", "Le due password non coincidono.");
            return "cambio_password_obbligatorio";
        }

        if (nuovaPassword.equals("Medibook123")) {
            model.addAttribute("errore", "Devi scegliere una password diversa da quella provvisoria!");
            return "cambio_password_obbligatorio";
        }

        // 3. Aggiorna la password nell'oggetto Java
        utente.setPassword(nuovaPassword);

        // 4. Salva nel Database (usiamo direttamente il repository qui)
        utenteRepository.save(utente);

        // 5. Aggiorna l'utente in sessione con la nuova password (per sicurezza)
        session.setAttribute("utente", utente);

        // 6. Redirect alla dashboard corretta in base al ruolo
        if ("PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/paziente?msg=Benvenuto";
        } else if ("MEDICO".equals(utente.getRuolo())) {
            return "redirect:/medico?msg=Benvenuto";
        }

        return "redirect:/";
    }
}