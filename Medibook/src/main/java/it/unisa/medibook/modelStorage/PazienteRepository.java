package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Paziente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PazienteRepository extends JpaRepository<Paziente, Integer> {

    // 1. Metodo per il controllo booleano (Veloce ed efficiente)
    // Genera SQL: SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Paziente p WHERE p.codiceFiscale = ?1
    boolean existsByCodiceFiscale(String codiceFiscale);

    // 2. Metodo per recuperare l'intero oggetto tramite CF (Opzionale, ma utile)
    // Genera SQL: SELECT * FROM paziente WHERE codice_fiscale = ?
    Optional<Paziente> findByCodiceFiscale(String codiceFiscale);
}