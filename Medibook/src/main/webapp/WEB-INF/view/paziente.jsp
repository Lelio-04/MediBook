<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Area Paziente - MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>👋 Ciao, ${nomePaziente}</h1>
        <a href="/logout" class="btn btn-danger">Esci</a>
    </div>

    <%-- Gestione Messaggi Successo/Errore --%>
    <% if (request.getParameter("success") != null) { %>
    <div class="alert alert-success">
        ✅ Operazione completata con successo!
    </div>
    <% } %>

    <% if (request.getParameter("errore") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getParameter("errore") %>
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

                <button type="submit" class="btn btn-primary" style="margin-top: 15px;">Conferma Prenotazione</button>
            </form>
        </div>

        <div style="flex: 1.5;"> <h3>📜 I tuoi Referti e Visite</h3>
            <table>
                <thead>
                <tr>
                    <th>Data</th>
                    <th>Medico</th>
                    <th>Stato</th>
                    <th>Azioni</th> </tr>
                </thead>
                <tbody>
                <c:forEach items="${visite}" var="v">
                    <tr>
                        <td>${v.data}</td>
                        <td>Dott. ${v.medico.cognome}</td>

                        <td>
                            <c:choose>
                                <c:when test="${v.stato == 'CONCLUSA'}">
                                    <span class="stato-ok">REFERTATA</span>
                                </c:when>
                                <c:when test="${v.stato == 'EFFETTUATA'}">
                                    <span class="stato-wait">IN ATTESA</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stato-wait">${v.stato}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:if test="${v.stato == 'CONCLUSA'}">
                                <a href="/paziente/referto?id=${v.id}" class="btn btn-primary btn-tabella" style="padding: 5px 10px; font-size: 0.8em;">
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

            <c:if test="${empty visite}">
                <p style="text-align: center; margin-top: 20px; color: #666;">Nessuna visita nello storico.</p>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>