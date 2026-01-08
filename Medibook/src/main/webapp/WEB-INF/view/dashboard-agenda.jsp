<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Segreteria - Agenda Appuntamenti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* STILI GENERALI */
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7f6; margin: 0; }
        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
        header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
        h1 { color: #333; margin: 0; }

        .btn { padding: 8px 15px; border-radius: 5px; text-decoration: none; font-weight: bold; border: none; cursor: pointer; font-size: 14px; }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-danger { background-color: #dc3545; color: white; }
        .btn-success { background-color: #28a745; color: white; }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-warning { background-color: #ffc107; color: #333; border: 1px solid #e0a800; }
        .btn-sm { padding: 5px 10px; font-size: 12px; }
        .btn-icon { display: inline-flex; align-items: center; justify-content: center; gap: 5px; }

        .input-date { padding: 6px; border: 1px solid #ccc; border-radius: 4px; width: 130px; }
        .input-select { padding: 6px; border: 1px solid #ccc; border-radius: 4px; font-weight:bold; cursor: pointer; }
        .search-input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; width: 250px; }

        .alert { padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        .panel { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        .table th { text-align: left; border-bottom: 2px solid #eee; padding: 10px; color: #555; font-size: 0.95em; }
        .table td { border-bottom: 1px solid #f0f0f0; padding: 10px; vertical-align: middle; }

        .stato-prenotata { background-color: #e3f2fd; color: #0d47a1; }
        .stato-effettuata { background-color: #d1e7dd; color: #0f5132; }
        .stato-conclusa { background-color: #fff3cd; color: #856404; }
        .stato-annullata { background-color: #f8d7da; color: #721c24; }

        .today-highlight { border: 2px solid #28a745; background-color: #f0fff4; font-weight: bold; }

        /* TASTO INFO PAZIENTE */
        .btn-info-paziente {
            background: none; border: none; color: #007bff;
            cursor: pointer; font-size: 1.1em; margin-left: 8px;
            transition: transform 0.2s; padding: 0;
        }
        .btn-info-paziente:hover { transform: scale(1.2); color: #0056b3; }

        /* MODALE */
        .modal-overlay {
            display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.5); z-index: 9999;
            align-items: center; justify-content: center;
        }
        .modal-overlay.active { display: flex; animation: fadeIn 0.2s ease-out; }
        .modal-box {
            background: white; padding: 25px; border-radius: 10px;
            max-width: 400px; width: 90%; text-align: left;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3); position: relative;
        }

        .patient-detail-row {
            display: flex; align-items: center; gap: 10px;
            margin-bottom: 12px; padding-bottom: 8px;
            border-bottom: 1px solid #f0f0f0;
        }
        .patient-detail-row i { color: #007bff; width: 25px; text-align: center; font-size: 1.1em; }
        .patient-detail-label { font-weight: bold; color: #555; font-size: 0.85em; display: block; }
        .patient-detail-value { color: #333; font-size: 1em; word-break: break-all; }

        .close-modal-btn {
            position: absolute; top: 15px; right: 20px;
            background: none; border: none; font-size: 1.5em; cursor: pointer; color: #aaa;
        }
        .close-modal-btn:hover { color: #333; }

        @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
    </style>
</head>
<body>

<div class="container">
    <header>
        <h1>📅 Gestione Agenda</h1>
        <div>
            <span style="margin-right: 15px; color: #555;">Operatore: <strong>${sessionScope.utente.nome} ${sessionScope.utente.cognome}</strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Esci</a>
        </div>
    </header>

    <c:if test="${not empty successo}"><div class="alert alert-success">✅ ${successo}</div></c:if>
    <c:if test="${not empty errore}"><div class="alert alert-danger">⚠️ ${errore}</div></c:if>

    <div class="panel">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px; flex-wrap: wrap; gap: 15px;">
            <h3 style="margin:0;">
                <c:choose>
                    <c:when test="${filtroAttivo == 'oggi'}">📅 Visite di OGGI (${oggi})</c:when>
                    <c:otherwise>Elenco Completo Prenotazioni</c:otherwise>
                </c:choose>
            </h3>

            <div style="display:flex; gap:10px; align-items:center;">
                <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" method="get" style="display:flex; gap:5px; margin:0;">
                    <input type="text" name="q" class="search-input" placeholder="Cerca cognome paziente..." value="${searchKeyword}">
                    <button type="submit" class="btn btn-primary">🔍</button>
                </form>

                <c:choose>
                    <c:when test="${filtroAttivo == 'oggi'}">
                        <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" class="btn btn-secondary">❌ Mostra Tutte</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard?filtro=oggi" class="btn btn-warning">📅 Vedi Oggi</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty searchKeyword}">
                    <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" class="btn btn-secondary">Reset</a>
                </c:if>
            </div>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th style="width: 20%;">Paziente</th>
                <th style="width: 15%;">Medico</th>
                <th style="width: 20%;">Data e Ora</th>
                <th style="width: 20%;">Stato Attuale</th>
                <th style="width: 20%;">Azioni</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listaPrenotazioni}" var="v">
                <tr>
                    <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/aggiorna" method="post">
                        <input type="hidden" name="id" value="${v.id}">

                        <td>
                            <div style="display:flex; align-items:center;">
                                <strong style="font-size:1.05em;">${v.paziente.cognome} ${v.paziente.nome}</strong>

                                <button type="button" class="btn-info-paziente" title="Vedi dettagli paziente"
                                        onclick="mostraPaziente(this)"
                                        data-nome="${v.paziente.nome} ${v.paziente.cognome}"
                                        data-cf="${v.paziente.codiceFiscale}"
                                        data-email="${v.paziente.email}"
                                        data-tel="${v.paziente.telefono}"
                                        data-ind="${v.paziente.indirizzo}">
                                    <i class="fa-solid fa-circle-info"></i>
                                </button>
                            </div>
                        </td>

                        <td>
                            <div style="font-weight:500; color:#555;">
                                <i class="fa-solid fa-user-doctor" style="color:#007bff; margin-right:5px;"></i>
                                Dott. ${v.medico.cognome}
                            </div>
                        </td>

                        <td>
                            <div style="display:flex; flex-direction:column; gap:5px;">
                                <input type="date" name="nuovaData" value="${v.data}" required
                                       class="input-date ${v.data == oggi ? 'today-highlight' : ''}">
                                <input type="time" name="nuovaOra" value="${v.ora}" required class="input-date">
                            </div>
                        </td>

                        <td>
                            <select name="nuovoStato" class="input-select"
                                    style="width: 100%; background-color: ${
                                               v.stato == 'PRENOTATA' ? '#e3f2fd' :
                                               (v.stato == 'EFFETTUATA' ? '#d1e7dd' :
                                               (v.stato == 'CONCLUSA' ? '#fff3cd' :
                                               (v.stato == 'ANNULLATA' ? '#f8d7da' : '#ffffff')))
                                           }">
                                <option value="PRENOTATA" class="stato-prenotata" ${v.stato == 'PRENOTATA' ? 'selected' : ''}>PRENOTATA</option>
                                <option value="EFFETTUATA" class="stato-effettuata" ${v.stato == 'EFFETTUATA' ? 'selected' : ''}>EFFETTUATA (Visita Fatta)</option>
                                <option value="CONCLUSA" class="stato-conclusa" ${v.stato == 'CONCLUSA' ? 'selected' : ''}>CONCLUSA (+ Referto)</option>
                                <option value="ANNULLATA" class="stato-annullata" ${v.stato == 'ANNULLATA' ? 'selected' : ''}>ANNULLATA</option>
                            </select>
                        </td>

                        <td style="text-align: center;">
                            <button type="submit" class="btn btn-success btn-sm btn-icon" style="width: 100%; margin-bottom: 5px;">
                                💾 Salva
                            </button>
                            <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/cancella?id=${v.id}"
                               class="btn btn-danger btn-sm btn-icon" style="width: 100%;"
                               onclick="return confirm('Sei sicuro di voler ELIMINARE definitivamente questa prenotazione?');">
                                🗑️ Elimina
                            </a>
                        </td>
                    </form>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty listaPrenotazioni}">
            <div style="text-align:center; padding:50px; color:#777;">
                <h3>📂 Nessun appuntamento trovato</h3>
                <c:choose>
                    <c:when test="${filtroAttivo == 'oggi'}">
                        <p>Non ci sono visite programmate per oggi.</p>
                        <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" class="btn btn-primary">Mostra tutte</a>
                    </c:when>
                    <c:otherwise><p>L'agenda è vuota o la ricerca non ha prodotto risultati.</p></c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</div>

<div id="modalPaziente" class="modal-overlay">
    <div class="modal-box">
        <button class="close-modal-btn" onclick="chiudiModalPaziente()">&times;</button>

        <h3 style="margin-top:0; color: #007bff; border-bottom: 1px solid #eee; padding-bottom: 10px;">
            <i class="fa-solid fa-id-card"></i> Scheda Paziente
        </h3>

        <div style="margin-top: 20px;">
            <div class="patient-detail-row">
                <i class="fa-solid fa-user"></i>
                <div>
                    <span class="patient-detail-label">Nome e Cognome</span>
                    <span class="patient-detail-value" id="pazNome"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-barcode"></i>
                <div>
                    <span class="patient-detail-label">Codice Fiscale</span>
                    <span class="patient-detail-value" id="pazCF"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-envelope"></i>
                <div>
                    <span class="patient-detail-label">Email</span>
                    <span class="patient-detail-value" id="pazEmail"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-phone"></i>
                <div>
                    <span class="patient-detail-label">Telefono</span>
                    <span class="patient-detail-value" id="pazTel"></span>
                </div>
            </div>

            <div class="patient-detail-row" style="border-bottom: none;">
                <i class="fa-solid fa-map-location-dot"></i>
                <div>
                    <span class="patient-detail-label">Indirizzo</span>
                    <span class="patient-detail-value" id="pazInd"></span>
                </div>
            </div>
        </div>

        <div style="text-align: center; margin-top: 20px;">
            <button class="btn btn-primary" style="width: 100%;" onclick="chiudiModalPaziente()">Chiudi Scheda</button>
        </div>
    </div>
</div>

<script>
    function mostraPaziente(btn) {
        document.getElementById('pazNome').innerText = btn.getAttribute('data-nome') || "N/D";
        document.getElementById('pazCF').innerText = btn.getAttribute('data-cf') || "Non inserito";
        document.getElementById('pazEmail').innerText = btn.getAttribute('data-email') || "Non inserita";
        document.getElementById('pazTel').innerText = btn.getAttribute('data-tel') || "Non inserito";
        document.getElementById('pazInd').innerText = btn.getAttribute('data-ind') || "Non inserito";
        document.getElementById('modalPaziente').classList.add('active');
    }

    function chiudiModalPaziente() {
        document.getElementById('modalPaziente').classList.remove('active');
    }

    window.onclick = function(e) {
        if (e.target.classList.contains('modal-overlay')) {
            e.target.classList.remove('active');
        }
    }
</script>

</body>
</html>