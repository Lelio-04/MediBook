import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const DashboardSegreteria = () => {
    const [visite, setVisite] = useState([]);
    const [utente, setUtente] = useState(null);

    // Stati per la gestione della modifica
    const [editingId, setEditingId] = useState(null); // Quale riga sto modificando?
    const [nuovaData, setNuovaData] = useState('');
    const [nuovaOra, setNuovaOra] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const userString = localStorage.getItem('utente');
        if (!userString) {
            navigate('/');
            return;
        }
        setUtente(JSON.parse(userString));

        // Per semplicità, la segreteria vede l'agenda del Medico con ID 1 (Dott. Rossi)
        // In un'app completa, qui metteremmo un menu a tendina per scegliere il medico
        caricaVisite(1);
    }, [navigate]);

    const caricaVisite = async (idMedico) => {
        try {
            const response = await api.get(`/prenotazioni/medico/${idMedico}`);
            setVisite(response.data);
        } catch (error) {
            console.error("Errore caricamento agenda:", error);
        }
    };

    // Attiva la modalità modifica per una specifica riga
    const avviaModifica = (visita) => {
        setEditingId(visita.id);
        setNuovaData(visita.data);
        setNuovaOra(visita.ora);
    };

    // Annulla la modifica
    const annullaModifica = () => {
        setEditingId(null);
        setNuovaData('');
        setNuovaOra('');
    };

    // Salva le modifiche su Spring Boot
    const salvaModifica = async (id) => {
        try {
            // Chiamata PUT al backend
            await api.put(`/prenotazioni/${id}`, {
                data: nuovaData,
                ora: nuovaOra
            });

            alert("Prenotazione aggiornata con successo!");
            setEditingId(null);
            caricaVisite(1); // Ricarica la tabella aggiornata

        } catch (error) {
            alert("Errore durante la modifica: " + error.message);
        }
    };

    const logout = () => {
        localStorage.removeItem('utente');
        navigate('/');
    };

    return (
        <div style={styles.container}>
            <header style={styles.header}>
                <h2>🖥️ Area Segreteria - {utente?.email}</h2>
                <button onClick={logout} style={styles.logoutBtn}>Esci</button>
            </header>

            <div style={styles.content}>
                <h3>Gestione Agenda (Dott. Rossi)</h3>
                <p>Qui puoi modificare data e ora degli appuntamenti.</p>

                <table style={styles.table}>
                    <thead>
                    <tr style={{backgroundColor: '#e3f2fd'}}>
                        <th style={styles.th}>Paziente</th>
                        <th style={styles.th}>Stato</th>
                        <th style={styles.th}>Data</th>
                        <th style={styles.th}>Ora</th>
                        <th style={styles.th}>Azioni</th>
                    </tr>
                    </thead>
                    <tbody>
                    {visite.map((v) => (
                        <tr key={v.id} style={{borderBottom: '1px solid #ddd'}}>
                            <td style={styles.td}>
                                {v.paziente ? `${v.paziente.cognome} ${v.paziente.nome}` : 'N/D'}
                            </td>
                            <td style={styles.td}>{v.stato}</td>

                            {/* Se questa è la riga in modifica, mostra INPUT, altrimenti mostra TESTO */}
                            {editingId === v.id ? (
                                <>
                                    <td style={styles.td}>
                                        <input type="date" value={nuovaData} onChange={e => setNuovaData(e.target.value)} />
                                    </td>
                                    <td style={styles.td}>
                                        <input type="time" value={nuovaOra} onChange={e => setNuovaOra(e.target.value)} />
                                    </td>
                                    <td style={styles.td}>
                                        <button onClick={() => salvaModifica(v.id)} style={styles.btnSave}>💾 Salva</button>
                                        <button onClick={annullaModifica} style={styles.btnCancel}>❌</button>
                                    </td>
                                </>
                            ) : (
                                <>
                                    <td style={styles.td}>{v.data}</td>
                                    <td style={styles.td}>{v.ora}</td>
                                    <td style={styles.td}>
                                        <button onClick={() => avviaModifica(v)} style={styles.btnEdit}>✏️ Modifica</button>
                                    </td>
                                </>
                            )}
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

// Stili
const styles = {
    container: { padding: '20px', fontFamily: 'Arial, sans-serif' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #eee', paddingBottom: '10px' },
    logoutBtn: { backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '5px', cursor: 'pointer' },
    content: { marginTop: '20px' },
    table: { width: '100%', borderCollapse: 'collapse', marginTop: '10px' },
    th: { textAlign: 'left', padding: '12px', borderBottom: '2px solid #ddd' },
    td: { padding: '12px', verticalAlign: 'middle' },
    btnEdit: { backgroundColor: '#ffc107', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer' },
    btnSave: { backgroundColor: '#28a745', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer', marginRight: '5px' },
    btnCancel: { backgroundColor: '#6c757d', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer' }
};

export default DashboardSegreteria;