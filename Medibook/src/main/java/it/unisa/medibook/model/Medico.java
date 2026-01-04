package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medico")
public class Medico extends Utente {

    @Column(nullable = false)
    private String nome;     // <--- AGGIUNTO

    @Column(nullable = false)
    private String cognome;  // <--- AGGIUNTO

    @Column(nullable = false)
    private String specializzazione;

    @Column(nullable = false)
    private String numeroAlbo;

    // --- Costruttori ---

    public Medico() {
        super();
    }

    // --- Getter e Setter ---

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

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

    @Override
    public String toString() {
        return "Medico{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                ", numeroAlbo='" + numeroAlbo + '\'' +
                '}';
    }
}