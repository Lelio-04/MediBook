package it.unisa.medibook.storage;

import it.unisa.medibook.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {

    // Serve per la funzionalità "Visione visite medico" richiesta
    // Corrisponde a doRetrieveByMedico nel tuo ODD [cite: 137]
    List<Prenotazione> findByMedicoId(Integer medicoId);
}