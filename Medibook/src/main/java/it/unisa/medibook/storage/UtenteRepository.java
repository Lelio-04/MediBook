package it.unisa.medibook.storage;

import it.unisa.medibook.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    // Corrisponde al metodo doRetrieveByEmail definito nel tuo ODD [cite: 136]
    // Spring implementa automaticamente la query: SELECT * FROM utente WHERE email = ?
    Optional<Utente> findByEmail(String email);
}