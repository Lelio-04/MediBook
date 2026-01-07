<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestione Pazienti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        /* Sfondo scuro che copre tutta la pagina */
        .modal-overlay {
            display: none; /* Nascosto di default */
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.6); /* Nero semitrasparente */
            z-index: 1000;
            justify-content: center;
            align-items: center;
            animation: fadeIn 0.3s;
        }

        /* Il box bianco al centro */
        .modal-content {
            background: white;
            padding: 30px;
            border-radius: 12px;
            width: 90%;
            max-width: 400px;
            text-align: center;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            position: relative;
        }

        .modal-icon { font-size: 50px; margin-bottom: 10px; }
        .modal-title { font-size: 20px; font-weight: bold; margin-bottom: 10px; color: #333; }
        .modal-text { color: #666; margin-bottom: 25px; font-size: 15px; }

        .modal-actions { display: flex; gap: 10px; justify-content: center; }

        /* Bottoni del modale */
        .btn-cancel { background: #e0e0e0; color: #333; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; }
        .btn-confirm { background: #dc3545; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; text-decoration: none; display: inline-block; }

        .btn-confirm:hover { background: #bd2130; }

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    </style>
</head>
<body>

<div class="container" style="margin-top: 30px;">

    <c:if test="${not empty param.msg}">
        <div class="alert alert-success" style="padding: 10px; background: #d4edda; color: #155724; border-radius: 5px; margin-bottom: 20px;">
            ✅ Operazione completata!
        </div>
    </c:if>
    <c:if test="${not empty param.errore}">
        <div class="alert alert-danger" style="padding: 10px; background: #f8d7da; color: #721c24; border-radius: 5px; margin-bottom: 20px;">
            ⚠️ Errore durante l'operazione.
        </div>
    </c:if>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2>👥 Gestione Pazienti</h2>
        <div>
            <span style="margin-right: 15px;">Operatore: ${sessionScope.utente.nome}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Esci</a>
        </div>
    </div>

    <a href="${pageContext.request.contextPath}/segreteria-utenti/nuovo" class="btn btn-primary" style="margin-bottom: 15px;">
        + Nuovo Paziente
    </a>

    <table class="table">
        <thead>
        <tr>
            <th>Nome e Cognome</th>
            <th>Codice Fiscale</th>
            <th>Email</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${listaPazienti}" var="p">
            <tr>
                <td>${p.nome} ${p.cognome}</td>
                <td>${p.codiceFiscale}</td>
                <td>${p.email}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/segreteria-utenti/modifica?id=${p.id}" class="btn btn-secondary btn-sm">✏️ Modifica</a>

                    <button type="button"
                            class="btn btn-danger btn-sm"
                            onclick="apriModal('${p.id}', '${p.nome} ${p.cognome}')">
                        🗑️ Elimina
                    </button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div id="modalElimina" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-icon">⚠️</div>
        <div class="modal-title">Eliminare Paziente?</div>
        <p class="modal-text" id="modalMessage">Stai per eliminare questo utente. Questa azione è irreversibile e cancellerà anche la cronologia visite.</p>

        <div class="modal-actions">
            <button class="btn-cancel" onclick="chiudiModal()">Annulla</button>
            <a href="#" id="btnConfirmDelete" class="btn-confirm">Sì, elimina definitivamente</a>
        </div>
    </div>
</div>

<script>
    function apriModal(idPaziente, nomePaziente) {
        // 1. Aggiorna il testo del messaggio con il nome del paziente
        document.getElementById('modalMessage').innerText =
            "Stai per eliminare " + nomePaziente + ". Verranno cancellate anche tutte le sue prenotazioni. Sei sicuro?";

        // 2. Imposta il link corretto nel bottone "Sì, elimina"
        const linkBase = "${pageContext.request.contextPath}/segreteria-utenti/elimina?id=";
        document.getElementById('btnConfirmDelete').href = linkBase + idPaziente;

        // 3. Mostra il modale (cambia display da none a flex)
        document.getElementById('modalElimina').style.display = 'flex';
    }

    function chiudiModal() {
        document.getElementById('modalElimina').style.display = 'none';
    }

    // Chiude il modale se clicchi sullo sfondo scuro
    window.onclick = function(event) {
        const modal = document.getElementById('modalElimina');
        if (event.target === modal) {
            chiudiModal();
        }
    }
</script>

</body>
</html>