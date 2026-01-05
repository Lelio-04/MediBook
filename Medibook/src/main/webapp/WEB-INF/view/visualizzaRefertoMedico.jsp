<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.unisa.medibook.model.Referto" %>
<!DOCTYPE html>
<html>
<head>
    <title>Visualizzazione Referto (Medico) - MediBook</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .foglio-referto {
            background: white;
            padding: 40px;
            border: 1px solid #ddd;
            box-shadow: 0 0 15px rgba(0,0,0,0.05);
            font-family: 'Courier New', Courier, monospace;
            line-height: 1.6;
            margin-top: 20px;
        }

        /* Aggiunta piccola etichetta per indicare che è la vista medico */
        .badge-medico {
            background-color: #e3f2fd;
            color: #0d47a1;
            padding: 5px 10px;
            border-radius: 4px;
            font-family: sans-serif;
            font-size: 12px;
            border: 1px solid #bbdefb;
        }
    </style>
</head>
<body style="background-color: var(--bg-body);">

<div class="container" style="max-width: 800px; margin-top: 40px;">

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <div>
            <h2 style="margin: 0;">📋 Archivio Clinico</h2>
            <span class="badge-medico">Accesso Medico</span>
        </div>
        <a href="${pageContext.request.contextPath}/medico" class="btn btn-primary">← Torna alla Lista Visite</a>
    </div>

    <%
        // Recuperiamo l'oggetto passato dal Controller
        Referto r = (Referto) request.getAttribute("referto");

        // Controllo di sicurezza base per evitare crash se r è null
        if (r != null) {
    %>

    <div class="foglio-referto">
        <div style="border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 20px;">
            <h1 style="text-align: center; font-size: 24px; text-transform: uppercase;">Referto Medico</h1>
            <p style="text-align: center; color: #666; font-size: 14px;">MediBook Digital Health System - Copia Originale</p>
        </div>

        <div style="display: flex; justify-content: space-between; margin-bottom: 30px;">
            <div>
                <strong>PAZIENTE:</strong><br>
                <%= r.getPrenotazione().getPaziente().getNome() %> <%= r.getPrenotazione().getPaziente().getCognome() %><br>
                CF: <%= r.getPrenotazione().getPaziente().getCodiceFiscale() %>
            </div>
            <div style="text-align: right;">
                <strong>MEDICO REFERTANTE:</strong><br>
                Dott. <%= r.getPrenotazione().getMedico().getCognome() %><br>
                <%= r.getPrenotazione().getMedico().getSpecializzazione() %>
            </div>
        </div>

        <p><strong>Data Visita:</strong> <%= r.getPrenotazione().getData() %></p>
        <hr>

        <h4 style="margin-top: 20px; text-decoration: underline;">ESITO CLINICO:</h4>
        <div style="white-space: pre-wrap; margin-top: 15px; font-size: 15px; min-height: 100px;"><%= r.getContenuto() %></div>

        <br><br>
        <div style="text-align: right;">
            <p style="font-family: 'Brush Script MT', cursive; font-size: 20px;">Firma: Dott. <%= r.getPrenotazione().getMedico().getCognome() %></p>
        </div>
    </div>

    <div style="text-align: center; margin-top: 20px; margin-bottom: 40px;">
        <button onclick="window.print()" class="btn btn-tabella" style="background: #666; color: white;">🖨️ Stampa Copia</button>
    </div>

    <% } else { %>
    <div class="alert alert-danger" style="text-align: center; padding: 20px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 5px;">
        <h3>⚠️ Errore</h3>
        <p>Impossibile visualizzare il referto. Dati mancanti.</p>
        <a href="${pageContext.request.contextPath}/medico">Torna indietro</a>
    </div>
    <% } %>

</div>

</body>
</html>