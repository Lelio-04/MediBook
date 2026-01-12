package it.unisa.medibook.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisa.medibook.model.*;
import it.unisa.medibook.service.GestionePrenotazioni;
import it.unisa.medibook.service.GestioneReferti;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    @Autowired private GestionePrenotazioni gestionePrenotazioni;
    @Autowired private GestioneReferti gestioneReferti;

    // --- DASHBOARD ---
    @GetMapping("")
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");


        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/";

        if (utente instanceof Medico) {
            Medico medico = (Medico) utente; // <--- CASTING FONDAMENTALE
            model.addAttribute("nomeMedico", medico.getCognome());
            model.addAttribute("visite", gestionePrenotazioni.visualizzaVisiteMedico(medico.getId()));
        }

        return "medico";
    }

    // --- CALENDARIO ---
    @GetMapping("/calendario")
    public String visualizzaCalendario(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/login";

        try {

            var eventi = gestionePrenotazioni.getEventiCalendarioJSON(utente.getId());
            model.addAttribute("eventiJson", new ObjectMapper().writeValueAsString(eventi));
        } catch (Exception e) {
            model.addAttribute("eventiJson", "[]");
        }

        return "medicoCalendario";
    }

    // --- GESTIONE REFERTI ---
    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        Prenotazione p = gestionePrenotazioni.getPrenotazioneById(id);

        // Controllo di sicurezza: il medico può scrivere referti solo per le SUE visite
        if (p == null || !p.getMedico().getId().equals(utente.getId())) {
            return "redirect:/medico?errore=AccessoNegato";
        }

        if (!"EFFETTUATA".equals(p.getStato())) {
            return "redirect:/medico?errore=VisitaNonValida";
        }

        model.addAttribute("prenotazione", p);
        return "medico_scrivi_referto";
    }

    @PostMapping("/referto/salva")
    public String salvaReferto(@RequestParam Integer prenotazioneId,
                               @RequestParam String contenuto,
                               HttpSession session,
                               Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        try {
            // Verifichiamo la proprietà della prenotazione prima di salvare
            Prenotazione p = gestionePrenotazioni.getPrenotazioneById(prenotazioneId);
            if (p == null || !p.getMedico().getId().equals(utente.getId())) {
                throw new Exception("Non hai i permessi per refertare questa visita.");
            }

            gestioneReferti.salvaNuovoReferto(prenotazioneId, contenuto);
            return "redirect:/medico?successo=RefertoSalvato";
        } catch (Exception e) {
            model.addAttribute("errore", e.getMessage());
            model.addAttribute("prenotazione", gestionePrenotazioni.getPrenotazioneById(prenotazioneId));
            return "medico_scrivi_referto";
        }
    }

    @GetMapping("/referto/visualizza")
    public String visualizza(@RequestParam("id") Integer prenotazioneId, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null) return "redirect:/accedi";

        Referto referto = gestioneReferti.visualizzaReferto(prenotazioneId);

        if (referto != null && !referto.getPrenotazione().getMedico().getId().equals(utente.getId())) {
            return "redirect:/medico?errore=AccessoNegato";
        }

        model.addAttribute("referto", referto);
        return "visualizzaRefertoMedico";
    }

    @PostMapping("/cambiaStato")
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/";

        // Sicurezza: aggiorna solo se la visita è assegnata al medico loggato
        Prenotazione p = gestionePrenotazioni.getPrenotazioneById(id);
        if (p != null && p.getMedico().getId().equals(utente.getId())) {
            gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        }

        return "redirect:/medico";
    }
}