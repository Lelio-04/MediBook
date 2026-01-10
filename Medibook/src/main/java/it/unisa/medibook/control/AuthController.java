package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.SegreteriaPrenotazioni;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.service.GestioneUtenza;
import it.unisa.medibook.service.PasswordService; // <--- 1. Import Service
import it.unisa.medibook.modelStorage.UtenteRepository;
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

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordService passwordService; // <--- 2. Iniezione PasswordService

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

        // ====================================================================
        // MODIFICA LOGIN: CONTROLLO HASH
        // ====================================================================

        // 1. Cerchiamo l'utente SOLO tramite Email
        Utente utente = utenteRepository.findByEmail(email).orElse(null);

        // 2. Verifichiamo se l'utente esiste E se la password corrisponde all'hash
        if (utente != null && passwordService.check(password, utente.getPassword())) {

            session.setAttribute("utente", utente);

            // ====================================================================
            // CONTROLLO PRIMO ACCESSO (Password Provvisoria)
            // ====================================================================
            // Controlliamo se la password digitata dall'utente è quella provvisoria
            if (password.equals("Medibook123")) {
                return "redirect:/cambio-password-obbligatorio";
            }
            // ====================================================================


            // --- GESTIONE SEGRETERIE ---
            if (utente instanceof SegreteriaUtenti) {
                return "redirect:/segreteria-utenti/dashboard";
            }

            if (utente instanceof SegreteriaPrenotazioni) {
                return "redirect:/segreteria-prenotazioni/dashboard";
            }

            // --- GESTIONE ALTRI RUOLI ---
            String ruolo = utente.getRuolo().toUpperCase();

            // Logica Redirect per Paziente
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.equals("null")) {
                if ("PAZIENTE".equals(ruolo)) {
                    return "redirect:" + redirect;
                }
            }

            switch (ruolo) {
                case "MEDICO":
                    return "redirect:/medico";
                case "PAZIENTE":
                    String queryCerca = (cerca != null && !cerca.isEmpty()) ? "?cerca=" + cerca : "";
                    return "redirect:/paziente" + queryCerca; // Ho corretto il path (era /)
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

        // Hash della password
        p.setPassword(passwordService.hash(password));

        try {
            gestioneUtenza.registraPaziente(p);
            model.addAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            return "login";

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // CATTURIAMO L'ERRORE DI DUPLICAZIONE SPECIFICO
            model.addAttribute("errore", "Errore: Esiste già un utente con questa Email o Codice Fiscale.");
            return "registrazione";

        } catch (Exception e) {
            // Errore generico
            model.addAttribute("errore", "Errore generico: " + e.getMessage());
            return "registrazione";
        }
    }

    // ====================================================================
    // CAMBIO PASSWORD OBBLIGATORIO
    // ====================================================================

    @GetMapping("/cambio-password-obbligatorio")
    public String showCambioPassword() {
        return "cambio_password_obbligatorio";
    }

    @PostMapping("/aggiorna-password-iniziale")
    public String performCambioPassword(@RequestParam String nuovaPassword,
                                        @RequestParam String confermaPassword,
                                        HttpSession session,
                                        Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null) return "redirect:/accedi";

        // Validazioni
        if (!nuovaPassword.equals(confermaPassword)) {
            model.addAttribute("errore", "Le due password non coincidono.");
            return "cambio_password_obbligatorio";
        }

        if (nuovaPassword.equals("Medibook123")) {
            model.addAttribute("errore", "Devi scegliere una password diversa da quella provvisoria!");
            return "cambio_password_obbligatorio";
        }

        // MODIFICA: HASHIAMO LA NUOVA PASSWORD
        utente.setPassword(passwordService.hash(nuovaPassword));

        // Salva nel Database
        utenteRepository.save(utente);

        // Aggiorna la sessione
        session.setAttribute("utente", utente);

        // Redirect
        if ("PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/paziente?msg=Benvenuto";
        } else if ("MEDICO".equals(utente.getRuolo())) {
            return "redirect:/medico?msg=Benvenuto";
        } else if ("SEGRETERIA".equals(utente.getRuolo())) {
            // Gestione generica se serve, altrimenti gli specifici sopra
            return "redirect:/";
        }

        return "redirect:/";
    }
}