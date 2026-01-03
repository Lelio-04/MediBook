import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const DashboardMedico = () => {
    const [visite, setVisite] = useState([]); // Qui salviamo la lista delle visite
    const [utente, setUtente] = useState(null);
    const navigate = useNavigate();

    // Al caricamento della pagina...
    useEffect(() => {
        // 1. Recuperiamo l'utente loggato dalla memoria
        const userString = localStorage.getItem('utente');
        if (!userString) {
            navigate('/'); // Se non è loggato, torna al login
            return;
        }
        const userObj = JSON.parse(userString);
        setUtente(userObj);

        // 2. Chiediamo al Backend le visite di questo medico
        caricaVisite(userObj.id);
    }, [navigate]);

    const caricaVisite = async (idMedico) => {
        try {
            const response = await api.get(`/prenotazioni/medico/${idMedico}`);
            setVisite(response.data);
        } catch (error) {
            console.error("Errore caricamento visite:", error);
        }
    };

    // Funzione per cambiare lo stato (es. visita completata)
    const cambiaStato = async (idPrenotazione, nuovoStato) => {
        try {
            // Chiamata PATCH al backend
            await api.patch(`/prenotazioni/${idPrenotazione}/stato`, null, {
                params: { nuovoStato }
            });

            // Ricarichiamo la lista per vedere le modifiche
            if (utente) caricaVisite(utente.id);

        } catch (error) {
            alert("Errore nell'aggiornamento dello stato");
        }
    };

    const logout = () => {
        localStorage.removeItem('utente');
        navigate('/');
    };

    return (
        <div style={styles.container}>
            <header style={styles.header}>
                <h2>👨‍⚕️ Area Medico - Dott. {utente?.cognome}</h2>
                <button onClick={logout} style={styles.logoutBtn}>Esci</button>
            </header>

            <div style={styles.content}>
                <h3>📅 Le tue Visite in programma</h3>

                {visite.length === 0 ? (
                    <p>Nessuna visita trovata.</p>
                ) : (
                    <table style={styles.table}>
                        <thead>
                        <tr style={{backgroundColor: '#f8f9fa'}}>
                            <th style={styles.th}>Data</th>
                            <th style={styles.th}>Ora</th>
                            <th style={styles.th}>Paziente</th>
                            <th style={styles.th}>Stato Attuale</th>
                            <th style={styles.th}>Azioni</th>
                        </tr>
                        </thead>
                        <tbody>
                        {visite.map((v) => (
                            <tr key={v.id} style={{borderBottom: '1px solid #ddd'}}>
                                <td style={styles.td}>{v.data}</td>
                                <td style={styles.td}>{v.ora}</td>
                                <td style={styles.td}>{v.paziente ? `${v.paziente.nome} ${v.paziente.cognome}` : 'N/D'}</td>

                                {/* Colora lo stato per renderlo più visibile */}
                                <td style={{...styles.td, fontWeight: 'bold', color: v.stato === 'EFFETTUATA' ? 'green' : 'orange'}}>
                                    {v.stato}
                                </td>

                                <td style={styles.td}>
                                    {v.stato !== 'EFFETTUATA' && (
                                        <>
                                            <button onClick={() => cambiaStato(v.id, 'EFFETTUATA')} style={styles.btnConfirm}>
                                                ✅ Concludi
                                            </button>
                                            <button onClick={() => cambiaStato(v.id, 'ANNULLATA')} style={styles.btnCancel}>
                                                ❌ Annulla
                                            </button>
                                        </>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

// Stili CSS
const styles = {
    container: { padding: '20px', fontFamily: 'Arial, sans-serif' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #eee', paddingBottom: '10px' },
    logoutBtn: { backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '5px', cursor: 'pointer' },
    content: { marginTop: '21px' },
    table: { width: '100%', borderCollapse: 'collapse', marginTop: '10px' },
    th: { textAlign: 'left', padding: '12px', borderBottom: '2px solid #ddd' },
    td: { padding: '12px' },
    btnConfirm: { backgroundColor: '#28a745', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer', marginRight: '5px' },
    btnCancel: { backgroundColor: '#6c757d', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer' }
};

export default DashboardMedico;