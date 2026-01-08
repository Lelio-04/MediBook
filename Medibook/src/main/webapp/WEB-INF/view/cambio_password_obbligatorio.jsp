<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cambio Password Obbligatorio</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body style="display:flex; justify-content:center; align-items:center; height:100vh; background:#f4f7f6;">

<div class="login-container" style="max-width:450px; padding:40px;">
    <div style="text-align:center;">
        <h2 style="color:#d9534f;">⚠️ Primo Accesso</h2>
        <p>Per motivi di sicurezza, è necessario modificare la password provvisoria prima di continuare.</p>
    </div>

    <% if (request.getAttribute("errore") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("errore") %></div>
    <% } %>

    <form action="/aggiorna-password-iniziale" method="post">

        <label>Nuova Password</label>
        <input type="password" name="nuovaPassword" required placeholder="Nuova password personale">

        <label>Conferma Nuova Password</label>
        <input type="password" name="confermaPassword" required placeholder="Ripeti password">

        <button type="submit" class="btn btn-primary" style="width:100%; margin-top:20px;">
            Salva e Accedi
        </button>
    </form>
</div>

</body>
</html>