<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Scrivi Referto - Segreteria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container" style="max-width: 600px; margin: 50px auto;">

    <h2>📝 Compilazione Referto (Segreteria)</h2>
    <div class="panel" style="background:white; padding:20px; border-radius:8px; box-shadow:0 2px 5px rgba(0,0,0,0.1);">

        <p>Stai chiudendo la visita per:</p>
        <ul>
            <li><strong>Paziente:</strong> ${prenotazione.paziente.nome} ${prenotazione.paziente.cognome}</li>
            <li><strong>Medico:</strong> Dott. ${prenotazione.medico.cognome}</li>
            <li><strong>Data:</strong> ${prenotazione.data}</li>
        </ul>
        <hr>

        <form action="${pageContext.request.contextPath}/segreteria-prenotazioni/referto/salva" method="post">
            <input type="hidden" name="prenotazioneId" value="${prenotazione.id}">

            <label for="contenuto"><strong>Testo del Referto:</strong></label><br>
            <textarea name="contenuto" id="contenuto" rows="6" style="width:100%; padding:10px; margin-top:5px;" required placeholder="Inserisci l'esito della visita..."></textarea>

            <div style="margin-top: 20px; text-align: right;">
                <a href="${pageContext.request.contextPath}/segreteria-prenotazioni/dashboard" class="btn btn-secondary">Annulla</a>
                <button type="submit" class="btn btn-success">💾 Salva e Concludi</button>
            </div>
        </form>

        <div style="margin-top:10px; font-size:0.9em; color:#666;">
            <em>Nota: Salvando questo modulo, lo stato della visita passerà definitivamente a <strong>CONCLUSA</strong>.</em>
        </div>
    </div>
</div>
</body>
</html>