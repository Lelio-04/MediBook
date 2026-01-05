package it.unisa.medibook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime; // Meglio di LocalDate per avere anche l'ora

@Entity
@Table(name = "referto")
public class Referto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime dataCaricamento; // Cambiato in LocalDateTime

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenuto;

    @OneToOne
    @JoinColumn(name = "prenotazione_id", nullable = false, unique = true)
    private Prenotazione prenotazione;

    public Referto() {
    }

    // Costruttore AGGIORNATO: ora chiede anche la Prenotazione
    public Referto(String contenuto, Prenotazione prenotazione) {
        this.contenuto = contenuto;
        this.prenotazione = prenotazione;
        this.dataCaricamento = LocalDateTime.now(); // Imposta l'ora attuale in automatico
    }

    // --- Getter e Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(LocalDateTime dataCaricamento) { this.dataCaricamento = dataCaricamento; }

    public String getContenuto() { return contenuto; }
    public void setContenuto(String contenuto) { this.contenuto = contenuto; }

    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }
}