<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Registrazione - MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="login-container" style="max-width: 550px;">

    <div class="text-center">
        <h2 style="margin-bottom: 10px;">📝 Crea Account Paziente</h2>
        <p class="text-muted" style="margin-bottom: 30px;">Inserisci i tuoi dati per accedere ai servizi</p>
    </div>

    <% if (request.getAttribute("errore") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errore") %>
    </div>
    <% } %>

    <form action="/registrazione" method="post">

        <div class="two-column-layout" style="padding: 0; gap: 20px; display: flex; margin-bottom: 0;">
            <div style="flex: 1;">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" placeholder="Es. Mario" required>
            </div>
            <div style="flex: 1;">
                <label for="cognome">Cognome</label>
                <input type="text" id="cognome" name="cognome" placeholder="Es. Rossi" required>
            </div>
        </div>

        <label for="codiceFiscale">Codice Fiscale</label>
        <input type="text" id="codiceFiscale" name="codiceFiscale"
               placeholder="RSSMRA80A01H501U" required
               style="text-transform: uppercase; font-family: monospace; letter-spacing: 1px;">

        <label for="telefono">Telefono</label>
        <input type="text" id="telefono" name="telefono" placeholder="333 1234567" required>

        <div style="margin: 20px 0; border-bottom: 1px solid var(--gray-300);"></div>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="mario.rossi@email.it" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Scegli una password sicura" required>

        <button type="submit" class="btn btn-success" style="width: 100%; margin-top: 10px; font-size: 18px;">
            Conferma Registrazione
        </button>
    </form>

    <div class="text-center mt-lg">
        <p class="text-muted" style="margin-bottom: 10px;">Hai già un account?
            <a href="/accedi" style="color: var(--primary); font-weight: 600; text-decoration: none;">Accedi</a>
        </p>

        <div style="border-top: 1px solid var(--gray-200); margin-top: 15px; padding-top: 15px;">
            <a href="/" style="color: var(--text-muted); text-decoration: none; font-size: 0.9em;">
                ← Torna alla Home Page
            </a>
        </div>
    </div>

</div>

</body>
</html>