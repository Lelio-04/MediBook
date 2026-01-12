package it.unisa.medibook.control;

import it.unisa.medibook.model.*;
import it.unisa.medibook.service.GestionePrenotazioni;
import it.unisa.medibook.service.GestioneReferti;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/segreteria-prenotazioni")
public class SegreteriaPrenotazioniController {

    @Autowired private GestionePrenotazioni gestionePrenotazioni;
    @Autowired private GestioneReferti gestioneReferti;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String q,
                            @RequestParam(required = false) String filtro,
                            HttpSession session, Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !(utente instanceof SegreteriaPrenotazioni)) return "redirect:/";


        model.addAttribute("listaPrenotazioni", gestionePrenotazioni.ricercaPrenotazioni(q, filtro));
        model.addAttribute("filtroAttivo", filtro);
        model.addAttribute("searchKeyword", q);
        model.addAttribute("oggi", LocalDate.now());

        return "dashboard-agenda";
    }

    @PostMapping("/aggiorna")
    public String aggiorna(@RequestParam Integer id,
                           @RequestParam LocalDate nuovaData,
                           @RequestParam LocalTime nuovaOra,
                           @RequestParam String nuovoStato,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        try {

            if ("CONCLUSA".equals(nuovoStato)) {
                gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, "EFFETTUATA");
                return "redirect:/segreteria-prenotazioni/referto/nuovo?id=" + id;
            }


            gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, nuovoStato);


            if (!"CANCELLATA".equals(nuovoStato)) {
                gestionePrenotazioni.inviaNotificheModifica(id);
            }

            redirectAttributes.addFlashAttribute("successo", "Prenotazione aggiornata correttamente!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/segreteria-prenotazioni/dashboard";
    }

    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        Prenotazione p = gestionePrenotazioni.getPrenotazioneById(id);
        if (p == null || !"EFFETTUATA".equals(p.getStato())) {
            return "redirect:/segreteria-prenotazioni/dashboard?errore=StatoNonValido";
        }

        model.addAttribute("prenotazione", p);
        return "segreteria_scrivi_referto";
    }

    @PostMapping("/referto/salva")
    public String salvaReferto(@RequestParam Integer prenotazioneId,
                               @RequestParam String contenuto,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        try {

            gestioneReferti.salvaNuovoReferto(prenotazioneId, contenuto);
            redirectAttributes.addFlashAttribute("successo", "Referto salvato!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
            return "redirect:/segreteria-prenotazioni/referto/nuovo?id=" + prenotazioneId;
        }

        return "redirect:/segreteria-prenotazioni/dashboard";
    }
}