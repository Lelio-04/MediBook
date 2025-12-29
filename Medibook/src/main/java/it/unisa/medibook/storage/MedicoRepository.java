package it.unisa.medibook.storage;

import it.unisa.medibook.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

// Utile per il filtraggio medici descritto nell'ODD [cite: 139]
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
}