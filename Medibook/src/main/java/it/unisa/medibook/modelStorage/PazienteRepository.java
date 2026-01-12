package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Paziente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PazienteRepository extends JpaRepository<Paziente, Integer> {

    Optional<Paziente> findByCodiceFiscale(String codiceFiscale);
}