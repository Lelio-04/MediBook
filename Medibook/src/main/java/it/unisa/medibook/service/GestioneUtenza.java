package it.unisa.medibook.service;

import it.unisa.medibook.model.Paziente; // <--- Importante: Importa la classe Paziente
import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository; // <--- Importante: Repository specifico
import it.unisa.medibook.modelStorage.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestioneUtenza {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PazienteRepository pazienteRepository; // <--- AGGIUNTA 1: Ci serve per salvare i pazienti

    @Autowired
    private MedicoRepository medicoRepository;


    /**
     * AGGIUNTA 2: Metodo per registrare un nuovo paziente.
     * Imposta il ruolo fisso e salva nel DB.
     */
    public void registraPaziente(Paziente p) throws Exception {
        // Controllo se l'email esiste già (per sicurezza)
        Optional<Utente> esistente = utenteRepository.findByEmail(p.getEmail());
        if (esistente.isPresent()) {
            throw new Exception("Email già presente nel sistema.");
        }

        // Assegno forzatamente il ruolo "PAZIENTE" (così nessuno può registrarsi come medico da qui)
        p.setRuolo("PAZIENTE");

        // Salvo nel database (JPA riempirà sia la tabella 'utente' che 'paziente')
        pazienteRepository.save(p);
    }
    // Assicurati di avere @Autowired private MedicoRepository medicoRepository;

}