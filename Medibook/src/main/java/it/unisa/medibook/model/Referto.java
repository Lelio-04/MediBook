package it.unisa.medibook.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "referto")
public class Referto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate dataCaricamento;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenuto; // Qui salviamo il testo del referto

    // Relazione 1-a-1: Un referto appartiene a una sola prenotazione
    @OneToOne
    @JoinColumn(name = "prenotazione_id", nullable = false, unique = true)
    private Prenotazione prenotazione;

    // Costruttore vuoto (obbligatorio per JPA)
    public Referto() {
    }

    // Costruttore utile per creare l'oggetto rapidamente
    public Referto(String contenuto, LocalDate dataCaricamento) {
        this.contenuto = contenuto;
        this.dataCaricamento = dataCaricamento;
    }

    // --- Getter e Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(LocalDate dataCaricamento) { this.dataCaricamento = dataCaricamento; }

    public String getContenuto() { return contenuto; }
    public void setContenuto(String contenuto) { this.contenuto = contenuto; }

    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }
}