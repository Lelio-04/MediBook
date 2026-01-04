package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente; // <--- Importante: Serve per creare il nuovo paziente
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
                               HttpSession session,
                               Model model) {
        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            session.setAttribute("utente", utente);

            // Reindirizzamento in base al ruolo
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

    // --- NUOVA PARTE: REGISTRAZIONE ---

    // 1. Mostra il modulo di registrazione
    @GetMapping("/registrazione")
    public String showRegister() {
        return "registrazione"; // Cerca il file registrazione.jsp
    }

    // 2. Riceve i dati dal form HTML e crea l'utente
    @PostMapping("/registrazione")
    public String performRegister(
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String codiceFiscale,
            @RequestParam String telefono,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        // Creiamo l'oggetto Paziente con i dati ricevuti
        Paziente p = new Paziente();
        p.setNome(nome);
        p.setCognome(cognome);
        p.setCodiceFiscale(codiceFiscale);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setPassword(password);

        try {
            // Proviamo a salvare tramite il Service
            gestioneUtenza.registraPaziente(p);

            // Se va bene, mandiamo l'utente al login con un messaggio verde
            model.addAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            return "login";

        } catch (Exception e) {
            // Se c'è un errore (es. email duplicata), ricarichiamo la pagina di registrazione con l'errore rosso
            model.addAttribute("errore", "Errore: " + e.getMessage());
            return "registrazione";
        }
    }
}