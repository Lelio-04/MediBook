<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Segreteria - Agenda Appuntamenti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .input-date { padding: 5px; border: 1px solid #ccc; border-radius: 4px; }
        .status-badge { padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.85em; }
        .status-prenotata { background-color: #cce5ff; color: #004085; }
        .status-completata { background-color: #d4edda; color: #155724; }
        .status-cancellata { background-color: #f8d7da; color: #721c24; }
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

    <div class="panel" style="background:white; padding:20px; border-radius:8px; box-shadow:0 2px 5px rgba(0,0,0,0.1);">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:15px;">
            <h3>Prossime Visite in Programma</h3>
            <select style="padding:5px;">
                <option>Tutti i Medici</option>
                <option>Dott. Rossi</option>
                <option>Dott. Verdi</option>
            </select>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>Data & Ora Attuale</th>
                <th>Paziente</th>
                <th>Medico</th>
                <th>Stato</th>
                <th>Nuova Data/Ora</th>
                <th>Azioni</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listaPrenotazioni}" var="v">
                <tr>
                    <td>
                        <strong>${v.data}</strong><br>
                        <small>ore ${v.ora}</small>
                    </td>
                    <td>
                            ${v.paziente.cognome} ${v.paziente.nome}<br>
                        <small>${v.paziente.telefono}</small>
                    </td>
                    <td>
                        Dott. ${v.medico.cognome}
                    </td>
                    <td>
                        <span class="status-badge status-${v.stato.toLowerCase()}">${v.stato}</span>
                    </td>

                    <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/aggiorna" method="post">
                        <input type="hidden" name="id" value="${v.id}">

                        <td>
                            <div style="display:flex; flex-direction:column; gap:5px;">
                                <input type="date" name="nuovaData" value="${v.data}" required class="input-date">
                                <input type="time" name="nuovaOra" value="${v.ora}" required class="input-date">
                            </div>
                        </td>
                        <td>
                            <button type="submit" class="btn btn-success btn-sm">💾 Salva</button>
                            <br><br>
                            <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/cancella?id=${v.id}"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Cancellare questa visita?');">❌</a>
                        </td>
                    </form>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty listaPrenotazioni}">
            <div style="text-align:center; padding:40px; color:#666;">
                <h3>🎉 Nessuna visita in programma</h3>
                <p>L'agenda è libera.</p>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>