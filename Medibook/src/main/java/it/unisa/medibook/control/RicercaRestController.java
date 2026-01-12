package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.service.GestioneMedico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RicercaRestController {

    @Autowired
    private GestioneMedico gestioneMedico; // <--- Unico Service

    @GetMapping("/api/suggerimenti")
    public List<String> getSuggerimenti(@RequestParam String q) {

        return gestioneMedico.getSuggerimentiRicerca(q);
    }

    @GetMapping("/api/cerca-medici-json")
    public List<Medico> cercaMediciJson(@RequestParam String q) {

        return gestioneMedico.ricercaAvanzata(q);
    }
}