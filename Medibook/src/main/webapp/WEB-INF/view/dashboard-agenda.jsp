<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Segreteria - Agenda Appuntamenti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .input-date { padding: 6px; border: 1px solid #ccc; border-radius: 4px; width: 135px; }
        .input-select { padding: 6px; border: 1px solid #ccc; border-radius: 4px; font-weight:bold; cursor: pointer; }

        .alert { padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        /* DEFINIZIONE COLORI STATI */
        .stato-prenotata { background-color: #e3f2fd; color: #0d47a1; }
        .stato-effettuata { background-color: #d1e7dd; color: #0f5132; }
        .stato-conclusa { background-color: #fff3cd; color: #856404; }
        .stato-annullata { background-color: #f8d7da; color: #721c24; }

        .btn-icon { display: inline-flex; align-items: center; justify-content: center; gap: 5px; }

        /* Stile barra di ricerca */
        .search-container { display: flex; gap: 10px; margin-bottom: 20px; align-items: center; }
        .search-input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; width: 300px; }
    </style>
</head>
<body>

<div class="container">
    <header style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;">
        <h1>📅 Gestione Agenda</h1>
        <div>
            <span style="margin-right: 15px;">Operatore: ${sessionScope.utente.nome} ${sessionScope.utente.cognome}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Esci</a>
        </div>
    </header>

    <c:if test="${not empty successo}">
        <div class="alert alert-success">✅ ${successo}</div>
    </c:if>
    <c:if test="${not empty errore}">
        <div class="alert alert-danger">⚠️ ${errore}</div>
    </c:if>

    <div class="panel" style="background:white; padding:20px; border-radius:8px; box-shadow:0 2px 5px rgba(0,0,0,0.1);">

        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 15px;">
            <h3>Elenco Prenotazioni</h3>

            <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" method="get" class="search-container" style="margin-bottom:0;">
                <input type="text" name="q" class="search-input" placeholder="Cerca cognome o nome paziente..." value="${searchKeyword}">
                <button type="submit" class="btn btn-primary">🔍 Cerca</button>
                <c:if test="${not empty searchKeyword}">
                    <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" class="btn btn-secondary">Reset</a>
                </c:if>
            </form>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th style="width: 20%;">Paziente & Medico</th>
                <th style="width: 25%;">Data e Ora</th>
                <th style="width: 25%;">Stato</th>
                <th style="width: 15%;">Azioni</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listaPrenotazioni}" var="v">
                <tr>
                    <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/aggiorna" method="post">
                        <input type="hidden" name="id" value="${v.id}">

                        <td style="vertical-align: middle;">
                            <strong>${v.paziente.cognome} ${v.paziente.nome}</strong><br>
                            <small>Dott. ${v.medico.cognome}</small>
                        </td>

                        <td style="vertical-align: middle;">
                            <div style="display:flex; flex-direction:column; gap:5px;">
                                <input type="date" name="nuovaData" value="${v.data}" required class="input-date" min="${oggi}">
                                <input type="time" name="nuovaOra" value="${v.ora}" required class="input-date">
                            </div>
                        </td>

                        <td style="vertical-align: middle;">
                            <select name="nuovoStato" class="input-select"
                                    style="width: 100%;
                                           background-color: ${
                                               v.stato == 'PRENOTATA' ? '#e3f2fd' :
                                               (v.stato == 'EFFETTUATA' ? '#d1e7dd' :
                                               (v.stato == 'CONCLUSA' ? '#fff3cd' :
                                               (v.stato == 'ANNULLATA' ? '#f8d7da' : '#ffffff')))
                                           }">

                                <option value="PRENOTATA" class="stato-prenotata"
                                    ${v.stato == 'PRENOTATA' ? 'selected' : ''}>PRENOTATA</option>

                                <option value="EFFETTUATA" class="stato-effettuata"
                                    ${v.stato == 'EFFETTUATA' ? 'selected' : ''}>EFFETTUATA (Visita Fatta)</option>

                                <option value="CONCLUSA" class="stato-conclusa"
                                    ${v.stato == 'CONCLUSA' ? 'selected' : ''}>CONCLUSA (+ Scrivi Referto)</option>

                                <option value="ANNULLATA" class="stato-annullata"
                                    ${v.stato == 'ANNULLATA' ? 'selected' : ''}>ANNULLATA</option>
                            </select>
                        </td>

                        <td style="vertical-align: middle; text-align: center;">
                            <button type="submit" class="btn btn-success btn-sm btn-icon" style="width: 100%; margin-bottom: 5px;">
                                💾 Salva
                            </button>

                            <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/cancella?id=${v.id}"
                               class="btn btn-danger btn-sm btn-icon" style="width: 100%;"
                               onclick="return confirm('Sei sicuro di voler ELIMINARE definitivamente questa prenotazione?');">
                                🗑️ Cancella
                            </a>
                        </td>
                    </form>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty listaPrenotazioni}">
            <div style="text-align:center; padding:40px; color:#666;">
                <h3> Nessun risultato trovato</h3>
                <c:if test="${not empty searchKeyword}">
                    <p>Prova a cercare con un altro nome o <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard">mostra tutto</a>.</p>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>