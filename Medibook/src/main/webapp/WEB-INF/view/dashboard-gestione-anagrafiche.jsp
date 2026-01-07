<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Segreteria - Gestione Utenti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .panel { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .form-inline { display: flex; gap: 10px; align-items: flex-end; }
        .form-inline input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .badge-active { background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; }
        .badge-archived { background-color: #f8d7da; color: #721c24; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; }
    </style>
</head>
<body>

<div class="container">
    <header style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;">
        <h1>👥 Area Gestione Pazienti</h1>
        <div>
            <span style="margin-right: 15px;">Ciao, ${sessionScope.utente.nome}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Esci</a>
        </div>
    </header>

    <div class="panel">
        <h3>📞 Nuovo Paziente (Inserimento Rapido)</h3>
        <p><small>Compila questi campi mentre sei al telefono. Il resto dei dati potrà essere aggiunto dopo.</small></p>

        <form action="${pageContext.request.contextPath}/segreteria-utenti/crea-rapido" method="post" class="form-inline">
            <div>
                <label>Nome</label><br>
                <input type="text" name="nome" required placeholder="Mario">
            </div>
            <div>
                <label>Cognome</label><br>
                <input type="text" name="cognome" required placeholder="Rossi">
            </div>
            <div>
                <label>Telefono</label><br>
                <input type="text" name="telefono" required placeholder="333 1234567">
            </div>
            <div>
                <label>Email (Opzionale)</label><br>
                <input type="email" name="email" placeholder="email@esempio.it">
            </div>
            <div>
                <br>
                <button type="submit" class="btn btn-primary">➕ Crea Account</button>
            </div>
        </form>
    </div>

    <div class="panel">
        <h3>Archivio Pazienti</h3>
        <table class="table">
            <thead>
            <tr>
                <th>Paziente</th>
                <th>Contatti</th>
                <th>Codice Fiscale</th>
                <th>Stato</th>
                <th>Azioni</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listaPazienti}" var="p">
                <tr>
                    <td>
                        <strong>${p.cognome} ${p.nome}</strong>
                    </td>
                    <td>
                        📞 ${p.telefono}<br>
                        📧 ${p.email}
                    </td>
                    <td>
                            ${p.codiceFiscale != null ? p.codiceFiscale : '<i style="color:red">Mancante</i>'}
                    </td>
                    <td>
                        <span class="badge-active">Attivo</span>
                    </td>
                    <td>
                        <div style="display:flex; gap:5px;">
                            <button class="btn btn-sm btn-secondary" onclick="alert('Funzione modifica dettaglio ID: ${p.id}')">✏️</button>

                            <form action="${pageContext.request.contextPath}/segreteria-utenti/archivia" method="post" onsubmit="return confirm('Sicuro di voler archiviare questo paziente?');">
                                <input type="hidden" name="id" value="${p.id}">
                                <button type="submit" class="btn btn-sm btn-danger">🗑️</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty listaPazienti}">
            <p style="text-align:center; padding:20px;">Nessun paziente trovato nel sistema.</p>
        </c:if>
    </div>
</div>

</body>
</html>