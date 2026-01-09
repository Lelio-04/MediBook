package it.unisa.medibook.control;

import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.Referto;
import it.unisa.medibook.model.SegreteriaPrenotazioni;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.EmailService; // <--- 1. Import EmailService
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // <--- Import Formatter

@Controller
@RequestMapping("/segreteria-prenotazioni")
public class SegreteriaPrenotazioniController {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private GestionePrenotazioni gestionePrenotazioni;

    @Autowired
    private GestioneReferti gestioneReferti;

    @Autowired
    private EmailService emailService; // <--- 2. Iniezione del Service Email

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String q,
                            @RequestParam(required = false) String filtro, // <--- NUOVO PARAMETRO
                            HttpSession session,
                            Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !(utente instanceof SegreteriaPrenotazioni)) {
            return "redirect:/";
        }

        // LOGICA DI FILTRAGGIO
        if ("oggi".equals(filtro)) {
            // CASO 1: Filtro OGGI
            model.addAttribute("listaPrenotazioni", prenotazioneRepository.findByData(LocalDate.now()));
            model.addAttribute("filtroAttivo", "oggi"); // Per evidenziare il bottone o mostrare messaggi
        }
        else if (q != null && !q.trim().isEmpty()) {
            // CASO 2: Ricerca per testo
            model.addAttribute("listaPrenotazioni",
                    prenotazioneRepository.findByPazienteNomeContainingIgnoreCaseOrPazienteCognomeContainingIgnoreCase(q, q));
            model.addAttribute("searchKeyword", q);
        }
        else {
            // CASO 3: Mostra tutto (Default)
            model.addAttribute("listaPrenotazioni", prenotazioneRepository.findAll());
        }

        model.addAttribute("oggi", LocalDate.now());
        return "dashboard-agenda";
    }

    // --- AGGIORNAMENTO CON INVIO EMAIL ---
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
            // CASO 1: CONCLUSA -> Va al Referto
            if ("CONCLUSA".equals(nuovoStato)) {
                gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, "EFFETTUATA");
                return "redirect:/segreteria-prenotazioni/referto/nuovo?id=" + id;
            }

            // CASO 2: MODIFICA NORMALE -> Aggiorna e Invia Email
            else {
                // 1. Eseguiamo la modifica
                gestionePrenotazioni.modificaPrenotazione(id, nuovaData, nuovaOra, nuovoStato);
                redirectAttributes.addFlashAttribute("successo", "Prenotazione aggiornata (" + nuovoStato + ")!");

                // 2. Recuperiamo la prenotazione aggiornata per mandare l'email
                // (Evitiamo di mandare email se è stata cancellata, se preferisci)
                if (!"CANCELLATA".equals(nuovoStato)) {
                    Prenotazione p = prenotazioneRepository.findById(id).orElse(null);
                    if (p != null) {
                        inviaEmailModifica(p); // <--- 3. Chiamata al metodo helper
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/segreteria-prenotazioni/dashboard";
    }

    // --- GESTIONE REFERTO ---

    @GetMapping("/referto/nuovo")
    public String showNuovoReferto(@RequestParam Integer id, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null || !"SEGRETERIA".equals(utente.getRuolo())) return "redirect:/";

        Prenotazione p = prenotazioneRepository.findById(id).orElse(null);

        if (p == null || !"EFFETTUATA".equals(p.getStato())) {
            return "redirect:/segreteria-prenotazioni/dashboard?errore=DeviPrimaImpostareEffettuata";
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
            Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                    .orElseThrow(() -> new Exception("Prenotazione non trovata"));

            Referto r = new Referto();
            r.setContenuto(contenuto);
            r.setDataCaricamento(LocalDateTime.now());

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
        prenotazioneRepository.deleteById(id);
        return "redirect:/segreteria-prenotazioni/dashboard";
    }

    private void inviaEmailModifica(Prenotazione p) {

        String dataFormat = p.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String oraFormat = p.getOra().toString();
        String nomePaziente = p.getPaziente().getNome() + " " + p.getPaziente().getCognome();
        String nomeMedico = p.getMedico().getCognome();

        // 1. INVIO AL PAZIENTE
        try {
            String emailPaziente = p.getPaziente().getEmail();
            if (emailPaziente != null && !emailPaziente.isEmpty()) {
                String oggetto = "⚠️ Modifica Appuntamento - MediBook";

                emailService.inviaEmailModifica(
                        emailPaziente,
                        oggetto,
                        nomePaziente,
                        nomeMedico,
                        dataFormat,
                        oraFormat
                );
            }
        } catch (Exception e) {
            System.err.println("Errore invio email paziente: " + e.getMessage());
        }

        // 2. INVIO AL MEDICO (NUOVO)
        try {
            String emailMedico = p.getMedico().getEmail();
            // Controlliamo che il medico abbia una mail
            if (emailMedico != null && !emailMedico.isEmpty()) {

                emailService.inviaEmailModificaMedico(
                        emailMedico,
                        nomePaziente,
                        dataFormat,
                        oraFormat
                );
            }
        } catch (Exception e) {
            System.err.println("Errore invio email medico: " + e.getMessage());
        }
    }
}