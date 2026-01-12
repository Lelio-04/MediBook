package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "paziente")

public class Paziente extends Utente {

    @Column(nullable = false, unique = true)
    private String codiceFiscale;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    private String indirizzo;
    private String telefono;



    public Paziente() {
        super();
    }



    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

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

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}