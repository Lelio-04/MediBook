package it.unisa.medibook.model;

import jakarta.persistence.*; // Usa javax.persistence se hai una versione vecchia di Spring

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // FONDAMENTALE: Crea la strategia per le sottoclassi
@Table(name = "utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Questo campo serve a distinguere chi si è loggato (es. "MEDICO", "PAZIENTE", "SEGRETERIA")
    @Column(nullable = false)
    private String ruolo;

    // --- Costruttori ---

    // Costruttore vuoto (Obbligatorio per JPA)
    public Utente() {
    }

    // Costruttore con parametri (Utile per creare nuovi oggetti rapidamente)
    public Utente(String email, String password, String ruolo) {
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    // --- Getter e Setter (Manuali) ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    // --- ToString (Opzionale, utile per il debug) ---

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }
}