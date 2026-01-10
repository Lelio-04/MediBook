package it.unisa.medibook.service;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.modelStorage.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestioneMedico {

    @Autowired
    private MedicoRepository medicoRepository;

    // Recupera tutti i medici (per la select della segreteria)
    public List<Medico> dammiTuttiIMedici() {
        return medicoRepository.findAll();
    }

    // Recupera un medico specifico
    public Medico getMedicoById(Integer id) {
        // Converto in Long perché la tua repository probabilmente usa Long come ID
        return medicoRepository.findById(Long.valueOf(id)).orElse(null);
    }
}