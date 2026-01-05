package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.modelService.GestioneReferti;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    // --- NUOVI AUTOWIRED PER I REFERTI ---
    @Autowired
    private GestioneReferti gestioneReferti;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    // --- DASHBOARD (Il tuo metodo originale) ---
    @GetMapping("")
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Medico) {
            Medico medico = (Medico) utente;
            model.addAttribute("nomeMedico", medico.getCognome());
            List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(medico.getId());
            model.addAttribute("visite", visite);
        }

        return "medico";
    }

    // --- CAMBIO STATO (Il tuo metodo originale) ---
    @PostMapping("/cambiaStato")
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato) {
        gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        return "redirect:/medico";
    }

    // --- NUOVO: MOSTRA FORM REFERTO ---
    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        // 1. Controllo Sicurezza
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        // 2. Recupero la prenotazione
        Prenotazione p = prenotazioneRepository.findById(id).orElse(null);

        // 3. Controllo validità (esiste? è effettuata?)
        if (p == null || !"EFFETTUATA".equals(p.getStato())) {
            // Se provo a refertare una visita non effettuata, torno alla dashboard con errore
            return "redirect:/medico?errore=VisitaNonValida";
        }

        model.addAttribute("prenotazione", p);
        return "medico_scrivi_referto"; // La JSP che abbiamo creato prima
    }

    // --- NUOVO: SALVA REFERTO ---
    @PostMapping("/referto/salva")
    public String salvaReferto(@RequestParam Integer prenotazioneId,
                               @RequestParam String contenuto,
                               HttpSession session,
                               Model model) {

        // 1. Controllo Sicurezza
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        try {
            // 2. Recupero Prenotazione
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                    .orElseThrow(() -> new Exception("Prenotazione non trovata"));

            // 3. Creo l'oggetto Referto (Il service vuole l'oggetto, non la stringa)
            Referto r = new Referto();
            r.setContenuto(contenuto);
            r.setDataCaricamento(LocalDateTime.now()); // Imposto data/ora attuali

            // 4. Chiamo il Service
            gestioneReferti.caricaReferto(r, p);

            // 5. Successo
            return "redirect:/medico?successo=RefertoSalvato";

        } catch (Exception e) {
            // Gestione Errori: ricarico la pagina del form con il messaggio d'errore
            model.addAttribute("errore", e.getMessage());

            // Ricarico l'oggetto prenotazione per non rompere la JSP
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId).orElse(null);
            model.addAttribute("prenotazione", p);

            return "medico_scrivi_referto";
        }
    }
}