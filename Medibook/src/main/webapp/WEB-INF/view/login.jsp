<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="login-container">
    <h2>🏥 Accedi a MediBook</h2>

    <% if (request.getAttribute("errore") != null) { %>
    <div class="alert alert-danger">
        <%= request.getAttribute("errore") %>
    </div>
    <% } %>

    <form action="/login" method="post">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="Inserisci la tua email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Inserisci la password" required>

        <button type="submit" class="btn btn-primary">Entra</button>
    </form>
</div>
</body>
</html>