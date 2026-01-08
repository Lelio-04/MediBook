package it.unisa.medibook.modelStorage;


import it.unisa.medibook.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {
    // Trova tutte le recensioni di un medico
    List<Recensione> findByMedicoId(Long medicoId);
}