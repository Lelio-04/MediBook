package it.unisa.medibook.modelService;

import it.unisa.medibook.model.Utente;
import it.unisa.medibook.modelStorage.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GestioneUtenza {

    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Effettuv il login verificando email e password.
     * Corrisponde all'operazione login() definita nell'ODD[cite: 198].
     */
    public Utente login(String email, String password) {
        // Cerca l'utente nel DB tramite email
        Optional<Utente> utente = utenteRepository.findByEmail(email);

        // Se l'utente esiste e la password coincide
        if (utente.isPresent() && utente.get().getPassword().equals(password)) {
            return utente.get();
        }

        // Se le credenziali sono errate
        return null;
    }
}