<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${empty paziente.id ? 'Nuovo Paziente' : 'Modifica Paziente'}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        /* --- STILI LOCALI MIGLIORATI --- */
        /* Puoi spostare questi stili nel tuo file style.css se preferisci */

        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #333;
        }

        .form-card {
            background: white;
            max-width: 650px;
            margin: 40px auto;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05);
            border: 1px solid #eaeaea;
        }

        .form-header {
            text-align: center;
            margin-bottom: 35px;
            padding-bottom: 20px;
            border-bottom: 2px solid #f0f0f0;
        }

        .form-header h2 {
            margin: 0 0 10px 0;
            color: #2c3e50;
            font-size: 1.8rem;
        }

        .form-header p {
            color: #7f8c8d;
            margin: 0;
            font-size: 0.95rem;
        }

        /* Griglia per i campi affiancati */
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #34495e;
            font-size: 0.9rem;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 1rem;
            transition: all 0.3s ease;
            box-sizing: border-box; /* Importante per il padding */
        }

        input:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15);
            outline: none;
        }

        /* Stile per campi Read-Only (Email in modifica) */
        input[readonly] {
            background-color: #e9ecef;
            color: #6c757d;
            cursor: not-allowed;
            border-color: #dee2e6;
        }

        .helper-text {
            font-size: 0.8rem;
            color: #888;
            margin-top: 5px;
            display: block;
        }

        /* Bottoni */
        .btn-container {
            margin-top: 30px;
            display: flex;
            gap: 15px;
        }

        .btn {
            padding: 12px 25px;
            border-radius: 6px;
            font-weight: 600;
            text-align: center;
            text-decoration: none;
            cursor: pointer;
            transition: background 0.2s, transform 0.1s;
            border: none;
            font-size: 1rem;
        }

        .btn:active { transform: scale(0.98); }

        .btn-primary {
            background-color: #007bff;
            color: white;
            flex: 2;
        }
        .btn-primary:hover { background-color: #0056b3; }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
            flex: 1;
        }
        .btn-secondary:hover { background-color: #545b62; }

        /* Mobile Responsive */
        @media (max-width: 600px) {
            .form-row { grid-template-columns: 1fr; gap: 0; }
            .form-card { padding: 20px; margin: 20px; }
        }
    </style>
</head>
<body>

<div class="form-card">

    <div class="form-header">
        <h2>
            <c:choose>
                <c:when test="${empty paziente.id}">➕ Nuovo Paziente</c:when>
                <c:otherwise>✏️ Modifica Paziente</c:otherwise>
            </c:choose>
        </h2>
        <p>
            <c:choose>
                <c:when test="${empty paziente.id}">
                    La password provvisoria sarà: <strong>Medibook123</strong>
                </c:when>
                <c:otherwise>Aggiorna i dati anagrafici del paziente</c:otherwise>
            </c:choose>
        </p>
    </div>

    <form action="${pageContext.request.contextPath}/segreteria-utenti/salva" method="post">

        <input type="hidden" name="id" value="${paziente.id}">

        <div class="form-row">
            <div>
                <label>Nome</label>
                <input type="text" name="nome" value="${paziente.nome}" required placeholder="Es. Mario">
            </div>
            <div>
                <label>Cognome</label>
                <input type="text" name="cognome" value="${paziente.cognome}" required placeholder="Es. Rossi">
            </div>
        </div>

        <div class="form-group">
            <label>Codice Fiscale</label>
            <input type="text" name="codiceFiscale" value="${paziente.codiceFiscale}" required
                   style="text-transform: uppercase; letter-spacing: 1px;" placeholder="RSSMRA...">
        </div>

        <div class="form-row">
            <div>
                <label>Email</label>

                <input type="email" name="email" value="${paziente.email}" required
                       placeholder="email@esempio.it"
                ${not empty paziente.id ? 'readonly' : ''}>

                <c:if test="${not empty paziente.id}">
                    <span class="helper-text">🔒 L'email non può essere modificata.</span>
                </c:if>
            </div>

            <div>
                <label>Telefono</label>
                <input type="text" name="telefono" value="${paziente.telefono}" required placeholder="333 1234567">
            </div>
        </div>

        <div class="form-group">
            <label>Indirizzo</label>
            <input type="text" name="indirizzo" value="${paziente.indirizzo}" placeholder="Via Roma 10, Salerno">
        </div>

        <div class="btn-container">
            <a href="${pageContext.request.contextPath}/segreteria-utenti/dashboard" class="btn btn-secondary">Annulla</a>
            <button type="submit" class="btn btn-primary">💾 Salva Paziente</button>
        </div>

    </form>
</div>

</body>
</html>