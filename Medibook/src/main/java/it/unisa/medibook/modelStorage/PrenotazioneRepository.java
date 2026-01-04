package it.unisa.medibook.modelStorage;

import it.unisa.medibook.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {

    // Per il Medico: vedere le sue visite
    List<Prenotazione> findByMedicoId(Integer medicoId);

    // Per il Paziente: vedere le sue visite
    List<Prenotazione> findByPazienteId(Integer pazienteId);

    // CONTROLLO DISPONIBILITÀ (Nuova Prenotazione)
    // "Esiste già una visita con questo medico, in questa data e ora?"
    boolean existsByMedicoIdAndDataAndOra(Integer medicoId, LocalDate data, LocalTime ora);

    // CONTROLLO DISPONIBILITÀ (Modifica Segreteria)
    // "Esiste già una visita... ESCLUDENDO quella che sto modificando (IdNot)?"
    boolean existsByMedicoIdAndDataAndOraAndIdNot(Integer medicoId, LocalDate data, LocalTime ora, Integer id);
}