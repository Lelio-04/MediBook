package it.unisa.medibook.presentation;

import it.unisa.medibook.business.GestioneUtenza;
import it.unisa.medibook.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Permette a React di chiamare questo backend
public class AuthService {

    @Autowired
    private GestioneUtenza gestioneUtenza;

    // Endpoint: POST /api/auth/login
    // Riceve un JSON { "email": "...", "password": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenziali) {
        String email = credenziali.get("email");
        String password = credenziali.get("password");

        Utente utente = gestioneUtenza.login(email, password);

        if (utente != null) {
            // Restituisce l'oggetto utente (incluso il ruolo) se il login ha successo
            return ResponseEntity.ok(utente);
        } else {
            return ResponseEntity.status(401).body("Email o password errati");
        }
    }
}