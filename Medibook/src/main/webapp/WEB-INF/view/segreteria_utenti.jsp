<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Gestione Pazienti</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary-color: #007bff;
            --primary-hover: #0056b3;
            --danger-color: #dc3545;
            --danger-hover: #bd2130;
            --bg-color: #f4f7f6;
            --text-color: #333;
            --card-bg: #ffffff;
            --border-color: #e0e0e0;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-color);
            color: var(--text-color);
            margin: 0;
            padding: 20px;
        }

        .dashboard-container {
            max-width: 1100px;
            margin: 0 auto;
            background: var(--card-bg);
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.05);
            padding: 30px;
        }

        /* HEADER */
        .header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            border-bottom: 1px solid var(--border-color);
            padding-bottom: 15px;
        }

        .header-row h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }

        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
            font-size: 0.95rem;
            color: #555;
        }

        /* TOOLBAR (Bottone Nuovo + Ricerca) */
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            gap: 15px;
            flex-wrap: wrap;
        }

        .search-box {
            position: relative;
            max-width: 300px;
            width: 100%;
        }

        .search-box input {
            width: 100%;
            padding: 10px 15px 10px 40px; /* Spazio per l'icona */
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 0.95rem;
        }

        .search-box::before {
            content: "🔍";
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 14px;
            opacity: 0.5;
        }

        /* TABELLA */
        .table-responsive {
            overflow-x: auto;
            border-radius: 8px;
            border: 1px solid var(--border-color);
        }

        .styled-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.95rem;
        }

        .styled-table thead tr {
            background-color: #f8f9fa;
            color: #333;
            text-align: left;
        }

        .styled-table th, .styled-table td {
            padding: 15px 20px;
        }

        .styled-table tbody tr {
            border-bottom: 1px solid var(--border-color);
            transition: background 0.2s;
        }

        .styled-table tbody tr:hover {
            background-color: #f1f3f5;
        }

        .styled-table tbody tr:last-of-type {
            border-bottom: none;
        }

        /* BOTTONI */
        .btn {
            padding: 8px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
            border: none;
            display: inline-block;
            font-size: 0.9rem;
        }

        .btn-primary { background-color: var(--primary-color); color: white; }
        .btn-primary:hover { background-color: var(--primary-hover); }

        .btn-danger { background-color: var(--danger-color); color: white; }
        .btn-danger:hover { background-color: var(--danger-hover); }

        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-secondary:hover { background-color: #5a6268; }

        .btn-sm { padding: 5px 10px; font-size: 0.85rem; margin-right: 5px; }

        /* MODALE */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            justify-content: center;
            align-items: center;
            backdrop-filter: blur(2px);
        }

        .modal-content {
            background: white;
            padding: 30px;
            border-radius: 12px;
            width: 90%;
            max-width: 400px;
            text-align: center;
            box-shadow: 0 20px 40px rgba(0,0,0,0.2);
        }

        .modal-icon { font-size: 40px; margin-bottom: 15px; }
        .modal-title { font-size: 1.25rem; font-weight: bold; margin-bottom: 10px; color: #333; }
        .modal-text { color: #666; margin-bottom: 25px; line-height: 1.5; }
        .modal-actions { display: flex; gap: 10px; justify-content: center; }

        /* ALERT MESSAGES */
        .alert { padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
        .alert-success { background: #d1e7dd; color: #0f5132; border: 1px solid #badbcc; }
        .alert-danger { background: #f8d7da; color: #842029; border: 1px solid #f5c2c7; }

    </style>
</head>
<body>

<div class="dashboard-container">

    <c:if test="${not empty param.msg}">
        <div class="alert alert-success">✅ Operazione completata! (${param.msg})</div>
    </c:if>
    <c:if test="${not empty param.errore}">
        <div class="alert alert-danger">⚠️ Errore: operazione fallita.</div>
    </c:if>

    <div class="header-row">
        <h2>👥 Gestione Pazienti</h2>
        <div class="user-info">
            <span>Operatore: <strong>${sessionScope.utente.nome}</strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">Esci</a>
        </div>
    </div>



    <div class="toolbar">
        <a href="${pageContext.request.contextPath}/segreteria-utenti/nuovo" class="btn btn-primary">
            + Nuovo Paziente
        </a>

        <div class="search-box">
            <input type="text" id="emailFilter" onkeyup="filtraPerEmail()" placeholder="Filtra per email...">
        </div>
    </div>

    <div class="table-responsive">
        <table class="styled-table" id="pazientiTable">
            <thead>
            <tr>
                <th>Nome e Cognome</th>
                <th>Codice Fiscale</th>
                <th>Email</th>
                <th style="text-align: center;">Azioni</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listaPazienti}" var="p">
                <tr>
                    <td>
                        <div style="font-weight: 600;">${p.nome} ${p.cognome}</div>
                    </td>
                    <td style="font-family: monospace; color: #555;">${p.codiceFiscale}</td>

                    <td class="col-email">${p.email}</td>

                    <td style="text-align: center;">
                        <a href="${pageContext.request.contextPath}/segreteria-utenti/modifica?id=${p.id}" class="btn btn-secondary btn-sm">✏️ Modifica</a>

                        <button type="button"
                                class="btn btn-danger btn-sm"
                                onclick="apriModal('${p.id}', '${p.nome} ${p.cognome}')">
                            🗑️ Elimina
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div id="noResults" style="display:none; text-align:center; padding:20px; color:#777; font-style:italic;">
            Nessun paziente trovato con questa email.
        </div>
    </div>
</div>

<div id="modalElimina" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-icon">⚠️</div>
        <div class="modal-title">Eliminare Paziente?</div>
        <p class="modal-text" id="modalMessage">...</p>

        <div class="modal-actions">
            <button class="btn btn-secondary" onclick="chiudiModal()">Annulla</button>
            <a href="#" id="btnConfirmDelete" class="btn btn-danger">Sì, elimina definitivamente</a>
        </div>
    </div>
</div>

<script>
    // --- FUNZIONE FILTRO EMAIL ---
    function filtraPerEmail() {
        // 1. Prendi il valore dell'input e convertilo in minuscolo
        var input = document.getElementById("emailFilter");
        var filter = input.value.toLowerCase();

        // 2. Prendi la tabella e le righe
        var table = document.getElementById("pazientiTable");
        var tr = table.getElementsByTagName("tr");
        var visibleCount = 0;

        // 3. Cicla tutte le righe (saltando l'header che è indice 0 nelle 'rows' ma qui siamo in tbody o tagName)
        // Nota: getElementsByTagName prende TUTTI i tr, incluso thead. Iniziamo da 1 se c'è thead.
        for (var i = 1; i < tr.length; i++) {
            // La colonna Email è la terza (indice 2)
            var tdEmail = tr[i].getElementsByTagName("td")[2];

            if (tdEmail) {
                var txtValue = tdEmail.textContent || tdEmail.innerText;

                // 4. Confronta
                if (txtValue.toLowerCase().indexOf(filter) > -1) {
                    tr[i].style.display = ""; // Mostra
                    visibleCount++;
                } else {
                    tr[i].style.display = "none"; // Nascondi
                }
            }
        }

        // Mostra messaggio "Nessun risultato" se necessario
        var noResDiv = document.getElementById("noResults");
        if(visibleCount === 0 && filter !== "") {
            noResDiv.style.display = "block";
            table.style.display = "none";
        } else {
            noResDiv.style.display = "none";
            table.style.display = "table";
        }
    }

    // --- FUNZIONI MODALE ---
    function apriModal(idPaziente, nomePaziente) {
        document.getElementById('modalMessage').innerText =
            "Stai per eliminare " + nomePaziente + ". Verranno cancellate anche tutte le sue prenotazioni. Sei sicuro?";

        const linkBase = "${pageContext.request.contextPath}/segreteria-utenti/elimina?id=";
        document.getElementById('btnConfirmDelete').href = linkBase + idPaziente;
        document.getElementById('modalElimina').style.display = 'flex';
    }

    function chiudiModal() {
        document.getElementById('modalElimina').style.display = 'none';
    }

    window.onclick = function(event) {
        const modal = document.getElementById('modalElimina');
        if (event.target === modal) {
            chiudiModal();
        }
    }
</script>

</body>
</html>