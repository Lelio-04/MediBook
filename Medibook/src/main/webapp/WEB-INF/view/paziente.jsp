<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Area Paziente - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

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

        /* --- CSS BADGE STATI --- */
        .stato-badge { padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; text-transform: uppercase; display: inline-block; letter-spacing: 0.5px; }

        .stato-prenotata { background-color: #cce5ff; color: #004085; border: 1px solid #b8daff; } /* Azzurro */
        .stato-attesa { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; } /* Giallo */
        .stato-conclusa { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; } /* Verde */
        .stato-annullata { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; } /* Rosso */

        /* --- CSS POPUP --- */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center; }
        .modal-overlay.active { display: flex; animation: fadeIn 0.3s ease-out; }
        .modal-box { background: white; padding: 30px; border-radius: 12px; max-width: 400px; width: 90%; box-shadow: 0 10px 25px rgba(0,0,0,0.2); text-align: center; }
        .modal-btn { background-color: #007bff; color: white; border: none; padding: 12px 25px; border-radius: 6px; cursor: pointer; font-size: 16px; font-weight: 500; width: 100%; transition: opacity 0.2s; }
        .modal-btn:hover { opacity: 0.9; }
        @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

        /* Bottoni */
        .btn-tabella { text-decoration: none; padding: 6px 12px; border-radius: 4px; font-size: 13px; font-weight: bold; transition: background 0.2s; display: inline-block;}
        .btn-leggi { background-color: #17a2b8; color: white; }
        .btn-leggi:hover { background-color: #138496; }

        /* Utility */
        .card { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 25px; border: 1px solid #eee; margin-bottom: 20px; }
        .card h3 { margin-top: 0; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 20px; color: #333; }
        .empty-state { text-align: center; color: #777; padding: 20px; font-style: italic; }
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

    <%-- MESSAGGI DI SUCCESSO / ERRORE --%>
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

                        <%--
                           LOGICA DI PRECOMPILAZIONE:
                           1. Se c'è 'prevNomeMedico' (errore validation), usa quello.
                           2. Altrimenti se c'è 'nomeMedico' (da pagina ricerca), usa quello.
                           3. Altrimenti stringa vuota.
                        --%>
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

        <div style="flex: 1.5; min-width: 300px; display: flex; flex-direction: column;">

            <div class="card">
                <h3>⏰ Prossimi Appuntamenti</h3>
                <c:choose>
                    <c:when test="${empty visiteFuture}">
                        <div class="empty-state">✅ Nessun appuntamento futuro in programma.</div>
                    </c:when>
                    <c:otherwise>
                        <div style="overflow-x: auto;">
                            <table style="width: 100%; border-collapse: collapse;">
                                <thead>
                                <tr style="background-color: #eef5ff;">
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #b8daff;">Quando</th>
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #b8daff;">Medico</th>
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #b8daff;">Stato</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${visiteFuture}" var="v">
                                    <tr>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                                            <strong style="color:#0056b3;">${v.data}</strong><br>
                                            <small>${v.ora}</small>
                                        </td>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                                            <strong>Dr. ${v.medico.cognome}</strong><br>
                                            <span style="font-size: 0.85em; color: #666;">${v.medico.specializzazione}</span>
                                        </td>
                                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                                            <c:if test="${v.stato == 'PRENOTATA'}">
                                                <span class="stato-badge stato-prenotata">📅 PRENOTATA</span>
                                            </c:if>
                                            <c:if test="${v.stato != 'PRENOTATA'}">
                                                <span class="stato-badge stato-prenotata">${v.stato}</span>
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

            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px;">
                    <h3 style="margin: 0; padding: 0; border: none;">📂 Storico e Referti</h3>
                </div>

                <div style="margin-bottom: 15px;">
                    <input type="text" id="filterSearch" placeholder="🔍 Filtra storico per nome medico..."
                           style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; box-sizing: border-box;">
                </div>

                <c:choose>
                    <c:when test="${empty storicoVisite}">
                        <div class="empty-state">Non hai ancora visite passate.</div>
                    </c:when>
                    <c:otherwise>
                        <div style="overflow-x: auto; max-height: 400px;">
                            <table id="historyTable" style="width: 100%; border-collapse: collapse;">
                                <thead>
                                <tr style="background-color: #f8f9fa;">
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #eee;">Data</th>
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #eee;">Medico</th>
                                    <th style="padding: 10px; text-align: left; border-bottom: 1px solid #eee;">Stato</th>
                                    <th style="padding: 10px; text-align: right; border-bottom: 1px solid #eee;">Azioni</th>
                                </tr>
                                </thead>
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
                            <div id="noResultsMessage" style="display:none; text-align:center; padding:20px; color:#777;">
                                Nessuna visita trovata con questi criteri.
                            </div>
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
        <span class="modal-icon" style="font-size: 30px; display:block; margin-bottom:10px;">⚠️</span>
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

    // --- LOGICA AUTOCOMPLETE MEDICO (Simulato se non hai API) ---
    /* NOTA: Sostituisci questo array con una chiamata fetch reale se hai l'endpoint JSON.
       Esempio fetch:
       fetch('${pageContext.request.contextPath}/api/cerca-medici-json?q=' + query)...
    */
    const searchInput = document.getElementById('medicoSearch');
    const hiddenIdInput = document.getElementById('idMedicoHidden');
    const suggestionsDiv = document.getElementById('suggestions');
    const errorMsg = document.getElementById('medicoError');

    // Lista medici "finta" per demo frontend (poiché la search JSON non è implementata nel controller mostrato)
    // Se hai implementato l'API JSON, usa il codice fetch commentato sotto.
    const mediciDemo = [
        <c:forEach items="${listaMedici}" var="m">
        { id: "${m.id}", nome: "${m.nome}", cognome: "${m.cognome}", spec: "${m.specializzazione}" },
        </c:forEach>
    ];

    searchInput.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        suggestionsDiv.innerHTML = '';

        if(query.length === 0) {
            hiddenIdInput.value = "";
            suggestionsDiv.style.display = 'none';
            return;
        }

        // Filtro locale (se usi la lista passata dal controller)
        const matches = mediciDemo.filter(m =>
            (m.nome.toLowerCase() + " " + m.cognome.toLowerCase()).includes(query) ||
            m.spec.toLowerCase().includes(query)
        );

        if (matches.length > 0) {
            matches.forEach(medico => {
                const div = document.createElement('div');
                div.className = 'suggestion-item';
                div.innerHTML = '<strong>Dr. ' + medico.nome + ' ' + medico.cognome + '</strong><small>' + medico.spec + '</small>';
                div.addEventListener('click', function() {
                    searchInput.value = 'Dr. ' + medico.cognome + ' (' + medico.spec + ')';
                    hiddenIdInput.value = medico.id;
                    suggestionsDiv.style.display = 'none';
                    errorMsg.style.display = 'none';
                });
                suggestionsDiv.appendChild(div);
            });
            suggestionsDiv.style.display = 'block';
        } else {
            suggestionsDiv.style.display = 'none';
        }
    });

    // Validazione invio form
    document.getElementById('bookingForm').addEventListener('submit', function(e) {
        if(!hiddenIdInput.value) {
            e.preventDefault();
            errorMsg.style.display = 'block';
            mostraPopup("Devi selezionare un medico valido dalla lista.", "Medico mancante");
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

        if (dataSelezionata < dataOdierna) {
            mostraPopup("Non puoi prenotare in una data passata.", "Data non valida");
            dateInput.value = '';
            return;
        }
        // Se è oggi, controlla l'ora
        if (dataSelezionata === dataOdierna && oraSelezionata) {
            const [ore, minuti] = oraSelezionata.split(':');
            const now = new Date();
            const timeSelected = new Date(now.getFullYear(), now.getMonth(), now.getDate(), ore, minuti);

            if (timeSelected < now) {
                mostraPopup("L'orario selezionato è già passato.", "Orario non valido");
                timeInput.value = '';
            }
        }
    }
    dateInput.addEventListener('change', controllaDataOra);
    timeInput.addEventListener('change', controllaDataOra);

    // --- FILTRAGGIO TABELLA STORICO ---
    const filterSearch = document.getElementById('filterSearch');
    if(filterSearch) {
        filterSearch.addEventListener('keyup', function() {
            const text = this.value.toLowerCase();
            const rows = document.querySelectorAll('.history-row');
            let found = false;

            rows.forEach(row => {
                const content = row.getAttribute('data-search').toLowerCase();
                if(content.includes(text)){
                    row.style.display = '';
                    found = true;
                } else {
                    row.style.display = 'none';
                }
            });
            document.getElementById('noResultsMessage').style.display = found ? 'none' : 'block';
        });
    }
</script>

</body>
</html>