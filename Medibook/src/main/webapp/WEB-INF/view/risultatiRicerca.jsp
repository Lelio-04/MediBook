<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="it.unisa.medibook.model.Utente" %>
<%@ page import="it.unisa.medibook.model.Paziente" %>
<%@ page import="it.unisa.medibook.model.Medico" %> <%-- IMPORTANTE: Aggiunto Medico --%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Risultati ricerca - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/css/style.css">
    <style>
        body { background-color: var(--bg-body); }
        .header { position: sticky; top: 0; z-index: 1000; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .search-strip { background: #fff; padding: 15px 0; border-bottom: 1px solid #e0e0e0; margin-bottom: 30px; }
        .doctor-card { background: #fff; border-radius: var(--radius-md); padding: 20px; margin-bottom: 20px; box-shadow: var(--shadow-sm); display: flex; gap: 20px; border: 1px solid var(--gray-200); transition: transform 0.2s, box-shadow 0.2s; }
        .doctor-card:hover { transform: translateY(-3px); box-shadow: var(--shadow-md); border-color: var(--primary); }
        .doctor-avatar { width: 80px; height: 80px; border-radius: 50%; background-color: var(--primary-ultralight); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: bold; flex-shrink: 0; }
        .doctor-info { flex: 1; }
        .doctor-name { font-size: 1.25em; font-weight: bold; color: var(--primary); text-decoration: none; display: block; margin-bottom: 5px; }
        .doctor-specialty { color: var(--text-secondary); font-weight: 500; margin-bottom: 10px; display: inline-block; background: var(--gray-100); padding: 2px 8px; border-radius: 4px; font-size: 0.9em; }
        .doctor-actions { display: flex; flex-direction: column; justify-content: center; min-width: 160px; border-left: 1px solid var(--gray-200); padding-left: 20px; }
        @media (max-width: 600px) { .doctor-card { flex-direction: column; } .doctor-actions { border-left: none; padding-left: 0; border-top: 1px solid #eee; padding-top: 15px; margin-top: 10px; } .hide-mobile { display: none; } }
    </style>
</head>
<body>

<header class="header">
    <a href="/" style="text-decoration: none;">
        <h1><span>🏥</span> MediBook</h1>
    </a>
    <div style="display: flex; gap: 15px; align-items: center;">
        <%
            Utente u = (Utente) session.getAttribute("utente");
            boolean isLogged = (u != null);

            if (isLogged) {
        %>
        <div class="hide-mobile" style="text-align: right; margin-right: 10px;">
            <span style="display: block; color: white; font-weight: bold; font-size: 14px;">
                Ciao, <%= (u instanceof Paziente) ? ((Paziente)u).getNome() : "Utente" %>
            </span>
            <span style="font-size: 12px; color: #e6f2ff;">Area Personale</span>
        </div>
        <a href="/" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid white; font-size: 14px;">🏠 Home</a>
        <a href="/<%= u.getRuolo().toLowerCase() %>" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid white;">Dashboard</a>
        <a href="/logout" class="btn btn-danger" style="padding: 8px 15px; font-size: 14px;">Esci</a>
        <% } else { %>
        <a href="/accedi" class="btn" style="color: white;">Accedi</a>
        <a href="/registrazione" class="btn" style="background: white; color: var(--primary);">Registrati</a>
        <% } %>
    </div>
</header>

<div class="search-strip">
    <div class="container" style="margin: 0 auto; max-width: 800px; padding: 0 20px;">
        <form action="/cerca" method="get" style="display: flex; gap: 10px;">
            <input type="text" name="q" value="${query}" class="search-input" style="border: 2px solid var(--gray-300) !important; padding: 10px 15px; border-radius: 50px;" placeholder="Cerca un altro medico...">
            <button type="submit" class="btn btn-primary" style="border-radius: 50px;">Cerca</button>
        </form>
    </div>
</div>

<div class="container" style="max-width: 900px; margin: 0 auto; padding: 0 20px;">
    <h2 style="margin-bottom: 20px; font-size: 1.5em;">Risultati per: <span style="color: var(--primary);">"${query}"</span></h2>

    <c:if test="${empty medici}">
        <div class="empty-state card">
            <h3>Nessun medico trovato.</h3>
            <p>Prova a controllare l'ortografia o cerca una specializzazione più generica.</p>
            <a href="/" class="btn btn-primary mt-lg">Torna alla Home</a>
        </div>
    </c:if>

    <c:forEach var="medico" items="${medici}">
        <div class="doctor-card">
            <div class="doctor-avatar">${medico.nome.charAt(0)}${medico.cognome.charAt(0)}</div>
            <div class="doctor-info">
                <a href="/medico/${medico.id}" class="doctor-name">Dr. ${medico.nome} ${medico.cognome}</a>
                <div class="doctor-specialty">${medico.specializzazione}</div>
                <div style="margin-top: 10px; font-size: 0.9em; color: var(--text-secondary);">📍 Studio Medico (Vedi dettagli)</div>
            </div>

            <div class="doctor-actions">
                <span class="stato-ok" style="justify-content: center; margin-bottom: 10px;">Disponibile</span>

                <%
                    // MODIFICA QUI: Usiamo Medico invece di Utente
                    Medico medicoCurrent = (Medico) pageContext.getAttribute("medico");

                    // Ora i metodi getNome, getCognome e getSpecializzazione funzioneranno
                    String destUrl = "/paziente?idMedico=" + medicoCurrent.getId() +
                            "&nomeMedico=Dr. " + medicoCurrent.getNome() + " " + medicoCurrent.getCognome() +
                            " (" + medicoCurrent.getSpecializzazione() + ")";

                    String finalLink;

                    if (isLogged) {
                        // Se loggato: vai dritto alla destinazione
                        finalLink = destUrl;
                    } else {
                        // Se NON loggato: vai al login, passando la destinazione come parametro 'redirect'
                        String encodedDest = URLEncoder.encode(destUrl, StandardCharsets.UTF_8);
                        finalLink = "/accedi?redirect=" + encodedDest;
                    }
                %>

                <a href="<%= finalLink %>" class="btn btn-primary" style="width: 100%;">
                    Prenota
                </a>
            </div>
        </div>
    </c:forEach>
</div>
<div style="height: 50px;"></div>
</body>
</html>