package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelService.EmailService;
import it.unisa.medibook.modelService.PasswordService; // <--- 1. IMPORT
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/segreteria-utenti")
public class SegreteriaUtentiController {

    @Autowired
    private PazienteRepository pazienteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService; // <--- 2. INIEZIONE PASSWORD SERVICE

    // --- METODO DI SICUREZZA ---
    private boolean isAutorizzato(HttpSession session) {
        Utente u = (Utente) session.getAttribute("utente");
        return u instanceof SegreteriaUtenti;
    }

    // ============================================================
    // 1. DASHBOARD
    // ============================================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";

        model.addAttribute("listaPazienti", pazienteRepository.findAll());
        return "segreteria_utenti";
    }

    // ============================================================
    // 2. ELIMINAZIONE
    // ============================================================
    @GetMapping("/elimina")
    public String eliminaPaziente(@RequestParam Integer id, HttpSession session) {
        if (!isAutorizzato(session)) return "redirect:/accedi";

        try {
            List<Prenotazione> visitePaziente = prenotazioneRepository.findByPazienteId(id);
            if (!visitePaziente.isEmpty()) {
                prenotazioneRepository.deleteAll(visitePaziente);
            }
            pazienteRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/segreteria-utenti/dashboard?errore=ErroreCancellazione";
        }

        return "redirect:/segreteria-utenti/dashboard?msg=Paziente eliminato";
    }

    // ============================================================
    // 3. NUOVO FORM
    // ============================================================
    @GetMapping("/nuovo")
    public String nuovoPaziente(HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        model.addAttribute("paziente", new Paziente());
        return "form_paziente";
    }

    // ============================================================
    // 4. MODIFICA FORM
    // ============================================================
    @GetMapping("/modifica")
    public String modificaPaziente(@RequestParam Integer id, HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        Paziente p = pazienteRepository.findById(id).orElse(null);
        if (p == null) return "redirect:/segreteria-utenti/dashboard";
        model.addAttribute("paziente", p);
        return "form_paziente";
    }

    // ============================================================
    // 5. SALVATAGGIO (CON HASH PASSWORD)
    // ============================================================
    @PostMapping("/salva")
    public String salvaPaziente(@ModelAttribute Paziente p,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (!isAutorizzato(session)) return "redirect:/accedi";

        // --- NUOVO PAZIENTE ---
        if (p.getId() == null) {
            String passwordProvvisoria = "Medibook123";

            // 3. CRIPTIAMO LA PASSWORD PRIMA DI SALVARE
            p.setPassword(passwordService.hash(passwordProvvisoria));

            p.setRuolo("PAZIENTE");

            pazienteRepository.save(p);

            // 4. INVIAMO L'EMAIL (Qui mandiamo la password IN CHIARO affinché l'utente la legga)
            try {
                if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                    emailService.inviaEmailBenvenuto(
                            p.getEmail(),
                            p.getNome(),
                            p.getCognome(),
                            passwordProvvisoria // Passiamo quella leggibile
                    );
                }
            } catch (Exception e) {
                System.err.println("Errore invio email benvenuto: " + e.getMessage());
            }

            return "redirect:/segreteria-utenti/dashboard?msg=Utente creato e Email inviata!";

        } else {
            // --- MODIFICA ESISTENTE ---
            Paziente esistente = pazienteRepository.findById(p.getId()).orElse(null);

            if (esistente != null) {
                esistente.setNome(p.getNome());
                esistente.setCognome(p.getCognome());
                esistente.setCodiceFiscale(p.getCodiceFiscale());
                esistente.setTelefono(p.getTelefono());
                esistente.setIndirizzo(p.getIndirizzo());

                // Nota: In modifica NON tocchiamo la password
                // La password rimane quella vecchia (già hashata)

                pazienteRepository.save(esistente);
            }
            return "redirect:/segreteria-utenti/dashboard?msg=Dati Paziente Aggiornati";
        }
    }
}