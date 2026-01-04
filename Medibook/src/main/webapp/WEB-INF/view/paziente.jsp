<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Area Paziente</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>👋 Ciao, ${nomePaziente}</h1>
        <a href="/logout" class="btn btn-danger">Esci</a>
    </div>

    <% if (request.getParameter("success") != null) { %>
    <div class="alert alert-success">
        ✅ Prenotazione effettuata con successo!
    </div>
    <% } %>

    <div style="display: flex; gap: 30px;">
        <div style="flex: 1;">
            <h3>📅 Prenota una nuova visita</h3>
            <form action="/paziente/prenota" method="post">
                <label>Scegli il Medico:</label>
                <select name="idMedico" required>
                    <option value="">-- Seleziona un dottore --</option>
                    <c:forEach items="${listaMedici}" var="m">
                        <option value="${m.id}">Dott. ${m.cognome} - ${m.specializzazione}</option>
                    </c:forEach>
                </select>

                <label>Data:</label>
                <input type="date" name="data" required>

                <label>Ora:</label>
                <input type="time" name="ora" required>

                <button type="submit" class="btn btn-primary">Conferma Prenotazione</button>
            </form>
        </div>

        <div style="flex: 1;">
            <h3>📜 Storico Visite</h3>
            <table>
                <thead>
                <tr><th>Data</th><th>Medico</th><th>Stato</th></tr>
                </thead>
                <tbody>
                <c:forEach items="${storicoVisite}" var="v">
                    <tr>
                        <td>${v.data}</td>
                        <td>Dott. ${v.medico.cognome}</td>
                        <td>
                            <c:choose>
                                <c:when test="${v.stato == 'EFFETTUATA'}"><span class="stato-ok">Completata</span></c:when>
                                <c:otherwise><span class="stato-wait">${v.stato}</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>