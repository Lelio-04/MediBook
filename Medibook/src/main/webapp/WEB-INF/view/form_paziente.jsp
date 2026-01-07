<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>${empty paziente.id ? 'Nuovo Paziente' : 'Modifica Paziente'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container" style="max-width: 600px; margin-top: 40px;">

    <div style="text-align: center; margin-bottom: 30px;">
        <h2>
            <c:choose>
                <c:when test="${empty paziente.id}">➕ Nuovo Paziente</c:when>
                <c:otherwise>✏️ Modifica Paziente</c:otherwise>
            </c:choose>
        </h2>
        <p style="color: #666;">
            <c:choose>
                <c:when test="${empty paziente.id}">
                    La password verrà impostata automaticamente a <strong>Medibook123</strong>
                </c:when>
                <c:otherwise>Modifica i dati anagrafici del paziente</c:otherwise>
            </c:choose>
        </p>
    </div>

    <form action="${pageContext.request.contextPath}/segreteria-utenti/salva" method="post">

        <input type="hidden" name="id" value="${paziente.id}">

        <div class="two-column-layout" style="display: flex; gap: 15px;">
            <div style="flex: 1;">
                <label>Nome</label>
                <input type="text" name="nome" value="${paziente.nome}" required placeholder="Es. Mario">
            </div>
            <div style="flex: 1;">
                <label>Cognome</label>
                <input type="text" name="cognome" value="${paziente.cognome}" required placeholder="Es. Rossi">
            </div>
        </div>

        <label>Codice Fiscale</label>
        <input type="text" name="codiceFiscale" value="${paziente.codiceFiscale}" required
               style="text-transform: uppercase;" placeholder="RSSMRA...">

        <div class="two-column-layout" style="display: flex; gap: 15px;">
            <div style="flex: 1;">
                <label>Email</label>
                <input type="email" name="email" value="${paziente.email}" required placeholder="email@esempio.it">
            </div>
            <div style="flex: 1;">
                <label>Telefono</label>
                <input type="text" name="telefono" value="${paziente.telefono}" required placeholder="333...">
            </div>
        </div>

        <label>Indirizzo</label>
        <input type="text" name="indirizzo" value="${paziente.indirizzo}" placeholder="Via Roma 10, Salerno">

        <div style="margin-top: 30px; display: flex; gap: 10px;">
            <a href="${pageContext.request.contextPath}/segreteria-utenti/dashboard" class="btn btn-secondary" style="flex: 1; text-align: center; text-decoration: none;">Annulla</a>
            <button type="submit" class="btn btn-primary" style="flex: 2;">💾 Salva Paziente</button>
        </div>

    </form>
</div>

</body>
</html>