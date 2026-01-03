package it.unisa.medibook.presentation;

import it.unisa.medibook.business.GestionePrenotazioni;
import it.unisa.medibook.business.GestioneUtenza;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Nota: NON RestController
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class WebController {

    @Autowired
    private GestioneUtenza gestioneUtenza;

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    // 1. Mostra la pagina di Login
    @GetMapping("/")
    public String showLogin() {
        return "login"; // Cerca /WEB-INF/jsp/login.jsp
    }

    // 2. Gestisce il form di Login
    @PostMapping("/login")
    public String performLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            // Salva l'utente in sessione
            session.setAttribute("utente", utente);

            if ("MEDICO".equals(utente.getRuolo())) {
                return "redirect:/medico"; // Va alla rotta /medico
            } else if ("SEGRETERIA".equals(utente.getRuolo())) {
                return "redirect:/segreteria";
            }
        }

        // Se login fallito:
        model.addAttribute("errore", "Credenziali non valide!");
        return "login";
    }

    // 3. Pagina Dashboard Medico
    @GetMapping("/medico")
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo sicurezza
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // Carico le visite dal DB e le passo alla JSP
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(utente.getId());
        model.addAttribute("visite", visite);
        model.addAttribute("emailMedico", utente.getEmail());

        return "medico"; // Cerca /WEB-INF/jsp/medico.jsp
    }

    // 4. Azione cambio stato (chiamata dal bottone nella JSP)
    @PostMapping("/medico/cambiaStato")
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato) {
        gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        return "redirect:/medico"; // Ricarica la pagina aggiornata
    }


    // 5. Dashboard Segreteria (GET)
    @GetMapping("/segreteria")
    public String dashboardSegreteria(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo di sicurezza: solo SEGRETERIA può entrare
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // Per semplicità, mostriamo l'agenda del Dott. Rossi (ID 1), come abbiamo fatto prima.
        // In un progetto reale potresti fare una findAll() o selezionare il medico.
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(1);
        model.addAttribute("visite", visite);

        return "segreteria"; // Cerca /WEB-INF/jsp/segreteria.jsp
    }

    // 6. Azione Modifica Data/Ora (POST)
    @PostMapping("/segreteria/modifica")
    public String modificaPrenotazione(@RequestParam Integer id,
                                       @RequestParam String data,
                                       @RequestParam String ora) throws Exception {

        // Convertiamo le stringhe ricevute dal form HTML in oggetti Java
        LocalDate dataL = LocalDate.parse(data);
        LocalTime oraL = LocalTime.parse(ora);

        gestionePrenotazioni.modificaPrenotazione(id, dataL, oraL);

        return "redirect:/segreteria"; // Ricarica la pagina per vedere le modifiche
    }
}