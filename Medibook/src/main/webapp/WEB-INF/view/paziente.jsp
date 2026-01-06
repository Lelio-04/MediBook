<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Area Paziente - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="https://npmcdn.com/flatpickr/dist/l10n/it.js"></script>

    <style>
        /* Stili identici a prima */
        .autocomplete-container { position: relative; width: 100%; }
        .suggestions-list {
            position: absolute; top: 100%; left: 0; right: 0;
            background: white; border: 1px solid #ddd; border-top: none;
            border-radius: 0 0 8px 8px; max-height: 200px; overflow-y: auto;
            z-index: 1000; box-shadow: 0 4px 6px rgba(0,0,0,0.1); display: none;
        }
        .suggestion-item { padding: 10px 15px; cursor: pointer; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
        .suggestion-item:hover { background-color: #eef5ff; color: #007bff; }
        .stato-badge { padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; text-transform: uppercase; display: inline-block; letter-spacing: 0.5px; }
        .stato-prenotata { background-color: #cce5ff; color: #004085; border: 1px solid #b8daff; }
        .stato-attesa { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .stato-conclusa { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .stato-annullata { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center; }
        .modal-overlay.active { display: flex; animation: fadeIn 0.3s ease-out; }
        .modal-box { background: white; padding: 30px; border-radius: 12px; max-width: 400px; width: 90%; box-shadow: 0 10px 25px rgba(0,0,0,0.2); text-align: center; }
        .modal-btn { background-color: #007bff; color: white; border: none; padding: 12px 25px; border-radius: 6px; cursor: pointer; font-size: 16px; font-weight: 500; width: 100%; transition: opacity 0.2s; }
        .modal-btn:hover { opacity: 0.9; }
        @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
        .btn-tabella { text-decoration: none; padding: 6px 12px; border-radius: 4px; font-size: 13px; font-weight: bold; transition: background 0.2s; display: inline-block;}
        .btn-leggi { background-color: #17a2b8; color: white; }
        .card { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 25px; border: 1px solid #eee; margin-bottom: 20px; }
        .card h3 { margin-top: 0; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 20px; color: #333; }
        .empty-state { text-align: center; color: #777; padding: 20px; font-style: italic; }
        select.loading { color: #999; font-style: italic; }
        table { width: 100%; border-collapse: collapse; }
        th { text-align: left; color: #555; font-size: 0.9em; border-bottom: 2px solid #eee; padding: 10px; }
        td { padding: 10px; border-bottom: 1px solid #f9f9f9; font-size: 0.95em; }
    </style>
</head>
<body style="display: flex; flex-direction: column; min-height: 100vh; background-color: #f4f7f6; font-family: sans-serif; margin:0;">

<header class="header" style="background: linear-gradient(135deg, #007bff, #0056b3); color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center;">
    <div style="display: flex; align-items: center; gap: 10px;">
        <h1 style="margin: 0; font-size: 24px;"><span>🏥</span> MediBook</h1>
        <span style="color: rgba(255,255,255,0.7); font-size: 14px; margin-left: 10px; padding-left: 10px; border-left: 1px solid rgba(255,255,255,0.3);">Area Paziente</span>
    </div>
    <div style="display: flex; gap: 15px; align-items: center;">
        <a href="${pageContext.request.contextPath}/" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid white; font-size: 14px; padding: 8px 15px; text-decoration: none; border-radius: 5px;">🏠 Home Page</a>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger" style="background: #dc3545; color: white; padding: 8px 15px; text-decoration: none; border-radius: 5px; font-size: 14px;">Logout</a>
    </div>
</header>

<div class="container" style="max-width: 1200px; margin: 30px auto; padding: 0 20px; width: 100%; box-sizing: border-box;">

    <div style="margin-bottom: 30px; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
        <h2 style="color: #007bff;">👋 Ciao, ${nomePaziente}</h2>
        <p style="color: #666;">Benvenuto nella tua dashboard personale.</p>
    </div>

    <c:if test="${not empty param.successo}">
        <div class="alert alert-success" style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #c3e6cb;">
            ✅ Operazione completata con successo!
        </div>
    </c:if>

    <c:if test="${not empty param.errore}">
        <div class="alert alert-danger" style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #f5c6cb;">
            ⚠️ <c:out value="${param.errore}"/>
        </div>
    </c:if>

    <div style="display: flex; gap: 30px; flex-wrap: wrap; align-items: flex-start;">

        <div style="flex: 1; min-width: 300px;">
            <div class="card">
                <h3>📅 Prenota una nuova visita</h3>

                <form action="${pageContext.request.contextPath}/paziente/prenota" method="post" id="bookingForm">

                    <label style="display:block; margin-bottom:5px; font-weight:500;">Cerca il Medico:</label>
                    <div class="autocomplete-container">

                        <%-- 1. RECUPERO VALORI PRECEDENTI O DALLA RICERCA --%>
                        <c:set var="valoreNome" value="${not empty param.prevNomeMedico ? param.prevNomeMedico : param.nomeMedico}" />
                        <c:set var="valoreId" value="${not empty param.prevIdMedico ? param.prevIdMedico : param.idMedico}" />

                        <input type="text" id="medicoSearch"
                               placeholder="Es. Rossi o Cardiologo..."
                               autocomplete="off"
                               value="${valoreNome}"
                               style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;"
                               required>

                        <input type="hidden" name="idMedico" id="idMedicoHidden"
                               value="${valoreId}">

                        <div id="suggestions" class="suggestions-list"></div>
                    </div>
                    <small id="medicoError" style="color: red; display: none; margin-bottom: 10px; margin-top:5px;">⚠️ Devi selezionare un medico dalla lista.</small>

                    <div style="display: flex; gap: 15px; margin-top: 15px;">
                        <div style="flex: 1;">
                            <label style="display:block; margin-bottom:5px; font-weight:500;">Data:</label>
                            <input type="text" name="data" id="dataInput"
                                   placeholder="Scegli Medico..."
                                   style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; background: #fff;"
                                   required disabled>
                        </div>
                        <div style="flex: 1;">
                            <label style="display:block; margin-bottom:5px; font-weight:500;">Ora:</label>
                            <select name="ora" id="oraSelect"
                                    style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;"
                                    required disabled>
                                <option value="">-- Prima la Data --</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary" style="background-color: #007bff; color: white; width: 100%; padding: 12px; margin-top: 20px; border: none; border-radius: 6px; font-size:16px; cursor:pointer;">Conferma Prenotazione</button>
                </form>
            </div>
        </div>

        <div style="flex: 1.5; min-width: 300px;">
            <div class="card">
                <h3>⏰ Prossimi Appuntamenti</h3>
                <c:choose>
                    <c:when test="${empty visiteFuture}">
                        <div class="empty-state">✅ Nessun appuntamento futuro.</div>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <thead><tr><th>Quando</th><th>Medico</th><th>Stato</th></tr></thead>
                            <tbody>
                            <c:forEach items="${visiteFuture}" var="v">
                                <tr>
                                    <td><strong>${v.data}</strong><br><small>${v.ora}</small></td>
                                    <td>Dr. ${v.medico.cognome}</td>
                                    <td><span class="stato-badge stato-prenotata">${v.stato}</span></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="card">
                <h3>📂 Storico Visite</h3>
                <div style="margin-bottom: 15px;">
                    <input type="text" id="filterSearch" placeholder="🔍 Filtra storico..." style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">
                </div>
                <c:choose>
                    <c:when test="${empty storicoVisite}">
                        <div class="empty-state">Storico vuoto.</div>
                    </c:when>
                    <c:otherwise>
                        <div style="overflow-x: auto; max-height: 400px;">
                            <table id="historyTable">
                                <thead><tr><th>Data</th><th>Medico</th><th>Stato</th><th>Azioni</th></tr></thead>
                                <tbody>
                                <c:forEach items="${storicoVisite}" var="v">
                                    <tr class="history-row"
                                        data-search="${v.medico.nome} ${v.medico.cognome} ${v.medico.specializzazione}">

                                        <td style="padding: 10px; border-bottom: 1px solid #eee; color: #555;">
                                                ${v.data}
                                        </td>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                                            Dr. ${v.medico.cognome} <br>
                                            <small style="color:#777;">${v.medico.specializzazione}</small>
                                        </td>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                                            <c:choose>
                                                <c:when test="${v.stato == 'CONCLUSA'}">
                                                    <span class="stato-badge stato-conclusa">✔ Refertata</span>
                                                </c:when>
                                                <c:when test="${v.stato == 'EFFETTUATA'}">
                                                    <span class="stato-badge stato-attesa">⏳ In attesa referto</span>
                                                </c:when>
                                                <c:when test="${v.stato == 'ANNULLATA'}">
                                                    <span class="stato-badge stato-annullata">✖ Annullata</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right;">
                                            <c:if test="${v.stato == 'CONCLUSA'}">
                                                <a href="${pageContext.request.contextPath}/paziente/referto?idVisita=${v.id}" class="btn-tabella btn-leggi">
                                                    📄 Vedi Referto
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

<div id="customPopup" class="modal-overlay">
    <div class="modal-box">
        <h3 id="popupTitle">Attenzione</h3>
        <p id="popupMessage">Messaggio.</p>
        <button class="modal-btn" onclick="chiudiPopup()">OK</button>
    </div>
</div>

<script>
    // --- ELEMENTI ---
    const searchInput = document.getElementById('medicoSearch');
    const hiddenIdInput = document.getElementById('idMedicoHidden');
    const suggestionsDiv = document.getElementById('suggestions');
    const dateInput = document.getElementById('dataInput');
    const oraSelect = document.getElementById('oraSelect');
    const errorMsg = document.getElementById('medicoError');

    // --- POPUP ---
    const popup = document.getElementById('customPopup');
    function mostraPopup(msg, titolo) {
        document.getElementById('popupMessage').innerText = msg;
        document.getElementById('popupTitle').innerText = titolo || "Attenzione";
        popup.classList.add('active');
    }
    function chiudiPopup() { popup.classList.remove('active'); }

    // --- FLATPICKR ---
    let calendarInstance = flatpickr("#dataInput", {
        locale: "it",
        minDate: "today",
        disable: [ () => true ], // Disabilita tutto all'inizio
        onChange: function(selectedDates, dateStr, instance) {
            caricaOrariDisponibili(dateStr);
        }
    });

    // --- LISTA MEDICI (JSP) ---
    const mediciDemo = [
        <c:forEach items="${listaMedici}" var="m">
        { id: "${m.id}", nome: "${m.nome}", cognome: "${m.cognome}", spec: "${m.specializzazione}" },
        </c:forEach>
    ];

    // --- 1. RICERCA MEDICO ---
    searchInput.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        suggestionsDiv.innerHTML = '';

        // Se cambio il testo, resetto l'ID nascosto (l'utente deve selezionare)
        hiddenIdInput.value = "";
        dateInput.disabled = true;
        dateInput.value = "";
        dateInput.placeholder = "Scegli Medico...";
        calendarInstance.clear();
        oraSelect.innerHTML = "<option value=''>-- Prima la Data --</option>";
        oraSelect.disabled = true;

        if(query.length === 0) { suggestionsDiv.style.display = 'none'; return; }

        const matches = mediciDemo.filter(m => (m.nome.toLowerCase() + " " + m.cognome.toLowerCase()).includes(query));

        if (matches.length > 0) {
            matches.forEach(medico => {
                const div = document.createElement('div');
                div.className = 'suggestion-item';
                div.innerHTML = '<strong>Dr. ' + medico.nome + ' ' + medico.cognome + '</strong><small>' + medico.spec + '</small>';

                div.addEventListener('click', function() {
                    selezionaMedico(medico.id, 'Dr. ' + medico.cognome + ' (' + medico.spec + ')');
                });
                suggestionsDiv.appendChild(div);
            });
            suggestionsDiv.style.display = 'block';
        }
    });

    // --- FUNZIONE DI SELEZIONE MEDICO ---
    function selezionaMedico(id, label) {
        searchInput.value = label;
        hiddenIdInput.value = id;
        suggestionsDiv.style.display = 'none';
        errorMsg.style.display = 'none';

        // Attiva il calendario chiamando l'API
        configuraCalendarioPerMedico(id);
    }

    // --- 2. CONFIGURAZIONE CALENDARIO ---
    function configuraCalendarioPerMedico(medicoId) {
        dateInput.placeholder = "Caricamento...";

        fetch('${pageContext.request.contextPath}/paziente/api/giorni-lavoro?medicoId=' + medicoId)
            .then(res => res.json())
            .then(giorniLavorativi => {
                dateInput.disabled = false;
                dateInput.placeholder = "Seleziona data";

                calendarInstance.set('disable', [
                    function(date) {
                        let jsDay = date.getDay();
                        let javaDay = (jsDay === 0) ? 7 : jsDay;
                        return !giorniLavorativi.includes(javaDay);
                    }
                ]);
            })
            .catch(err => console.error("Errore API Giorni:", err));
    }

    // --- 3. CARICAMENTO ORARI ---
    function caricaOrariDisponibili(dataStr) {
        const medicoId = hiddenIdInput.value;
        if(!medicoId || !dataStr) return;

        oraSelect.innerHTML = "<option>Caricamento...</option>";
        oraSelect.classList.add('loading');
        oraSelect.disabled = true;

        fetch('${pageContext.request.contextPath}/paziente/api/orari-disponibili?medicoId=' + medicoId + '&data=' + dataStr)
            .then(res => res.json())
            .then(orari => {
                oraSelect.classList.remove('loading');
                oraSelect.innerHTML = "";

                if(orari.length === 0) {
                    oraSelect.innerHTML = "<option value=''>Nessun posto libero</option>";
                    oraSelect.disabled = true;
                } else {
                    oraSelect.disabled = false;
                    oraSelect.add(new Option("-- Scegli orario --", ""));
                    orari.forEach(ora => oraSelect.add(new Option(ora, ora)));
                }
            })
            .catch(err => {
                console.error("Errore API Orari:", err);
                oraSelect.innerHTML = "<option>Errore</option>";
            });
    }

    // --- AUTO-ATTIVAZIONE SE ARRIVO DA RICERCA ---
    // Questo è il pezzo che mancava: controlla se c'è già un ID e attiva tutto
    window.addEventListener('DOMContentLoaded', (event) => {
        const idPrecaricato = hiddenIdInput.value;
        if(idPrecaricato && idPrecaricato.trim() !== "") {
            configuraCalendarioPerMedico(idPrecaricato);
        }
    });

    // Validazione finale
    document.getElementById('bookingForm').addEventListener('submit', function(e) {
        if(!hiddenIdInput.value) {
            e.preventDefault();
            errorMsg.style.display = 'block';
            mostraPopup("Devi selezionare un medico valido.", "Errore");
        } else if (!oraSelect.value) {
            e.preventDefault();
            mostraPopup("Devi selezionare un orario valido.", "Errore");
        }
    });

    // Chiudi suggerimenti click fuori
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !suggestionsDiv.contains(e.target)) {
            suggestionsDiv.style.display = 'none';
        }
    });
</script>

</body>
</html>