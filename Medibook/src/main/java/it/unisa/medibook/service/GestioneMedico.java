package it.unisa.medibook.service;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Recensione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import it.unisa.medibook.modelStorage.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestioneMedico {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private RecensioneRepository recensioneRepository;

    // Recupera tutti i medici (per la select della segreteria)
    public List<Medico> dammiTuttiIMedici() {
        return medicoRepository.findAll();
    }

    // Recupera un medico specifico
    public Medico getMedicoById(Integer id) {
        // Converto in Long perché la tua repository probabilmente usa Long come ID
        return medicoRepository.findById(Long.valueOf(id)).orElse(null);
    }
    // 2. LOGICA DI RICERCA (Snellisce RicercaController)
    public List<Medico> ricercaAvanzata(String query) {
        // Logica OCL/Business: Rimuoviamo prefissi che disturbano la ricerca nel DB
        String queryPulita = query.replaceAll("(?i)^dr\\.?\\s+|^dott\\.?\\s+", "");
        return medicoRepository.cercaGlobale(queryPulita);
    }

    // 3. LOGICA RECENSIONI E MEDIA (Snellisce RicercaController)
    public List<Recensione> getRecensioniPerMedico(Integer medicoId) {
        return recensioneRepository.findByMedicoId(medicoId);
    }

    public double calcolaMediaVoti(List<Recensione> recensioni) {
        if (recensioni == null || recensioni.isEmpty()) {
            return 0.0;
        }
        return recensioni.stream()
                .mapToInt(Recensione::getVoto)
                .average()
                .orElse(0.0);
    }
    public List<String> getSuggerimentiRicerca(String q) {
        if (q == null || q.length() < 2) return Collections.emptyList();

        List<Medico> medici = medicoRepository.cercaGlobale(q);
        List<String> suggerimenti = new ArrayList<>();

        for (Medico m : medici) {
            suggerimenti.add("Dr. " + m.getNome() + " " + m.getCognome());
            if (!suggerimenti.contains(m.getSpecializzazione())) {
                suggerimenti.add(m.getSpecializzazione());
            }
        }

        return suggerimenti.stream().distinct().limit(6).collect(Collectors.toList());
    }
}