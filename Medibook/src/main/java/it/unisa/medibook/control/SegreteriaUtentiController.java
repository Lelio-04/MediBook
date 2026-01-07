package it.unisa.medibook.control;

import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.model.SegreteriaUtenti;
import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/segreteria-utenti")
public class SegreteriaUtentiController {

    @Autowired
    private PazienteRepository pazienteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    // RIMOSSO PasswordEncoder perché salviamo in chiaro come richiesto

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
        return "segreteria_utenti"; // view/segreteria_utenti.jsp
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
    // 3. NUOVO
    // ============================================================
    @GetMapping("/nuovo")
    public String nuovoPaziente(HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";

        model.addAttribute("paziente", new Paziente());
        return "form_paziente"; // view/form_paziente.jsp
    }

    // ============================================================
    // 4. MODIFICA
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
    // 5. SALVATAGGIO (IN CHIARO)
    // ============================================================
    @PostMapping("/salva")
    public String salvaPaziente(@ModelAttribute Paziente p, HttpSession session) {
        if (!isAutorizzato(session)) return "redirect:/accedi";

        if (p.getId() == null) {
            // --- NUOVO PAZIENTE ---

            // Salviamo la password in CHIARO per compatibilità con il tuo GestioneUtenza
            String passwordDefault = "Medibook123";
            p.setPassword(passwordDefault);

            p.setRuolo("PAZIENTE");
            pazienteRepository.save(p);

            return "redirect:/segreteria-utenti/dashboard?msg=Utente creato! Password: " + passwordDefault;

        } else {
            // --- MODIFICA ---
            Paziente esistente = pazienteRepository.findById(p.getId()).orElse(null);
            if (esistente != null) {
                esistente.setNome(p.getNome());
                esistente.setCognome(p.getCognome());
                esistente.setCodiceFiscale(p.getCodiceFiscale());
                esistente.setEmail(p.getEmail());
                esistente.setTelefono(p.getTelefono());
                esistente.setIndirizzo(p.getIndirizzo());
                // Non tocchiamo password e ruolo
                pazienteRepository.save(esistente);
            }
            return "redirect:/segreteria-utenti/dashboard?msg=Aggiornato";
        }
    }
}