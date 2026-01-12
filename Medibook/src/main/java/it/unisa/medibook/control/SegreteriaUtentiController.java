package it.unisa.medibook.control;

import it.unisa.medibook.model.*;
import it.unisa.medibook.service.GestioneUtenza;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/segreteria-utenti")
public class SegreteriaUtentiController {

    @Autowired
    private GestioneUtenza gestioneUtenza;

    private boolean isAutorizzato(HttpSession session) {
        Utente u = (Utente) session.getAttribute("utente");
        return u instanceof SegreteriaUtenti;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        model.addAttribute("listaPazienti", gestioneUtenza.dammiTuttiIPazienti());
        return "segreteria_utenti";
    }

    @GetMapping("/elimina")
    public String eliminaPaziente(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        try {
            gestioneUtenza.eliminaPaziente(id);
            ra.addFlashAttribute("successo", "Paziente eliminato con successo.");
        } catch (Exception e) {
            ra.addFlashAttribute("errore", "Errore nella cancellazione.");
        }
        return "redirect:/segreteria-utenti/dashboard";
    }

    @GetMapping("/nuovo")
    public String nuovoPaziente(HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        model.addAttribute("paziente", new Paziente());
        return "form_paziente";
    }

    @GetMapping("/modifica")
    public String modificaPaziente(@RequestParam Integer id, HttpSession session, Model model) {
        if (!isAutorizzato(session)) return "redirect:/accedi";
        Paziente p = gestioneUtenza.getPazienteById(id);
        if (p == null) return "redirect:/segreteria-utenti/dashboard";
        model.addAttribute("paziente", p);
        return "form_paziente";
    }

    @PostMapping("/salva")
    public String salvaPaziente(@ModelAttribute Paziente p, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isAutorizzato(session)) return "redirect:/accedi";

        try {
            gestioneUtenza.salvaOAggiornaPaziente(p);
            ra.addFlashAttribute("successo", "Operazione completata con successo!");
            return "redirect:/segreteria-utenti/dashboard";
        } catch (Exception e) {
            // Logica semplificata: l'eccezione ora viene dal Service
            model.addAttribute("paziente", p);
            model.addAttribute("errore", e.getMessage().contains("Duplicate") ?
                    "Email o Codice Fiscale già presenti!" : e.getMessage());
            return "form_paziente";
        }
    }
}