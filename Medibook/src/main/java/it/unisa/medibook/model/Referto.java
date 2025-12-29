package it.unisa.medibook.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "referto")
public class Referto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate dataCaricamento;

    @Column(columnDefinition = "TEXT")
    private String contenuto; // Qui scriveremo la diagnosi o il path del file PDF

    // Relazione 1-a-1: Un referto appartiene a una specifica prenotazione
    @OneToOne
    @JoinColumn(name = "prenotazione_id", nullable = false)
    private Prenotazione prenotazione;

    public Referto() {}

    public Referto(LocalDate dataCaricamento, String contenuto, Prenotazione prenotazione) {
        this.dataCaricamento = dataCaricamento;
        this.contenuto = contenuto;
        this.prenotazione = prenotazione;
    }

    // Getter e Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(LocalDate dataCaricamento) { this.dataCaricamento = dataCaricamento; }
    public String getContenuto() { return contenuto; }
    public void setContenuto(String contenuto) { this.contenuto = contenuto; }
    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }
}