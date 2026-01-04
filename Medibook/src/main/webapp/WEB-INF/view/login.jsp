<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="login-container">

    <div class="text-center">
        <h2 style="margin-bottom: 10px;">🏥 Accedi a MediBook</h2>
        <p class="text-muted" style="margin-bottom: 30px;">Inserisci le tue credenziali</p>
    </div>

    <% if (request.getAttribute("errore") != null) { %>
    <div class="alert alert-danger">
        ⚠️ <%= request.getAttribute("errore") %>
    </div>
    <% } %>

    <form action="/login" method="post">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="Inserisci la tua email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Inserisci la password" required>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px; font-size: 18px;">
            Entra
        </button>
    </form>

    <div class="text-center mt-lg">
        <p class="text-muted" style="margin-bottom: 10px;">Non hai un account?
            <a href="/registrazione" style="color: var(--success); font-weight: 600; text-decoration: none;">Registrati</a>
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