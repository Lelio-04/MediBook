package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SEG_UTENTI")
public class SegreteriaUtenti extends Utente {

    public SegreteriaUtenti() {
        super();
    }


    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

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
}