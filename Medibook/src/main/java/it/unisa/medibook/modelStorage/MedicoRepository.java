package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Query("SELECT m FROM Medico m WHERE " +
            "LOWER(m.nome) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(m.cognome) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(m.specializzazione) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(CONCAT(m.nome, ' ', m.cognome)) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(CONCAT(m.cognome, ' ', m.nome)) LIKE LOWER(CONCAT('%', :q, '%'))"
    )
    List<Medico> cercaGlobale(@Param("q") String q);
}