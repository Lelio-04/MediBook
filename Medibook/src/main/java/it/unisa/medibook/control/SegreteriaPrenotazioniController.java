package it.unisa.medibook.control;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.GestionePrenotazioni;
import it.unisa.medibook.modelService.GestioneReferti; // <--- Importiamo il service Referti
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/segreteria-prenotazioni")
public class SegreteriaPrenotazioniController {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @Autowired
    private GestioneReferti gestioneReferti; // <--- Service Referti aggiunto

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String q, // Parametro di ricerca opzionale
                            HttpSession session,
                            Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) {
            return "redirect:/";
        }

        // 1. LOGICA DI RICERCA
        if (q != null && !q.trim().isEmpty()) {
            // Cerca sia nel nome che nel cognome
            model.addAttribute("listaPrenotazioni",
                    prenotazioneRepository.findByPazienteNomeContainingIgnoreCaseOrPazienteCognomeContainingIgnoreCase(q, q));
            model.addAttribute("searchKeyword", q); // Per mantenere il testo nella barra di ricerca
        } else {
            // Se non cerco nulla, mostra tutto
            model.addAttribute("listaPrenotazioni", prenotazioneRepository.findAll());
        }

        // 2. DATA DI OGGI PER IL BLOCCO DEL CALENDARIO
        model.addAttribute("oggi", LocalDate.now());

        return "dashboard-agenda";
    }

    // --- METODO INTELLIGENTE PER L'AGGIORNAMENTO ---
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
            // CASO SPECIALE: La segretaria ha selezionato "CONCLUSA"
            if ("CONCLUSA".equals(nuovoStato)) {

                // 1. Salviamo prima data e ora (e mettiamo stato EFFETTUATA per preparare il terreno al referto)
                // Nota: Mettiamo "EFFETTUATA" perché GestioneReferti.caricaReferto RICHIEDE che lo stato sia EFFETTUATA.
                gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, "EFFETTUATA");

                // 2. Reindirizziamo alla pagina per scrivere il referto
                return "redirect:/segreteria-prenotazioni/referto/nuovo?id=" + id;
            }

            // CASO NORMALE (Prenotata, Annullata, Effettuata senza referto immediato)
            else {
                gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, nuovoStato);
                redirectAttributes.addFlashAttribute("successo", "Prenotazione aggiornata (" + nuovoStato + ")!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/segreteria-prenotazioni/dashboard";
    }

    // --- GESTIONE REFERTO LATO SEGRETERIA ---

    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        Prenotazione p = prenotazioneRepository.findById(id).orElse(null);

        // Controllo di sicurezza
        if (p == null || !"EFFETTUATA".equals(p.getStato())) {
            return "redirect:/segreteria-prenotazioni/dashboard?errore=DeviPrimaImpostareEffettuata";
        }

        model.addAttribute("prenotazione", p);
        return "segreteria_scrivi_referto"; // Creiamo questa JSP sotto
    }

    @PostMapping("/referto/salva")
    public String salvaReferto(@RequestParam Integer prenotazioneId,
                               @RequestParam String contenuto,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        try {
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                    .orElseThrow(() -> new Exception("Prenotazione non trovata"));

            Referto r = new Referto();
            r.setContenuto(contenuto);
            r.setDataCaricamento(LocalDateTime.now());

            // Questo metodo del service salva il referto E imposta lo stato a "CONCLUSA"
            gestioneReferti.caricaReferto(r, p);

            redirectAttributes.addFlashAttribute("successo", "Referto salvato e visita Conclusa!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", "Errore salvataggio referto: " + e.getMessage());
            return "redirect:/segreteria-prenotazioni/referto/nuovo?id=" + prenotazioneId;
        }

        return "redirect:/segreteria-prenotazioni/dashboard";
    }

    @GetMapping("/cancella")
    public String cancella(@RequestParam Integer id, HttpSession session) {
        // ... (tuo codice esistente) ...
        prenotazioneRepository.deleteById(id);
        return "redirect:/segreteria-prenotazioni/dashboard";
    }
}