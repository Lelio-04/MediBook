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
        // Il controller non sa come si creano i suggerimenti, lo chiede al service
        return gestioneMedico.getSuggerimentiRicerca(q);
    }

    @GetMapping("/api/cerca-medici-json")
    public List<Medico> cercaMediciJson(@RequestParam String q) {
        // Riutilizziamo la ricerca avanzata definita nel service
        return gestioneMedico.ricercaAvanzata(q);
    }
}