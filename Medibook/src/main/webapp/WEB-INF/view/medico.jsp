<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Area Medico - MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>👨‍⚕️ Dott. ${nomeMedico}</h1>
        <a href="/logout" class="btn btn-danger">Esci</a>
    </div>

    <c:if test="${not empty param.successo}">
        <div class="alert alert-success" style="margin-top: 20px;">
            ✅ Operazione completata con successo!
        </div>
    </c:if>

    <c:if test="${not empty param.errore}">
        <div class="alert alert-danger" style="margin-top: 20px;">
            ⚠️ Errore: ${param.errore}
        </div>
    </c:if>

    <h3>📅 Le tue visite programmate:</h3>

    <table>
        <thead>
        <tr>
            <th>Data</th>
            <th>Ora</th>
            <th>Paziente</th>
            <th>Stato</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${visite}" var="v">
            <tr>
                <td>${v.data}</td>
                <td>${v.ora}</td>
                <td>${v.paziente.nome} ${v.paziente.cognome}</td>

                <td>
                    <c:choose>
                        <c:when test="${v.stato == 'EFFETTUATA'}">
                            <span class="stato-wait" style="background-color: #d1ecf1; color: #0c5460; border-color: #bee5eb;">
                                IN ATTESA DI REFERTO
                            </span>
                        </c:when>
                        <c:when test="${v.stato == 'CONCLUSA'}">
                            <span class="stato-ok">VISITA CONCLUSA</span>
                        </c:when>
                        <c:when test="${v.stato == 'ANNULLATA'}">
                            <span class="stato-wait" style="background-color: #f8d7da; color: #721c24; border-color: #f5c6cb;">ANNULLATA</span>
                        </c:when>
                        <c:otherwise>
                            <span class="stato-wait">${v.stato}</span>
                        </c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <c:choose>

                        <%-- CASO 1: Visita non ancora fatta -> Mostra pulsante "Concludi" --%>
                        <c:when test="${v.stato != 'EFFETTUATA' && v.stato != 'CONCLUSA' && v.stato != 'ANNULLATA'}">
                            <form action="/medico/cambiaStato" method="post" style="display:inline; margin:0;">
                                <input type="hidden" name="id" value="${v.id}">
                                <input type="hidden" name="stato" value="EFFETTUATA">
                                <button type="submit" class="btn btn-success btn-tabella">
                                    ✅ Concludi Visita
                                </button>
                            </form>
                        </c:when>

                        <%-- CASO 2: Visita fatta ma non refertata -> Mostra pulsante "Scrivi Referto" --%>
                        <c:when test="${v.stato == 'EFFETTUATA'}">
                            <a href="/medico/referto/nuovo?id=${v.id}" class="btn btn-primary btn-tabella">
                                📄 Scrivi Referto
                            </a>
                        </c:when>

                        <%-- CASO 3: Visita conclusa -> Nessuna azione o visualizza --%>
                        <c:when test="${v.stato == 'CONCLUSA'}">
                            <span style="color: green; font-weight: bold; font-size: 0.9em;">
                                ✔ Referto Inviato
                            </span>
                        </c:when>

                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty visite}">
        <div class="empty-state">
            <p>Non ci sono visite in programma al momento.</p>
        </div>
    </c:if>
</div>
</body>
</html>