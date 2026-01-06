package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medico")
public class Medico extends Utente {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private String specializzazione;

    @Column(nullable = false)
    private String numeroAlbo;

    // --- NUOVO CAMPO: LA "STRINGA INTELLIGENTE" ---
    // Questo campo conterrà le regole dei turni.
    // Esempio: "1:09:00-13:00,3:15:00-19:00"
    // Significa: Lunedì (1) 9-13 E Mercoledì (3) 15-19.
    @Column(name = "turni", length = 500) // Lunghezza 500 per stare larghi
    private String turni;

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

    // --- GETTER E SETTER PER I TURNI ---

    public String getTurni() {
        return turni;
    }

    public void setTurni(String turni) {
        this.turni = turni;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                ", numeroAlbo='" + numeroAlbo + '\'' +
                ", turni='" + turni + '\'' +
                '}';
    }
}