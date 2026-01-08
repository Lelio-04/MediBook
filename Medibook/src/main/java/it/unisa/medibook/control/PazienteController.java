package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.EmailService;
import it.unisa.medibook.modelService.GestioneMedico;
import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.modelService.GestioneReferti;
import it.unisa.medibook.modelStorage.PazienteRepository; // <--- 1. Import Repository
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
import java.util.stream.Collectors;

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
    private EmailService emailService;

    @Autowired
    private PazienteRepository pazienteRepository; // <--- 2. Iniezione Repository per salvare modifiche profilo

    // --- DASHBOARD ---
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

        model.addAttribute("listaMedici", gestioneMedico.dammiTuttiIMedici());

        return "paziente";
    }

    // --- PRENOTAZIONE VISITA ---
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

            // LOGICA INVIO EMAIL
            try {
                if (utente instanceof Paziente) {
                    Paziente p = (Paziente) utente;
                    Medico m = gestioneMedico.getMedicoById(idMedico);

                    if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                        String nomePaziente = p.getNome() + " " + p.getCognome();
                        String nomeMedico = (m != null) ? m.getCognome() : "Specialista";
                        String dataFormat = dataL.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                        emailService.inviaEmailConferma(p.getEmail(), nomePaziente, nomeMedico, dataFormat, oraL.toString());
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore email: " + e.getMessage());
            }

            redirectAttributes.addAttribute("successo", "true");

        } catch (Exception e) {
            redirectAttributes.addAttribute("errore", e.getMessage());
            redirectAttributes.addAttribute("prevIdMedico", idMedico);
            try {
                Medico m = gestioneMedico.getMedicoById(idMedico);
                if (m != null) redirectAttributes.addAttribute("prevNomeMedico", "Dr. " + m.getCognome());
            } catch (Exception ex) {}
        }

        return "redirect:/paziente";
    }

    // --- VISUALIZZA REFERTO ---
    @GetMapping("/referto")
    public String vediReferto(@RequestParam(name = "idVisita") Integer idVisita, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"PAZIENTE".equals(utente.getRuolo())) return "redirect:/accedi";

        Referto referto = gestioneReferti.visualizzaReferto(idVisita);

        if (referto == null) return "redirect:/paziente?errore=RefertoNonTrovato";
        if (!referto.getPrenotazione().getPaziente().getId().equals(utente.getId())) return "redirect:/paziente?errore=AccessoNegato";

        model.addAttribute("referto", referto);
        return "paziente_visualizza_referto";
    }

    // ==========================================================
    //  NOVITÀ: GESTIONE PROFILO
    // ==========================================================

    // 1. VISUALIZZA PAGINA PROFILO
    @GetMapping("/profilo")
    public String vediProfilo(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !(utente instanceof Paziente)) return "redirect:/";

        // Ricarichiamo i dati freschi dal DB
        Paziente p = pazienteRepository.findById(utente.getId()).orElse(null);
        model.addAttribute("paziente", p);

        return "paziente_profilo"; // Assicurati di aver creato questa JSP
    }

    // 2. SALVA MODIFICHE PROFILO E PASSWORD
    @PostMapping("/profilo/salva")
    public String salvaProfilo(@RequestParam String telefono,
                               @RequestParam String indirizzo,
                               @RequestParam(required = false) String nuovaPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !(utente instanceof Paziente)) return "redirect:/";

        try {
            Paziente p = pazienteRepository.findById(utente.getId()).orElseThrow();

            // Aggiorna dati modificabili
            p.setTelefono(telefono);
            p.setIndirizzo(indirizzo);

            // Cambio Password (Se il campo non è vuoto)
            if (nuovaPassword != null && !nuovaPassword.trim().isEmpty()) {
                p.setPassword(nuovaPassword);
                redirectAttributes.addFlashAttribute("successo", "Profilo e Password aggiornati con successo!");
            } else {
                redirectAttributes.addFlashAttribute("successo", "Dati di contatto aggiornati.");
            }

            // Salva nel DB
            pazienteRepository.save(p);

            // Aggiorna la sessione per vedere subito le modifiche nell'header
            session.setAttribute("utente", p);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", "Errore aggiornamento: " + e.getMessage());
        }

        return "redirect:/paziente/profilo";
    }

    // ==========================================================
    //  API REST PER AJAX
    // ==========================================================

    @GetMapping("/api/giorni-lavoro")
    @ResponseBody
    public List<Integer> getGiorniLavoro(@RequestParam Integer medicoId) {
        return gestionePrenotazioni.getGiorniLavorativi(medicoId);
    }

    @GetMapping("/api/orari-disponibili")
    @ResponseBody
    public List<LocalTime> getOrari(@RequestParam Integer medicoId, @RequestParam String data) {
        LocalDate dataScelta = LocalDate.parse(data);
        return gestionePrenotazioni.getOrariLiberi(medicoId, dataScelta);
    }
}