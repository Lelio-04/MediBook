package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Paziente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PazienteRepository extends JpaRepository<Paziente, Integer> {

    // Questo metodo serve per controllare se un CF esiste già durante la registrazione
    // Spring genera automaticamente la query: SELECT * FROM paziente WHERE codice_fiscale = ?
}