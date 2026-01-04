<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Area Medico</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>👨‍⚕️ Dott. ${nomeMedico}</h1>
        <a href="/logout" class="btn btn-danger">Esci</a>
    </div>

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
                            <span class="stato-ok">EFFETTUATA</span>
                        </c:when>
                        <c:otherwise>
                            <span class="stato-wait">${v.stato}</span>
                        </c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <c:if test="${v.stato != 'EFFETTUATA'}">
                        <form action="/medico/cambiaStato" method="post" style="display:inline; margin:0;">
                            <input type="hidden" name="id" value="${v.id}">
                            <input type="hidden" name="stato" value="EFFETTUATA">
                            <button type="submit" class="btn btn-success" style="padding: 8px 15px; font-size: 0.9rem;">✅ Concludi</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:if test="${empty visite}">
        <p style="margin-top: 20px; text-align: center;"><i>Nessuna visita in programma.</i></p>
    </c:if>
</div>
</body>
</html>