package it.unisa.medibook.presentation;

import it.unisa.medibook.business.GestionePrenotazioni;
import it.unisa.medibook.business.GestioneUtenza;
import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private GestioneUtenza gestioneUtenza;

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    // ==========================================
    // SEZIONE 1: LOGIN E LOGOUT
    // ==========================================

    @GetMapping("/")
    public String showLogin() {
        return "login"; // Cerca /WEB-INF/jsp/login.jsp
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            // Salva l'utente generico in sessione
            session.setAttribute("utente", utente);

            // Reindirizzamento in base al Ruolo
            if ("MEDICO".equals(utente.getRuolo())) {
                return "redirect:/medico";
            } else if ("SEGRETERIA".equals(utente.getRuolo())) {
                return "redirect:/segreteria";
            } else if ("PAZIENTE".equals(utente.getRuolo())) {
                return "redirect:/paziente";
            }
        }

        // Se login fallito
        model.addAttribute("errore", "Credenziali non valide!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Distrugge la sessione
        return "redirect:/";  // Torna al login
    }

    // ==========================================
    // SEZIONE 2: AREA MEDICO
    // ==========================================

    @GetMapping("/medico")
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo sicurezza
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // CASTING: Trasformiamo l'Utente generico in Medico per leggere il cognome
        if (utente instanceof Medico) {
            Medico medico = (Medico) utente;
            model.addAttribute("nomeMedico", medico.getCognome());
            // Passiamo l'ID corretto
            List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(medico.getId());
            model.addAttribute("visite", visite);
        }

        return "medico";
    }

    @PostMapping("/medico/cambiaStato")
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato) {
        gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        return "redirect:/medico";
    }

    // ==========================================
    // SEZIONE 3: AREA SEGRETERIA
    // ==========================================

    @GetMapping("/segreteria")
    public String dashboardSegreteria(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // Visualizza agenda del Dott. Rossi (ID 1) come esempio
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(1);
        model.addAttribute("visite", visite);

        return "segreteria";
    }

    @PostMapping("/segreteria/modifica")
    public String modificaPrenotazione(@RequestParam Integer id,
                                       @RequestParam String data,
                                       @RequestParam String ora,
                                       RedirectAttributes redirectAttributes) {
        try {
            LocalDate dataL = LocalDate.parse(data);
            LocalTime oraL = LocalTime.parse(ora);

            gestionePrenotazioni.modificaPrenotazione(id, dataL, oraL);

        } catch (Exception e) {
            System.out.println("Errore modifica: " + e.getMessage());
        }

        return "redirect:/segreteria";
    }

    // ==========================================
    // SEZIONE 4: AREA PAZIENTE
    // ==========================================

    @GetMapping("/paziente")
    public String dashboardPaziente(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo Sicurezza
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // --- CASTING FONDAMENTALE (Risolve il tuo errore) ---
        // Verifichiamo che sia davvero un Paziente e facciamo il cast
        if (utente instanceof Paziente) {
            Paziente paziente = (Paziente) utente;

            // Ora possiamo chiamare getNome() perché la variabile è di tipo Paziente
            model.addAttribute("nomePaziente", paziente.getNome() + " " + paziente.getCognome());

            // Carica lo storico usando l'ID del paziente
            model.addAttribute("storicoVisite", gestionePrenotazioni.visualizzaVisitePaziente(paziente.getId()));
        }

        // Carica la lista dei medici per il menu a tendina
        model.addAttribute("listaMedici", gestionePrenotazioni.dammiTuttiIMedici());

        return "paziente";
    }

    @PostMapping("/paziente/prenota")
    public String prenotaVisita(@RequestParam Integer idMedico,
                                @RequestParam String data,
                                @RequestParam String ora,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");

        try {
            LocalDate dataL = LocalDate.parse(data);
            LocalTime oraL = LocalTime.parse(ora);

            gestionePrenotazioni.nuovaPrenotazione(utente.getId(), idMedico, dataL, oraL);

            // Se va bene, aggiunge parametro di successo
            redirectAttributes.addAttribute("success", "true");

        } catch (Exception e) {
            System.out.println("Errore prenotazione: " + e.getMessage());
        }

        return "redirect:/paziente";
    }
}