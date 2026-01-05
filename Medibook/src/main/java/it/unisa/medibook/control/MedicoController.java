package it.unisa.medibook.control;

import com.fasterxml.jackson.databind.ObjectMapper; // <--- FONDAMENTALE PER IL JSON
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @Autowired
    private GestioneReferti gestioneReferti;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    // --- DASHBOARD ---
    @GetMapping("")
    public String dashboardMedico(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        if (utente instanceof Medico) {
            Medico medico = (Medico) utente;
            model.addAttribute("nomeMedico", medico.getCognome());
            // Qui lasciamo la visualizzazione tabellare classica
            List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisiteMedico(medico.getId());
            model.addAttribute("visite", visite);
        }

        return "medico"; // Assicurati che il file JSP si chiami 'medico.jsp'
    }

    // --- NUOVO: VISTA CALENDARIO ---
    @GetMapping("/calendario")
    public String visualizzaCalendario(HttpSession session, Model model) {
        // 1. Controllo Sicurezza
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) {
            return "redirect:/login";
        }
        Medico medico = (Medico) utente;

        // 2. Recupero SOLO le visite future/confermate ("PRENOTATA")
        List<Prenotazione> visite = gestionePrenotazioni.visualizzaVisitePerCalendario(medico.getId());

        // 3. Preparazione dati per FullCalendar
        List<Map<String, Object>> eventiCalendario = new ArrayList<>();

        for (Prenotazione p : visite) {
            Map<String, Object> evento = new HashMap<>();

            // Dati base per FullCalendar
            evento.put("title", p.getPaziente().getCognome() + " " + p.getPaziente().getNome());
            evento.put("start", p.getData().toString() + "T" + p.getOra().toString());
            evento.put("backgroundColor", "#28a745");
            evento.put("borderColor", "#28a745");

            // --- DATI EXTRA PER IL POPUP ---
            // Passiamo l'ID per creare il link
            evento.put("id", p.getId());
            // Passiamo anche lo stato o altre info se servono
            evento.put("extendedProps", Map.of(
                    "stato", p.getStato(),
                    "codiceFiscale", p.getPaziente().getCodiceFiscale()
            ));

            eventiCalendario.add(evento);
        }

        // 4. Conversione in JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(eventiCalendario);
            model.addAttribute("eventiJson", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("eventiJson", "[]"); // Evita crash della pagina se fallisce il JSON
        }

        // Restituisce la vista JSP del calendario
        // NB: Assicurati di salvare il file jsp in: /WEB-INF/jsp/medico/calendarioMedico.jsp
        // O semplicemente in /view/calendarioMedico.jsp a seconda della tua configurazione
        return "medicoCalendario";
    }

    // --- CAMBIO STATO ---
    @PostMapping("/cambiaStato")
    public String cambiaStato(@RequestParam Integer id, @RequestParam String stato) {
        gestionePrenotazioni.aggiornaStatoVisita(id, stato);
        return "redirect:/medico";
    }

    // --- FORM NUOVO REFERTO ---
    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        Prenotazione p = prenotazioneRepository.findById(id).orElse(null);

        if (p == null || !"EFFETTUATA".equals(p.getStato())) {
            return "redirect:/medico?errore=VisitaNonValida";
        }

        model.addAttribute("prenotazione", p);
        return "medico_scrivi_referto";
    }

    // --- SALVA REFERTO ---
    @PostMapping("/referto/salva")
    public String salvaReferto(@RequestParam Integer prenotazioneId,
                               @RequestParam String contenuto,
                               HttpSession session,
                               Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"MEDICO".equals(utente.getRuolo())) return "redirect:/accedi";

        try {
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                    .orElseThrow(() -> new Exception("Prenotazione non trovata"));

            Referto r = new Referto();
            r.setContenuto(contenuto);
            r.setDataCaricamento(LocalDateTime.now());

            gestioneReferti.caricaReferto(r, p);

            return "redirect:/medico?successo=RefertoSalvato";

        } catch (Exception e) {
            model.addAttribute("errore", e.getMessage());
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId).orElse(null);
            model.addAttribute("prenotazione", p);
            return "medico_scrivi_referto";
        }
    }

    // --- VISUALIZZA REFERTO ---
    @GetMapping("/referto/visualizza")
    public String visualizza(@RequestParam("id") Integer prenotazioneId, Model model) {
        Referto referto = gestioneReferti.visualizzaReferto(prenotazioneId);
        model.addAttribute("referto", referto);

        // NB: Controlla che il nome del file JSP sia esatto
        return "visualizzaRefertoMedico";
    }
}