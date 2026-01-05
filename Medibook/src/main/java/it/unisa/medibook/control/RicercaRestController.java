package it.unisa.medibook.control;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.modelStorage.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController // Restituisce JSON, non HTML
public class RicercaRestController {

    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping("/api/suggerimenti")
    public List<String> getSuggerimenti(@RequestParam String q) {
        if (q == null || q.length() < 2) {
            return new ArrayList<>(); // Non cercare se meno di 2 lettere
        }

        // Cerca nel DB (nomi, cognomi o specializzazioni)
        List<Medico> medici = medicoRepository.cercaGlobale(q);

        // Trasformiamo gli oggetti Medico in una lista di stringhe da mostrare
        List<String> suggerimenti = new ArrayList<>();

        for (Medico m : medici) {
            // Aggiungi il nome completo
            suggerimenti.add("Dr. " + m.getNome() + " " + m.getCognome());

            // Aggiungi la specializzazione se non è già presente nella lista
            if (!suggerimenti.contains(m.getSpecializzazione())) {
                suggerimenti.add(m.getSpecializzazione());
            }
        }

        // Limitiamo a max 5-6 risultati per non fare una lista infinita
        return suggerimenti.stream().distinct().limit(6).collect(Collectors.toList());
    }
    // Nel file: it.unisa.medibook.control.RicercaRestController.java

    @GetMapping("/api/cerca-medici-json")
    public List<Medico> cercaMediciJson(@RequestParam String q) {
        if (q == null || q.length() < 2) {
            return new ArrayList<>();
        }
        // Usa la stessa logica di ricerca globale che hai già
        return medicoRepository.cercaGlobale(q.trim());
    }
}