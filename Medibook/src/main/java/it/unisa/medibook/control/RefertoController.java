package it.unisa.medibook.controller;

import it.unisa.medibook.model.Referto;
import it.unisa.medibook.modelService.GestioneReferti;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/medico/referto") // 1. Parte iniziale dell'URL
public class RefertoController {

    @Autowired
    private GestioneReferti gestioneReferti; // Colleghiamo il tuo Service

    /**
     * Risponde alla richiesta GET: /medico/referto/visualizza?id=123
     */
    @GetMapping("/visualizza")
    public String visualizza(@RequestParam("id") Integer prenotazioneId, Model model) {

        Referto referto = gestioneReferti.visualizzaReferto(prenotazioneId);
        model.addAttribute("referto", referto);

        return "visualizzaRefertoMedico";

    }
}