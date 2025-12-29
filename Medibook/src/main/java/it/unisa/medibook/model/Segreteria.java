package it.unisa.medibook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "segreteria")
public class Segreteria extends Utente {

    // Se in futuro serviranno campi specifici (es. 'codiceDipendente'), li aggiungerai qui.
    // Per ora eredita tutto da Utente.

    public Segreteria() {
        super();
    }
}
