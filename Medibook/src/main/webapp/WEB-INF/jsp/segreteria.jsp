<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Area Segreteria</title>
    <link rel="stylesheet" href="/css/style.css">
    <style>
        .input-tabella { margin-bottom: 0; padding: 8px; }
        .btn-tabella { padding: 8px 12px; font-size: 0.9rem; margin: 0; width: auto;}
    </style>
</head>
<body>
<div class="container" style="max-width: 1200px;">
    <div class="header">
        <h1>🖥️ Area Segreteria</h1>
        <a href="/logout" class="btn btn-danger">Esci</a>
    </div>

    <h3>Gestione Agenda (Dott. Rossi)</h3>
    <p>Modifica direttamente data e ora e premi "Salva" sulla riga corrispondente.</p>

    <table>
        <thead>
        <tr>
            <th>Paziente</th>
            <th>Stato</th>
            <th>Data</th>
            <th>Ora</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${visite}" var="v">
            <tr>
                <form id="form-${v.id}" action="/segreteria/modifica" method="post" style="margin:0;"></form>
                <input type="hidden" name="id" value="${v.id}" form="form-${v.id}">

                <td style="vertical-align: middle;">${v.paziente.cognome} ${v.paziente.nome}</td>
                <td style="vertical-align: middle;"><span class="stato-wait">${v.stato}</span></td>

                <td>
                    <input type="date" name="data" value="${v.data}" form="form-${v.id}" required class="input-tabella">
                </td>
                <td>
                    <input type="time" name="ora" value="${v.ora}" form="form-${v.id}" required class="input-tabella">
                </td>

                <td>
                    <button type="submit" form="form-${v.id}" class="btn btn-success btn-tabella">💾 Salva</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty visite}">
        <p style="margin-top:20px; text-align:center;"><i>Nessuna visita in programma.</i></p>
    </c:if>
</div>
</body>
</html>