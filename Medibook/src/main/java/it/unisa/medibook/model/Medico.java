package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medico")
public class Medico extends Utente {

    @Column(nullable = false)
    private String specializzazione;

    @Column(nullable = false)
    private String numeroAlbo;

    // --- Costruttori ---

    public Medico() {
        super();
    }

    // --- Getter e Setter ---

    public String getSpecializzazione() {
        return specializzazione;
    }

    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }

    public String getNumeroAlbo() {
        return numeroAlbo;
    }

    public void setNumeroAlbo(String numeroAlbo) {
        this.numeroAlbo = numeroAlbo;
    }
}