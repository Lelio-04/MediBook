<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Il Mio Profilo - MediBook</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .profile-container { display: flex; gap: 30px; margin-top: 20px; }
        .card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); flex: 1; }
        .card h3 { border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 20px; color: #007bff; }
        .info-group { margin-bottom: 15px; }
        .info-label { font-weight: bold; color: #555; font-size: 0.9em; }
        .info-value { font-size: 1.1em; }
        .form-control { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ccc; border-radius: 4px; }
        .btn-save { background-color: #28a745; color: white; border: none; padding: 10px 20px; font-size: 1rem; border-radius: 5px; cursor: pointer; width: 100%; }
        .btn-save:hover { background-color: #218838; }
        .alert { padding: 10px; border-radius: 5px; margin-bottom: 15px; }
        .alert-success { background: #d4edda; color: #155724; }
        .alert-danger { background: #f8d7da; color: #721c24; }
        .readonly-field { background-color: #e9ecef; color: #495057; }
    </style>
</head>
<body>

<div class="container">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
        <h1>👤 Il Mio Profilo</h1>
        <a href="${pageContext.request.contextPath}/paziente" class="btn btn-secondary">⬅ Torna alla Dashboard</a>
    </div>

    <c:if test="${not empty successo}"><div class="alert alert-success">${successo}</div></c:if>
    <c:if test="${not empty errore}"><div class="alert alert-danger">${errore}</div></c:if>

    <div class="profile-container">

        <div class="card">
            <h3>🔒 Dati Anagrafici</h3>
            <p><em>Questi dati non sono modificabili. Contatta la segreteria per variazioni.</em></p>

            <div class="info-group">
                <div class="info-label">Nome e Cognome</div>
                <div class="info-value">${paziente.nome} ${paziente.cognome}</div>
            </div>

            <div class="info-group">
                <div class="info-label">Codice Fiscale</div>
                <div class="info-value">${paziente.codiceFiscale}</div>
            </div>

            <div class="info-group">
                <div class="info-label">Email (Username)</div>
                <div class="info-value">${paziente.email}</div>
            </div>
        </div>

        <div class="card">
            <h3>✏️ Modifica Contatti e Sicurezza</h3>

            <form action="${pageContext.request.contextPath}/paziente/profilo/salva" method="post">

                <div class="info-group">
                    <label class="info-label">Telefono</label>
                    <input type="text" name="telefono" class="form-control" value="${paziente.telefono}" required>
                </div>

                <div class="info-group">
                    <label class="info-label">Indirizzo di Residenza</label>
                    <input type="text" name="indirizzo" class="form-control" value="${paziente.indirizzo}">
                </div>

                <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">

                <div class="info-group">
                    <label class="info-label">Cambia Password</label>
                    <input type="password" name="nuovaPassword" class="form-control" placeholder="Lascia vuoto per non cambiare">
                    <small style="color:#666">Inserisci una nuova password solo se vuoi cambiarla.</small>
                </div>

                <button type="submit" class="btn-save">💾 Salva Modifiche</button>
            </form>
        </div>

    </div>
</div>

</body>
</html>