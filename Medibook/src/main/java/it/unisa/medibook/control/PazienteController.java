package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico; // <--- Importante: Import aggiunto
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
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
import java.util.List;
import java.util.stream.Collectors;

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

        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Paziente) {
            Paziente paziente = (Paziente) utente;
            model.addAttribute("nomePaziente", paziente.getNome() + " " + paziente.getCognome());

            List<Prenotazione> tutteLeVisite = gestionePrenotazioni.visualizzaVisitePaziente(paziente.getId());

            List<Prenotazione> future = tutteLeVisite.stream()
                    .filter(v -> "PRENOTATA".equals(v.getStato()))
                    .collect(Collectors.toList());

            List<Prenotazione> storico = tutteLeVisite.stream()
                    .filter(v -> "EFFETTUATA".equals(v.getStato()) ||
                            "CONCLUSA".equals(v.getStato()) ||
                            "ANNULLATA".equals(v.getStato()))
                    .collect(Collectors.toList());

            model.addAttribute("visiteFuture", future);
            model.addAttribute("storicoVisite", storico);
        }

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

            redirectAttributes.addAttribute("successo", "true");

        } catch (Exception e) {
            // *** GESTIONE ERRORE CON MANTENIMENTO DATI ***
            System.out.println("Errore prenotazione: " + e.getMessage());
            redirectAttributes.addAttribute("errore", e.getMessage());

            // 1. Rimandiamo indietro l'ID del medico
            redirectAttributes.addAttribute("prevIdMedico", idMedico);

            // 2. Recuperiamo il nome del medico per riempire la casella di testo
            try {
                Medico m = gestionePrenotazioni.getMedicoById(idMedico);
                if (m != null) {
                    String labelMedico = "Dr. " + m.getCognome() + " (" + m.getSpecializzazione() + ")";
                    redirectAttributes.addAttribute("prevNomeMedico", labelMedico);
                }
            } catch (Exception ex) {
                // Se fallisce il recupero del nome, pazienza
            }
        }

        return "redirect:/paziente";
    }

    @GetMapping("/referto")
    public String vediReferto(@RequestParam(name = "idVisita") Integer idVisita, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) return "redirect:/accedi";

        Referto referto = gestioneReferti.visualizzaReferto(idVisita);

        if (referto == null) {
            return "redirect:/paziente?errore=RefertoNonTrovato";
        }

        if (!referto.getPrenotazione().getPaziente().getId().equals(utente.getId())) {
            return "redirect:/paziente?errore=AccessoNegato";
        }

        model.addAttribute("referto", referto);

        return "paziente_visualizza_referto";
    }
}