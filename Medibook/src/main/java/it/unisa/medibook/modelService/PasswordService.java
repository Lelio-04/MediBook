package it.unisa.medibook.modelService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Metodo per criptare la password (da usare quando crei/modifichi utente)
    public String hash(String passwordInChiaro) {
        return passwordEncoder.encode(passwordInChiaro);
    }

    // Metodo per controllare la password (da usare nel Login)
    public boolean check(String passwordInserita, String passwordNelDatabase) {
        return passwordEncoder.matches(passwordInserita, passwordNelDatabase);
    }
}