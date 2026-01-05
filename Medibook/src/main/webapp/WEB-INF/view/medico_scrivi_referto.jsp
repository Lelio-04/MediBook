<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.unisa.medibook.model.Prenotazione" %>
<!DOCTYPE html>
<html>
<head>
    <title>Compilazione Referto - MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body style="background-color: var(--bg-body);">

<div class="container" style="max-width: 800px; margin-top: 40px;">

    <div class="header" style="border-radius: var(--radius-lg) var(--radius-lg) 0 0;">
        <h1>📄 Nuovo Referto Medico</h1>
        <a href="/medico" class="btn btn-tabella" style="background: rgba(255,255,255,0.2); color: white; text-decoration: none;">
            Annulla e Torna Indietro
        </a>
    </div>

    <div class="card" style="border-top: none; border-radius: 0 0 var(--radius-lg) var(--radius-lg);">

        <% Prenotazione p = (Prenotazione) request.getAttribute("prenotazione"); %>

        <div style="background: var(--gray-50); padding: 15px; border-radius: var(--radius-md); margin-bottom: 20px; border: 1px solid var(--gray-200);">
            <h4 style="margin-bottom: 10px; color: var(--primary);">Dettagli Visita</h4>
            <p><strong>Paziente:</strong> <%= p.getPaziente().getNome() %> <%= p.getPaziente().getCognome() %> (CF: <%= p.getPaziente().getCodiceFiscale() %>)</p>
            <p><strong>Data:</strong> <%= p.getData() %> alle ore <%= p.getOra() %></p>
        </div>

        <% if (request.getAttribute("errore") != null) { %>
        <div class="alert alert-danger">
            <%= request.getAttribute("errore") %>
        </div>
        <% } %>

        <form action="/medico/referto/salva" method="post">
            <input type="hidden" name="prenotazioneId" value="<%= p.getId() %>">

            <label for="contenuto" style="font-size: 16px;">Esito Visita e Prescrizioni:</label>
            <textarea id="contenuto" name="contenuto" rows="15"
                      style="width: 100%; padding: 15px; border: 2px solid var(--gray-300); border-radius: var(--radius-md); font-family: monospace; font-size: 14px; line-height: 1.6; resize: vertical;"
                      placeholder="Scrivi qui il referto clinico..." required></textarea>

            <div style="text-align: right; margin-top: 20px;">
                <button type="submit" class="btn btn-primary" style="padding: 12px 30px; font-size: 18px;">
                    💾 Firma e Salva Referto
                </button>
            </div>
        </form>

    </div>
</div>

</body>
</html>