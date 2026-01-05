<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Login MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background-color: var(--bg-body);">

<div class="login-container">

    <div class="text-center">
        <h2 style="margin-bottom: 10px;">🏥 Accedi a MediBook</h2>
        <p class="text-muted" style="margin-bottom: 30px;">Inserisci le tue credenziali</p>
    </div>

    <%-- Gestione Errori --%>
    <% if (request.getAttribute("errore") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errore") %>
    </div>
    <% } %>

    <form action="/login" method="post">

        <%--
            NUOVO CAMPO NASCOSTO:
            Serve per ricordare dove voleva andare l'utente (es. pagina prenotazione con dati precompilati).
            Se il parametro "redirect" è presente nell'URL, lo mettiamo qui dentro.
        --%>
        <input type="hidden" name="redirect" value="<%= request.getParameter("redirect") != null ? request.getParameter("redirect") : "" %>">

        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="Inserisci la tua email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Inserisci la password" required>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px; font-size: 18px;">
            Entra
        </button>
    </form>

    <div style="margin-top: 25px; padding: 15px; background-color: var(--primary-ultralight); border-radius: var(--radius-md); border: 1px solid #cce5ff; font-size: 0.9em; color: var(--primary-dark); text-align: center;">
        <strong>Sei un Medico o lavori in Segreteria?</strong><br>
        <span style="font-size: 0.9em;">Le credenziali per l'accesso professionale devono essere richieste direttamente in sede amministrativa.</span>
    </div>

    <div class="text-center mt-lg">
        <p class="text-muted" style="margin-bottom: 10px;">Non hai un account paziente?
            <a href="/registrazione" style="color: var(--success); font-weight: 600; text-decoration: none;">Registrati ora</a>
        </p>

        <div style="border-top: 1px solid var(--gray-200); margin-top: 20px; padding-top: 15px;">
            <a href="/" style="color: var(--text-secondary); text-decoration: none; font-size: 0.95em; display: flex; align-items: center; justify-content: center; gap: 5px;">
                <span>←</span> Torna alla Home Page
            </a>
        </div>
    </div>

</div>

</body>
</html>