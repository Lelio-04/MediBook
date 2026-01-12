package it.unisa.medibook.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int voto; // Da 1 a 5

    @Column(columnDefinition = "TEXT")
    private String commento;

    private LocalDate dataInserimento;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paziente_id")
    private Paziente paziente;

    @OneToOne
    @JoinColumn(name = "prenotazione_id")
    private Prenotazione prenotazione;


    public Recensione() {
        this.dataInserimento = LocalDate.now();
    }


    public Recensione(int voto, String commento, Medico medico, Paziente paziente) {
        this.voto = voto;
        this.commento = commento;
        this.medico = medico;
        this.paziente = paziente;
        this.dataInserimento = LocalDate.now();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }
    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
    public LocalDate getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(LocalDate dataInserimento) { this.dataInserimento = dataInserimento; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public Paziente getPaziente() { return paziente; }
    public void setPaziente(Paziente paziente) { this.paziente = paziente; }
    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }
}