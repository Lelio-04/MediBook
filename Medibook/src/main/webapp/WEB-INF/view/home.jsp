<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Importiamo la classe Utente e Paziente per poterle usare --%>
<%@ page import="it.unisa.medibook.model.Utente" %>
<%@ page import="it.unisa.medibook.model.Paziente" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Benvenuto - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body style="display: flex; flex-direction: column; min-height: 100vh;">

<header class="header">
    <h1><span>🏥</span> MediBook</h1>

    <div style="display: flex; gap: 15px; align-items: center;">

        <%
            // 1. RECUPERO DALLA SESSIONE
            // Nel tuo AuthController hai usato: session.setAttribute("utente", utente);
            // Quindi qui dobbiamo cercare "utente"
            Utente u = (Utente) session.getAttribute("utente");

            if (u != null) {
                // --- CASO A: UTENTE LOGGATO ---
        %>
        <div style="text-align: right; margin-right: 10px;">
                    <span style="display: block; color: white; font-weight: bold; font-size: 14px;">
                        <%-- Cerchiamo di stampare il nome se disponibile, altrimenti l'email --%>
                        Ciao, <%= (u instanceof Paziente) ? ((Paziente)u).getNome() : u.getEmail() %>
                    </span>
            <span style="font-size: 12px; color: #e6f2ff; text-transform: lowercase;">
                        <%= u.getRuolo() %> </span>
        </div>

        <a href="/<%= u.getRuolo().toLowerCase() %>" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid white;">
            Area Personale
        </a>

        <a href="/logout" class="btn btn-danger" style="padding: 8px 15px; font-size: 14px;">
            Logout
        </a>

        <% } else {
            // --- CASO B: UTENTE NON LOGGATO (Ospite) ---
        %>

        <a href="/accedi" class="btn" style="color: white;">Accedi</a>
        <a href="/registrazione" class="btn" style="background: white; color: var(--primary);">Registrati</a>

        <% } %>

    </div>
</header>

<div class="hero-wrapper">

    <h1 class="hero-title">Prenota la tua visita online</h1>
    <p class="hero-subtitle">
        Il portale sanitario professionale per gestire le tue prenotazioni.<br>
        Veloce, sicuro, affidabile.
    </p>

    <div class="search-wrapper">
        <form action="/cerca" method="get" class="search-container" autocomplete="off">
            <div class="input-group">
                <span>🔍</span>
                <input type="text" id="searchInput" name="q" class="search-input"
                       placeholder="Cerca medico o specializzazione..." required>
            </div>
            <button type="submit" class="search-btn">Cerca</button>
        </form>

        <div id="suggestionsList" class="suggestions-box"></div>
    </div>

</div>

<div class="text-center p-xl text-muted" style="background: var(--gray-50);">
    <p>&copy; 2026 MediBook System. Tutti i diritti riservati.</p>
</div>

<script>
    const searchInput = document.getElementById('searchInput');
    const suggestionsBox = document.getElementById('suggestionsList');

    searchInput.addEventListener('input', function() {
        const query = this.value;
        if (query.length < 2) {
            suggestionsBox.style.display = 'none';
            return;
        }

        fetch('/api/suggerimenti?q=' + encodeURIComponent(query))
            .then(response => response.json())
            .then(data => {
                suggestionsBox.innerHTML = '';
                if (data.length > 0) {
                    data.forEach(text => {
                        const div = document.createElement('div');
                        div.className = 'suggestion-item';
                        const icon = text.startsWith("Dr.") ? "👨‍⚕️" : "🩺";
                        div.innerHTML = '<span>' + icon + '</span> ' + text;
                        div.addEventListener('click', function() {
                            searchInput.value = text;
                            suggestionsBox.style.display = 'none';
                        });
                        suggestionsBox.appendChild(div);
                    });
                    suggestionsBox.style.display = 'block';
                } else {
                    suggestionsBox.style.display = 'none';
                }
            })
            .catch(error => console.error('Errore:', error));
    });

    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !suggestionsBox.contains(e.target)) {
            suggestionsBox.style.display = 'none';
        }
    });
</script>
</body>
</html>