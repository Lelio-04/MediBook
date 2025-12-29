import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api'; // Importiamo la connessione che abbiamo appena creato

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); // Serve per cambiare pagina

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            // 1. Chiamata al Backend Java (AuthService.java)
            const response = await api.post('/auth/login', { email, password });

            const utente = response.data;
            console.log("Login successo:", utente);

            // 2. Salviamo l'utente nel browser (per ricordarci chi è)
            localStorage.setItem('utente', JSON.stringify(utente));

            // 3. Reindirizzamento in base al Ruolo (come da ODD)
            if (utente.ruolo === 'MEDICO') {
                navigate('/medico'); // Andremo alla dashboard medico
            } else if (utente.ruolo === 'SEGRETERIA') {
                navigate('/segreteria'); // Andremo alla dashboard segreteria
            } else if (utente.ruolo === 'PAZIENTE') {
                navigate('/paziente');
            } else {
                setError('Ruolo non riconosciuto');
            }

        } catch (err) {
            console.error(err);
            setError('Credenziali non valide! Riprova.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2>Benvenuto in MediBook</h2>
                <p>Inserisci le tue credenziali per accedere</p>

                <form onSubmit={handleLogin}>
                    <div style={styles.inputGroup}>
                        <label>Email:</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <label>Password:</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    {error && <p style={{color: 'red'}}>{error}</p>}

                    <button type="submit" style={styles.button}>Accedi</button>
                </form>
            </div>
        </div>
    );
};

// Stile CSS semplice interno al file
const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: '#f0f2f5' },
    card: { padding: '2rem', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)', width: '300px', textAlign: 'center' },
    inputGroup: { marginBottom: '1rem', textAlign: 'left' },
    input: { width: '100%', padding: '8px', marginTop: '5px', borderRadius: '4px', border: '1px solid #ccc' },
    button: { width: '100%', padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '16px' }
};

export default LoginPage;