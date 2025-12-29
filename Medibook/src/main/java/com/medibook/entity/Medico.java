package com.medibook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Medico implements Serializable {
    @Id
    private int numeroAlboMedici;
    private String specialistica;
    private String nome;
    private String cognome;
    private String email;
    private String password;

    public Medico() {
    }

    public Medico(int numeroAlboMedici, String specialistica, String nome, String cognome, String email, String password) {
        this.numeroAlboMedici = numeroAlboMedici;
        this.specialistica = specialistica;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public int getNumeroAlboMedici() {
        return numeroAlboMedici;
    }

    public String getSpecialistica() {
        return specialistica;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setNumeroAlboMedici(int numeroAlboMedici) {
        this.numeroAlboMedici = numeroAlboMedici;
    }

    public void setSpecialistica(String specialistica) {
        this.specialistica = specialistica;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "numeroAlboMedici=" + numeroAlboMedici +
                ", specialistica='" + specialistica + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", cassword='" + password + '\'' +
                '}';
    }
}
