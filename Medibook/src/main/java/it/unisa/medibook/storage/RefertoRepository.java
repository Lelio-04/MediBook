package it.unisa.medibook.storage;

import it.unisa.medibook.model.Referto;
import org.springframework.data.jpa.repository.JpaRepository;
    //NUOVO
public interface RefertoRepository extends JpaRepository<Referto, Integer> {
    // Serve per recuperare il referto partendo dall'ID della visita
    Referto findByPrenotazioneId(Integer prenotazioneId);
}