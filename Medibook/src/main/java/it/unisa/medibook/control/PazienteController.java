package it.unisa.medibook.control;

import it.unisa.medibook.model.*;
import it.unisa.medibook.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/paziente")
public class PazienteController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @Autowired
    private GestioneMedico gestioneMedico;

    @Autowired
    private GestioneReferti gestioneReferti;

    @Autowired
    private GestioneUtenza gestioneUtenza; // <--- NUOVO SERVICE

    @Autowired
    private EmailService emailService;

    // --- DASHBOARD ---
    @GetMapping("")
    public String dashboardPaziente(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // CORREZIONE QUI: Controllo e Casting
        if (utente instanceof Paziente) {
            Paziente p = (Paziente) utente; // <--- Casting necessario

            // Ora puoi chiamare getNome() perché 'p' è di tipo Paziente
            model.addAttribute("nomePaziente", p.getNome() + " " + p.getCognome());

            // Passiamo l'ID al service
            model.addAttribute("visiteFuture", gestionePrenotazioni.getVisiteFuture(p.getId()));
            model.addAttribute("storicoVisite", gestionePrenotazioni.getVisiteStorico(p.getId()));
        }

        model.addAttribute("listaMedici", gestioneMedico.dammiTuttiIMedici());

        return "paziente";
    }

    // --- PRENOTAZIONE ---
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

            // 1. Chiamata al Service
            gestionePrenotazioni.nuovaPrenotazione(utente.getId(), idMedico, dataL, oraL);

            // 2. Invio Email (Gestito tramite helper privato per pulizia)
            inviaEmailConfermaHelper((Paziente) utente, idMedico, dataL, oraL);

            redirectAttributes.addAttribute("successo", "true");

        } catch (Exception e) {
            redirectAttributes.addAttribute("errore", e.getMessage());
            // Manteniamo i dati inseriti per la UX
            redirectAttributes.addAttribute("prevIdMedico", idMedico);
            try {
                Medico m = gestioneMedico.getMedicoById(idMedico);
                if (m != null) redirectAttributes.addAttribute("prevNomeMedico", "Dr. " + m.getCognome());
            } catch (Exception ex) {}
        }

        return "redirect:/paziente";
    }

    // --- REFERTO ---
    @GetMapping("/referto")
    public String vediReferto(@RequestParam(name = "idVisita") Integer idVisita, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) return "redirect:/accedi";

        Referto referto = gestioneReferti.visualizzaReferto(idVisita);

        if (referto == null) return "redirect:/paziente?errore=RefertoNonTrovato";

        // Controllo di sicurezza
        if (!referto.getPrenotazione().getPaziente().getId().equals(utente.getId())) {
            return "redirect:/paziente?errore=AccessoNegato";
        }

        model.addAttribute("referto", referto);
        return "paziente_visualizza_referto";
    }

    // --- PROFILO (VISUALIZZA) ---
    @GetMapping("/profilo")
    public String vediProfilo(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !(utente instanceof Paziente)) return "redirect:/";

        // Usiamo il service per recuperare il paziente aggiornato (non la repo)
        Paziente p = gestioneUtenza.getPazienteById(utente.getId());
        model.addAttribute("paziente", p);

        return "paziente_profilo";
    }

    // --- PROFILO (SALVA MODIFICHE) ---
    @PostMapping("/profilo/salva")
    public String salvaProfilo(@RequestParam String telefono,
                               @RequestParam String indirizzo,
                               @RequestParam(required = false) String nuovaPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !(utente instanceof Paziente)) return "redirect:/";

        try {
            // TUTTA LA LOGICA (Hash password, set dati, save) è ora qui dentro:
            Paziente pAggiornato = gestioneUtenza.aggiornaProfiloPaziente(utente.getId(), telefono, indirizzo, nuovaPassword);

            // Aggiorna la sessione con i nuovi dati
            session.setAttribute("utente", pAggiornato);

            redirectAttributes.addFlashAttribute("successo", "Profilo aggiornato con successo!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", "Errore aggiornamento: " + e.getMessage());
        }

        return "redirect:/paziente/profilo";
    }

    // --- RECENSIONE ---
    @PostMapping("/recensione/salva")
    public String salvaRecensione(@RequestParam Long idPrenotazione,
                                  @RequestParam int voto,
                                  @RequestParam String commento,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (!(utente instanceof Paziente)) return "redirect:/accedi";

        try {
            // Delega completa al service
            gestionePrenotazioni.salvaRecensione(idPrenotazione, voto, commento, (Paziente) utente);
            redirectAttributes.addAttribute("successo", "RecensioneInviata");
        } catch (Exception e) {
            redirectAttributes.addAttribute("errore", e.getMessage());
        }

        return "redirect:/paziente";
    }

    // --- API AJAX ---
    @GetMapping("/api/giorni-lavoro")
    @ResponseBody
    public List<Integer> getGiorniLavoro(@RequestParam Integer medicoId) {
        return gestionePrenotazioni.getGiorniLavorativi(medicoId);
    }

    @GetMapping("/api/orari-disponibili")
    @ResponseBody
    public List<LocalTime> getOrari(@RequestParam Integer medicoId, @RequestParam String data) {
        // Parsing minimo necessario per chiamare il service
        return gestionePrenotazioni.getOrariLiberi(medicoId, LocalDate.parse(data));
    }

    // =================================================================================
    // PRIVATE HELPERS (Per tenere puliti i metodi pubblici)
    // =================================================================================

    private void inviaEmailConfermaHelper(Paziente p, Integer idMedico, LocalDate data, LocalTime ora) {
        try {
            if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                Medico m = gestioneMedico.getMedicoById(idMedico);
                String nomePaziente = p.getNome() + " " + p.getCognome();
                String nomeMedico = (m != null) ? m.getCognome() : "Specialista";
                String dataFormat = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                emailService.inviaEmailConferma(p.getEmail(), nomePaziente, nomeMedico, dataFormat, ora.toString());
            }
        } catch (Exception e) {
            System.err.println("Errore non bloccante invio email: " + e.getMessage());
        }
    }
}