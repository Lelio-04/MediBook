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

    // --- NUOVO: Aggiungiamo il service dei referti ---
    @Autowired
    private GestioneReferti gestioneReferti;

    @GetMapping("")
    public String dashboardPaziente(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Paziente) {
            Paziente paziente = (Paziente) utente;
            model.addAttribute("nomePaziente", paziente.getNome() + " " + paziente.getCognome());

            // NOTA: Ho rinominato "storicoVisite" in "visite" per farlo combaciare con la JSP che abbiamo fatto prima
            model.addAttribute("visite", gestionePrenotazioni.visualizzaVisitePaziente(paziente.getId()));
        }

        // Serve per il menu a tendina della prenotazione
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
            redirectAttributes.addAttribute("success", "true"); // Feedback visuale

        } catch (Exception e) {
            System.out.println("Errore prenotazione: " + e.getMessage());
            redirectAttributes.addAttribute("errore", e.getMessage());
        }

        return "redirect:/paziente";
    }

    // --- NUOVO METODO: Visualizza Referto ---
    @GetMapping("/referto")
    public String vediReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        // Controllo Sicurezza
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) return "redirect:/accedi";

        // 1. Recupero il referto usando l'ID della prenotazione
        Referto referto = gestioneReferti.visualizzaReferto(id);

        if (referto == null) {
            return "redirect:/paziente?errore=RefertoNonTrovato";
        }

        // 2. CONTROLLO DI SICUREZZA FONDAMENTALE (IDOR Protection)
        // Verifico che il referto appartenga davvero a questo paziente e non a un altro
        if (!referto.getPrenotazione().getPaziente().getId().equals(utente.getId())) {
            return "redirect:/paziente?errore=AccessoNegato"; // Hacker bloccato
        }

        // 3. Passo il referto alla vista
        model.addAttribute("referto", referto);

        return "paziente_visualizza_referto"; // La JSP "foglio di carta"
    }
}