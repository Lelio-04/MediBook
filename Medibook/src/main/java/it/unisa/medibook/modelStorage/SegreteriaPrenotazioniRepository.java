package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.SegreteriaPrenotazioni;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SegreteriaPrenotazioniRepository extends JpaRepository<SegreteriaPrenotazioni, Integer> {
    // Trova solo le segretarie addette alle prenotazioni
}