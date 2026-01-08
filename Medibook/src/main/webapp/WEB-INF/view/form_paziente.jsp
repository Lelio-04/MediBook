<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${empty paziente.id ? 'Nuovo Paziente' : 'Modifica Paziente'}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        /* --- STILI LOCALI --- */
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
            margin-bottom: 25px; /* Ridotto leggermente per far spazio all'errore */
            padding-bottom: 20px;
            border-bottom: 2px solid #f0f0f0;
        }

        .form-header h2 { margin: 0 0 10px 0; color: #2c3e50; font-size: 1.8rem; }
        .form-header p { color: #7f8c8d; margin: 0; font-size: 0.95rem; }

        /* --- STILE MESSAGGIO ERRORE AGGIUNTO --- */
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
            border: 1px solid #f5c6cb;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: fadeIn 0.3s ease-in-out;
        }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
        /* --------------------------------------- */

        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
        .form-group { margin-bottom: 20px; }

        label { display: block; margin-bottom: 8px; font-weight: 600; color: #34495e; font-size: 0.9rem; }

        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%; padding: 12px 15px; border: 1px solid #ced4da; border-radius: 6px;
            font-size: 1rem; transition: all 0.3s ease; box-sizing: border-box;
        }

        input:focus { border-color: #007bff; box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15); outline: none; }

        /* Validazione visuale */
        input:invalid:not(:placeholder-shown) { border-color: #dc3545; }
        input:valid:not(:placeholder-shown) { border-color: #28a745; }

        input[readonly] { background-color: #e9ecef; color: #6c757d; cursor: not-allowed; border-color: #dee2e6; }

        .helper-text { font-size: 0.8rem; color: #888; margin-top: 5px; display: block; }

        .btn-container { margin-top: 30px; display: flex; gap: 15px; }
        .btn { padding: 12px 25px; border-radius: 6px; font-weight: 600; text-align: center; text-decoration: none; cursor: pointer; transition: background 0.2s, transform 0.1s; border: none; font-size: 1rem; }
        .btn:active { transform: scale(0.98); }
        .btn-primary { background-color: #007bff; color: white; flex: 2; }
        .btn-primary:hover { background-color: #0056b3; }
        .btn-secondary { background-color: #6c757d; color: white; flex: 1; }
        .btn-secondary:hover { background-color: #545b62; }

        @media (max-width: 600px) { .form-row { grid-template-columns: 1fr; gap: 0; } .form-card { padding: 20px; margin: 20px; } }
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

    <c:if test="${not empty errore}">
        <div class="alert-error">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
            <span>${errore}</span>

            [Image of red warning icon]

        </div>
    </c:if>
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
            <label>Codice Fiscale </label>

            <input type="text" name="codiceFiscale" value="${paziente.codiceFiscale}"
                   required
                   minlength="16"
                   maxlength="16"
                   pattern="[A-Za-z]{6}[0-9]{2}[A-Za-z][0-9]{2}[A-Za-z][0-9]{3}[A-Za-z]"
                   title="Il Codice Fiscale deve essere di 16 caratteri (es. RSSMRA80A01H501U)"
                   style="text-transform: uppercase; letter-spacing: 1px; font-family: monospace;"
                   placeholder="RSSMRA...">

            <span class="helper-text">Formato obbligatorio: 16 caratteri (6 lettere, 2 numeri, 1 lettera...)</span>
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