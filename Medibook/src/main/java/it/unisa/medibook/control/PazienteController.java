package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.modelService.GestioneReferti;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/paziente")
public class PazienteController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @Autowired
    private GestioneReferti gestioneReferti;

    @GetMapping("")
    public String dashboardPaziente(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo se l'utente è loggato ed è un PAZIENTE
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Paziente) {
            Paziente paziente = (Paziente) utente;
            model.addAttribute("nomePaziente", paziente.getNome() + " " + paziente.getCognome());

            // --- MODIFICA FONDAMENTALE ---
            // Abbiamo cambiato "visite" in "storicoVisite" per farlo combaciare con la JSP
            // che usa <c:forEach items="${storicoVisite}" ...>
            model.addAttribute("storicoVisite", gestionePrenotazioni.visualizzaVisitePaziente(paziente.getId()));
        }

        // Serve per il menu a tendina della prenotazione (lista medici)
        model.addAttribute("listaMedici", gestionePrenotazioni.dammiTuttiIMedici());

        return "paziente";
    }

    @PostMapping("/prenota")
    public String prenotaVisita(@RequestParam Integer idMedico,
                                @RequestParam String data,
                                @RequestParam String ora,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) return "redirect:/";

        try {
            LocalDate dataL = LocalDate.parse(data);
            LocalTime oraL = LocalTime.parse(ora);

            gestionePrenotazioni.nuovaPrenotazione(utente.getId(), idMedico, dataL, oraL);

            // Feedback visuale di successo
            redirectAttributes.addAttribute("success", "true");

        } catch (Exception e) {
            System.out.println("Errore prenotazione: " + e.getMessage());
            redirectAttributes.addAttribute("errore", e.getMessage());
        }

        return "redirect:/paziente";
    }

    @GetMapping("/referto")
    public String vediReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo Sicurezza Login
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) return "redirect:/accedi";

        // 1. Recupero il referto usando l'ID della prenotazione
        Referto referto = gestioneReferti.visualizzaReferto(id);

        if (referto == null) {
            return "redirect:/paziente?errore=RefertoNonTrovato";
        }

        // 2. CONTROLLO DI SICUREZZA (IDOR Protection)
        // Verifico che il referto appartenga davvero a questo paziente e non a un altro
        if (!referto.getPrenotazione().getPaziente().getId().equals(utente.getId())) {
            return "redirect:/paziente?errore=AccessoNegato"; // Hacker bloccato
        }

        // 3. Passo il referto alla vista
        model.addAttribute("referto", referto);

        // Assicurati di avere il file "paziente_visualizza_referto.jsp" nella cartella delle viste
        return "paziente_visualizza_referto";
    }
}