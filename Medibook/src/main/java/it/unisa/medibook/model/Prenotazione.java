package it.unisa.medibook.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime ora;

    @Column(nullable = false)
    private String stato;

    // --- Relazioni (Chiavi Esterne) ---

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    // --- NUOVA AGGIUNTA: Collegamento al Referto ---
    // mappedBy = "prenotazione" dice a JPA: "La chiave esterna non è qui, ma nell'altra tabella (Referto)"
    @OneToOne(mappedBy = "prenotazione", cascade = CascadeType.ALL)
    private Referto referto;

    @OneToOne(mappedBy = "prenotazione", cascade = CascadeType.ALL)
    private Recensione recensione;

    // Getter e Setter
    public Recensione getRecensione() {
        return recensione;
    }

    public void setRecensione(Recensione recensione) {
        this.recensione = recensione;
    }

    // --- Costruttori ---

    public Prenotazione() {
    }

    public Prenotazione(LocalDate data, LocalTime ora, String stato, Medico medico, Paziente paziente) {
        this.data = data;
        this.ora = ora;
        this.stato = stato;
        this.medico = medico;
        this.paziente = paziente;
    }

    // --- Getter e Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    // Getter e Setter per il REFERTO
    public Referto getReferto() {
        return referto;
    }

    public void setReferto(Referto referto) {
        this.referto = referto;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + id +
                ", data=" + data +
                ", ora=" + ora +
                ", stato='" + stato + '\'' +
                ", medico=" + (medico != null ? medico.getId() : "null") +
                ", paziente=" + (paziente != null ? paziente.getId() : "null") +
                '}';
    }
}