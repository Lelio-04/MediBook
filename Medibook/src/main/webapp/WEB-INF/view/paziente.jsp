<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Area Paziente - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/style.css">
    <style>
        /* --- CSS Autocomplete --- */
        .autocomplete-container { position: relative; width: 100%; }
        .suggestions-list {
            position: absolute; top: 100%; left: 0; right: 0;
            background: white; border: 1px solid #ddd; border-top: none;
            border-radius: 0 0 8px 8px; max-height: 200px; overflow-y: auto;
            z-index: 1000; box-shadow: 0 4px 6px rgba(0,0,0,0.1); display: none;
        }
        .suggestion-item { padding: 10px 15px; cursor: pointer; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
        .suggestion-item:hover { background-color: #eef5ff; color: #007bff; }
        .suggestion-item strong { display: block; color: #333; }
        .suggestion-item small { color: #777; }

        /* --- CSS PER GLI STATI (Nuovi) --- */
        .stato-badge { padding: 5px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; text-transform: uppercase; display: inline-block; letter-spacing: 0.5px; }

        .stato-ok { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; } /* Verde - Conclusa/Refertata */
        .stato-wait { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; } /* Giallo - In attesa/Prenotata */
        .stato-annullata { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; } /* Rosso - Annullata */

        /* --- CSS PER IL POPUP --- */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center; }
        .modal-overlay.active { display: flex; animation: fadeIn 0.3s ease-out; }
        .modal-box { background: white; padding: 30px; border-radius: 12px; max-width: 400px; width: 90%; box-shadow: 0 10px 25px rgba(0,0,0,0.2); text-align: center; }
        .modal-icon { font-size: 40px; margin-bottom: 15px; display: block; }
        .modal-box h3 { margin: 0 0 10px 0; color: #333; font-size: 20px;}
        .modal-box p { color: #666; line-height: 1.5; margin-bottom: 25px;}
        .modal-btn { background-color: #007bff; color: white; border: none; padding: 12px 25px; border-radius: 6px; cursor: pointer; font-size: 16px; font-weight: 500; width: 100%; transition: opacity 0.2s; }
        .modal-btn:hover { opacity: 0.9; }
        @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

        /* Bottone Tabella */
        .btn-tabella { text-decoration: none; padding: 6px 12px; border-radius: 4px; font-size: 13px; font-weight: bold; transition: background 0.2s; display: inline-block;}
        .btn-leggi { background-color: #17a2b8; color: white; }
        .btn-leggi:hover { background-color: #138496; }
    </style>
</head>
<body style="display: flex; flex-direction: column; min-height: 100vh; background-color: #f4f7f6; font-family: sans-serif; margin:0;">

<%
    // Cattura parametri URL (Safe mode)
    String urlIdMedico = request.getParameter("idMedico");
    String urlNomeMedico = request.getParameter("nomeMedico");
    if (urlIdMedico == null) urlIdMedico = "";
    if (urlNomeMedico == null) urlNomeMedico = "";
%>

<header class="header" style="background: linear-gradient(135deg, #007bff, #0056b3); color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center;">
    <div style="display: flex; align-items: center; gap: 10px;">
        <h1 style="margin: 0; font-size: 24px;"><span>🏥</span> MediBook</h1>
        <span style="color: rgba(255,255,255,0.7); font-size: 14px; margin-left: 10px; padding-left: 10px; border-left: 1px solid rgba(255,255,255,0.3);">Area Paziente</span>
    </div>
    <div style="display: flex; gap: 15px; align-items: center;">
        <a href="/" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid white; font-size: 14px; padding: 8px 15px; text-decoration: none; border-radius: 5px;">🏠 Home Page</a>
        <a href="/logout" class="btn btn-danger" style="background: #dc3545; color: white; padding: 8px 15px; text-decoration: none; border-radius: 5px; font-size: 14px;">Logout</a>
    </div>
</header>

<div class="container" style="max-width: 1200px; margin: 30px auto; padding: 0 20px; width: 100%; box-sizing: border-box;">

    <div style="margin-bottom: 30px; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
        <h2 style="color: #007bff;">👋 Ciao, ${nomePaziente}</h2>
        <p style="color: #666;">Benvenuto nella tua dashboard personale. Da qui puoi gestire le tue visite.</p>
    </div>

    <% if (request.getParameter("success") != null) { %>
    <div class="alert alert-success" style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #c3e6cb;">
        ✅ Operazione completata con successo!
    </div>
    <% } %>

    <% if (request.getParameter("errore") != null) { %>
    <div class="alert alert-danger" style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #f5c6cb;">
        ⚠️ <%= request.getParameter("errore") %>
    </div>
    <% } %>

    <div style="display: flex; gap: 30px; flex-wrap: wrap; align-items: flex-start;">

        <div style="flex: 1; min-width: 300px;">
            <div class="card" style="background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 25px; border: 1px solid #eee;">
                <h3 style="margin-top: 0; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 20px;">📅 Prenota una nuova visita</h3>

                <form action="/paziente/prenota" method="post" style="padding: 0;" id="bookingForm">
                    <label style="display:block; margin-bottom:5px; font-weight:500;">Cerca il Medico:</label>
                    <div class="autocomplete-container">
                        <input type="text" id="medicoSearch"
                               placeholder="Es. Rossi o Cardiologo..."
                               autocomplete="off"
                               value="<%= urlNomeMedico %>"
                               style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;"
                               required>

                        <input type="hidden" name="idMedico" id="idMedicoHidden" value="<%= urlIdMedico %>">
                        <div id="suggestions" class="suggestions-list"></div>
                    </div>
                    <small id="medicoError" style="color: red; display: none; margin-bottom: 10px; margin-top:5px;">⚠️ Devi selezionare un medico dalla lista.</small>

                    <div style="display: flex; gap: 15px; margin-top: 15px;">
                        <div style="flex: 1;">
                            <label style="display:block; margin-bottom:5px; font-weight:500;">Data:</label>
                            <input type="date" name="data" id="dataInput" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;" required>
                        </div>
                        <div style="flex: 1;">
                            <label style="display:block; margin-bottom:5px; font-weight:500;">Ora:</label>
                            <input type="time" name="ora" id="oraInput" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary" style="background-color: #007bff; color: white; width: 100%; padding: 12px; margin-top: 20px; border: none; border-radius: 6px; font-size:16px; cursor:pointer;">Conferma Prenotazione</button>
                </form>
            </div>
        </div>

        <div style="flex: 1.5; min-width: 300px;">
            <div class="card" style="background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 25px; border: 1px solid #eee;">
                <h3 style="margin-top: 0; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 20px;">📜 I tuoi Referti e Visite</h3>

                <c:choose>
                    <c:when test="${empty storicoVisite}">
                        <div class="empty-state" style="text-align:center; color:#777; padding:20px;"><p>Non hai ancora effettuato visite.</p></div>
                    </c:when>
                    <c:otherwise>
                        <div style="overflow-x: auto;">
                            <table style="width: 100%; border-collapse: collapse;">
                                <thead>
                                <tr style="background-color: #f8f9fa;">
                                    <th style="padding: 12px; text-align: left; border-bottom: 1px solid #eee;">Data</th>
                                    <th style="padding: 12px; text-align: left; border-bottom: 1px solid #eee;">Medico</th>
                                    <th style="padding: 12px; text-align: left; border-bottom: 1px solid #eee;">Stato</th>
                                    <th style="padding: 12px; text-align: right; border-bottom: 1px solid #eee;">Azioni</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${storicoVisite}" var="v">
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px solid #eee;">
                                                ${v.data} <br><small style="color: #777;">${v.ora}</small>
                                        </td>
                                        <td style="padding: 12px; border-bottom: 1px solid #eee;">
                                            <strong>Dott. ${v.medico.cognome}</strong><br>
                                            <span style="font-size: 0.85em; color: #666;">${v.medico.specializzazione}</span>
                                        </td>
                                        <td style="padding: 12px; border-bottom: 1px solid #eee;">
                                            <c:choose>
                                                <c:when test="${v.stato == 'CONCLUSA'}">
                                                    <span class="stato-badge stato-ok">REFERTATA</span>
                                                </c:when>
                                                <c:when test="${v.stato == 'EFFETTUATA'}">
                                                    <span class="stato-badge stato-wait">IN ATTESA</span>
                                                </c:when>
                                                <c:when test="${v.stato == 'ANNULLATA'}">
                                                    <span class="stato-badge stato-annullata">ANNULLATA</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="stato-badge stato-wait">PRENOTATA</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right;">
                                            <c:if test="${v.stato == 'CONCLUSA'}">
                                                <a href="/paziente/referto?id=${v.id}" class="btn-tabella btn-leggi">
                                                    👁️ Leggi
                                                </a>
                                            </c:if>
                                            <c:if test="${v.stato != 'CONCLUSA'}">
                                                <span style="color: #ccc;">-</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<div class="text-center p-xl text-muted" style="text-align: center; padding: 20px; color: #aaa; margin-top: auto;">
    <p>&copy; 2026 MediBook System. Tutti i diritti riservati.</p>
</div>

<div id="customPopup" class="modal-overlay">
    <div class="modal-box">
        <span class="modal-icon">⚠️</span>
        <h3 id="popupTitle">Attenzione</h3>
        <p id="popupMessage">Messaggio di avviso.</p>
        <button class="modal-btn" onclick="chiudiPopup()">Ho capito</button>
    </div>
</div>

<script>
    // --- GESTIONE POPUP ---
    const popup = document.getElementById('customPopup');
    const popupMsg = document.getElementById('popupMessage');
    const popupTitle = document.getElementById('popupTitle');

    function mostraPopup(messaggio, titolo = "Attenzione") {
        popupMsg.innerText = messaggio;
        popupTitle.innerText = titolo;
        popup.classList.add('active');
    }
    function chiudiPopup() { popup.classList.remove('active'); }
    popup.addEventListener('click', function(e) { if (e.target === popup) chiudiPopup(); });

    // --- GESTIONE RICERCA MEDICO (Fetch API) ---
    const searchInput = document.getElementById('medicoSearch');
    const hiddenIdInput = document.getElementById('idMedicoHidden');
    const suggestionsDiv = document.getElementById('suggestions');
    const errorMsg = document.getElementById('medicoError');

    searchInput.addEventListener('input', function() {
        const query = this.value;
        if(query.length === 0) hiddenIdInput.value = "";

        if (query.length < 2) { suggestionsDiv.style.display = 'none'; return; }

        fetch('/api/cerca-medici-json?q=' + encodeURIComponent(query))
            .then(response => response.json())
            .then(medici => {
                suggestionsDiv.innerHTML = '';
                if (medici.length > 0) {
                    medici.forEach(medico => {
                        const div = document.createElement('div');
                        div.className = 'suggestion-item';
                        div.innerHTML = '<strong>Dr. ' + medico.nome + ' ' + medico.cognome + '</strong><small>' + medico.specializzazione + '</small>';
                        div.addEventListener('click', function() {
                            searchInput.value = 'Dr. ' + medico.cognome + ' (' + medico.specializzazione + ')';
                            hiddenIdInput.value = medico.id;
                            suggestionsDiv.style.display = 'none';
                            errorMsg.style.display = 'none';
                        });
                        suggestionsDiv.appendChild(div);
                    });
                    suggestionsDiv.style.display = 'block';
                } else { suggestionsDiv.style.display = 'none'; }
            })
            .catch(err => console.error(err));
    });

    document.getElementById('bookingForm').addEventListener('submit', function(e) {
        if(!hiddenIdInput.value) {
            e.preventDefault();
            errorMsg.style.display = 'block';
            mostraPopup("Devi selezionare un medico dalla lista suggerita.", "Dati mancanti");
        }
    });

    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !suggestionsDiv.contains(e.target)) {
            suggestionsDiv.style.display = 'none';
        }
    });

    // --- CONTROLLO DATA E ORA ---
    const dateInput = document.getElementById('dataInput');
    const timeInput = document.getElementById('oraInput');

    const oggi = new Date();
    dateInput.min = oggi.toISOString().split('T')[0];

    function controllaDataOra() {
        const dataSelezionata = dateInput.value;
        const oraSelezionata = timeInput.value;

        if (!dataSelezionata) return;

        const adesso = new Date();
        const dataOdierna = adesso.toISOString().split('T')[0];

        // 1. Controllo Data Passata
        if (dataSelezionata < dataOdierna) {
            mostraPopup("Non puoi prenotare in una data passata.", "Data non valida");
            dateInput.value = '';
            timeInput.value = '';
            return;
        }

        // 2. Controllo Ora Passata (Se è oggi)
        if (dataSelezionata === dataOdierna && oraSelezionata) {
            const [oreScelte, minutiScelti] = oraSelezionata.split(':').map(Number);
            const oreAttuali = adesso.getHours();
            const minutiAttuali = adesso.getMinutes();

            if (oreScelte < oreAttuali || (oreScelte === oreAttuali && minutiScelti < minutiAttuali)) {
                mostraPopup("L'orario selezionato è già passato.", "Orario non valido");
                timeInput.value = '';
            }
        }
    }

    dateInput.addEventListener('change', controllaDataOra);
    timeInput.addEventListener('change', controllaDataOra);
</script>

</body>
</html>