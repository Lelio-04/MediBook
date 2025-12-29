package it.unisa.medibook.storage;

import it.unisa.medibook.model.Referto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefertoRepository extends JpaRepository<Referto, Integer> {
    // Possiamo trovare un referto tramite l'ID della prenotazione associata
    Referto findByPrenotazioneId(Integer prenotazioneId);
}