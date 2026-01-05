package it.unisa.medibook.model;

import it.unisa.medibook.model.Medico;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Disponibilita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer giornoSettimana;
    private LocalTime oraInizio;
    private LocalTime oraFine;

    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Medico medico;

    // --- AGGIUNGI QUESTI METODI ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getGiornoSettimana() { return giornoSettimana; }
    public void setGiornoSettimana(Integer giornoSettimana) { this.giornoSettimana = giornoSettimana; }

    public LocalTime getOraInizio() { return oraInizio; }
    public void setOraInizio(LocalTime oraInizio) { this.oraInizio = oraInizio; }

    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime oraFine) { this.oraFine = oraFine; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
}