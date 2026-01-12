package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Referto;
import org.springframework.data.jpa.repository.JpaRepository;
    //NUOVO
public interface RefertoRepository extends JpaRepository<Referto, Integer> {
    Referto findByPrenotazioneId(Integer prenotazioneId);
}