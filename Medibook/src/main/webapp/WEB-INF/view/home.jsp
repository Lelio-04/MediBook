<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Benvenuto - MediBook</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background-color: var(--bg-body);">

<div class="login-container" style="max-width: 600px; margin: 0;">

    <div class="text-center">
        <h1 style="color: var(--primary); font-size: 3em; margin-bottom: 10px;">🏥 MediBook</h1>
        <p style="color: var(--text-secondary); font-size: 1.2em; margin-bottom: 40px;">
            Il portale sanitario professionale per gestire<br>
            le tue prenotazioni e i tuoi referti.
        </p>

        <div style="display: flex; gap: 20px; justify-content: center;">
            <a href="/accedi" class="btn btn-primary" style="padding: 15px 30px; font-size: 18px;">
                Accedi
            </a>

            <a href="/registrazione" class="btn btn-success" style="padding: 15px 30px; font-size: 18px;">
                Registrati
            </a>
        </div>

        <hr style="margin: 40px 0; border-top: 1px solid var(--gray-300);">

        <p class="text-muted" style="font-size: 0.9em;">
            Sei un <b>Medico</b> o lavori in <b>Segreteria</b>?<br>
            Contatta l'amministrazione per le credenziali.
        </p>
    </div>
</div>

</body>
</html>