package it.unisa.medibook.storage;

import it.unisa.medibook.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {

    // Serve per la funzionalità "Visione visite medico" richiesta
    // Corrisponde a doRetrieveByMedico nel tuo ODD [cite: 137]
    List<Prenotazione> findByMedicoId(Integer medicoId);

    // Serve per trovare le visite di un paziente       NUOVO
    List<Prenotazione> findByPazienteId(Integer pazienteId);

    // Query per verificare sovrapposizioni (TCS Req: Slot Occupato)
    boolean existsByMedicoIdAndDataAndOraAndIdNot(Integer medicoId, LocalDate data, LocalTime ora, Integer idDaEscludere);
}