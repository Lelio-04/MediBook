<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="it.unisa.medibook.model.Utente" %>
<%@ page import="it.unisa.medibook.model.Paziente" %>
<%@ page import="it.unisa.medibook.model.Medico" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>Risultati ricerca - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        body { background-color: #f4f7f6; font-family: 'Segoe UI', sans-serif; margin: 0; }

        /* NAVBAR (STILE FORNITO DA TE) */
        .header {
            position: sticky; top: 0; z-index: 1000;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            background: linear-gradient(135deg, #007bff, #0056b3);
            padding: 15px 20px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .header h1 { margin: 0; font-size: 1.5rem; color: white; }
        .header h1 span { font-size: 1.8rem; vertical-align: middle; margin-right: 5px; }

        /* Bottoni Navbar */
        .btn-nav {
            padding: 8px 15px; border-radius: 6px; font-weight: 500; font-size: 0.9rem;
            text-decoration: none; transition: 0.2s; display: inline-block;
        }
        .btn-glass { background: rgba(255,255,255,0.2); color: white; border: 1px solid rgba(255,255,255,0.4); }
        .btn-glass:hover { background: rgba(255,255,255,0.3); }
        .btn-danger-nav { background: #dc3545; color: white; border: none; }
        .btn-danger-nav:hover { background: #bb2d3b; }
        .btn-white { background: white; color: #007bff; border: none; }
        .btn-white:hover { background: #f0f0f0; }


        /* BARRA DI RICERCA - STRUTTURA */
        .search-strip-container {
            background-color: white;
            padding: 20px 0;
            border-bottom: 1px solid #e0e0e0;
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            /* FONDAMENTALE: Permette al menu di uscire fuori dai bordi */
            overflow: visible !important;
            position: relative;
            z-index: 900;
            margin-bottom: 30px;
        }

        .search-form {
            display: flex; gap: 10px; max-width: 800px; margin: 0 auto; padding: 0 20px;
        }

        /* Wrapper relativo SOLO per input e dropdown */
        .search-input-wrapper {
            position: relative !important;
            flex-grow: 1;
            z-index: 901;
        }

        .custom-search-input {
            width: 100%; padding: 12px 20px; font-size: 16px;
            border: 2px solid #ddd; border-radius: 50px; outline: none; transition: 0.3s;
            box-sizing: border-box;
        }
        .custom-search-input:focus {
            border-color: #007bff; box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
        }

        /* MENU SUGGERIMENTI */
        .suggestions-dropdown {
            position: absolute;
            top: 100%; /* Subito sotto */
            left: 15px; right: 15px; /* Allineato all'ovale */

            background-color: white !important;
            border: 1px solid #ccc !important;
            border-top: none;
            border-radius: 0 0 15px 15px;

            z-index: 99999 !important; /* Altissimo */
            box-shadow: 0 10px 20px rgba(0,0,0,0.2) !important;

            max-height: 300px; overflow-y: auto;
            display: none; margin-top: 2px;
        }

        .suggestion-item {
            padding: 12px 20px; cursor: pointer; border-bottom: 1px solid #f5f5f5;
            color: #333 !important; background: white !important; font-size: 15px;
            display: flex; align-items: center; gap: 10px;
        }
        .suggestion-item:hover {
            background-color: #f0f7ff !important; color: #007bff !important;
        }

        /* CARD DOTTORE */
        .doctor-card {
            background: #fff; border-radius: 12px; padding: 25px; margin-bottom: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05); display: flex; gap: 25px;
            border: 1px solid #eee; transition: 0.2s; position: relative; z-index: 1;
        }
        .doctor-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); border-color: #007bff; }
        .doctor-avatar { width: 90px; height: 90px; border-radius: 50%; background-color: #eef5ff; color: #007bff; display: flex; align-items: center; justify-content: center; font-size: 32px; font-weight: bold; flex-shrink: 0; border: 2px solid white; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
        .doctor-info { flex: 1; }
        .doctor-name { font-size: 1.4em; font-weight: bold; color: #333; text-decoration: none; display: block; margin-bottom: 5px; }
        .doctor-specialty { color: #007bff; font-weight: 600; background: #eef5ff; padding: 4px 12px; border-radius: 20px; font-size: 0.9em; display: inline-block; margin-bottom: 10px; }
        .doctor-actions { display: flex; flex-direction: column; justify-content: center; min-width: 150px; border-left: 1px solid #eee; padding-left: 25px; }

        @media (max-width: 768px) {
            .doctor-card { flex-direction: column; }
            .doctor-actions { border-left: none; padding-left: 0; border-top: 1px solid #eee; padding-top: 15px; margin-top: 15px; }
            .hide-mobile { display: none; }
        }
    </style>
</head>
<body>

<header class="header">
    <a href="${pageContext.request.contextPath}/" style="text-decoration: none;">
        <h1><span>🏥</span> MediBook</h1>
    </a>
    <div style="display: flex; gap: 10px; align-items: center;">

        <%
            Utente u = (Utente) session.getAttribute("utente");
            if (u != null) {
        %>
        <div class="hide-mobile" style="text-align: right; margin-right: 10px;">
                <span style="display: block; color: white; font-weight: bold; font-size: 14px;">
                    Ciao, <%= (u instanceof Paziente) ? ((Paziente)u).getNome() : u.getEmail() %>
                </span>
            <span style="font-size: 12px; color: #e6f2ff; text-transform: capitalize;">
                    <%= u.getRuolo().toLowerCase() %>
                </span>
        </div>

        <a href="${pageContext.request.contextPath}/" class="btn-nav btn-glass">🏠 Home</a>

        <a href="${pageContext.request.contextPath}/<%= u.getRuolo().toLowerCase() %>" class="btn-nav btn-glass">Area Personale</a>

        <a href="${pageContext.request.contextPath}/logout" class="btn-nav btn-danger-nav">Esci</a>
        <%
        } else {
        %>
        <a href="${pageContext.request.contextPath}/accedi" class="btn-nav btn-glass">Accedi</a>
        <a href="${pageContext.request.contextPath}/registrazione" class="btn-nav btn-white">Registrati</a>
        <% } %>
    </div>
</header>

<div class="search-strip-container">
    <form action="${pageContext.request.contextPath}/cerca" method="get" class="search-form" autocomplete="off">

        <div class="search-input-wrapper">
            <input type="text" id="searchInput" name="q" value="${query}" class="custom-search-input"
                   placeholder="🔍 Cerca medico o specializzazione...">

            <div id="suggestionsList" class="suggestions-dropdown"></div>
        </div>

        <button type="submit" style="background: #007bff; color: white; border: none; padding: 0 35px; border-radius: 50px; font-size: 16px; cursor: pointer; font-weight: bold;">
            Cerca
        </button>
    </form>
</div>

<div class="container" style="max-width: 900px; margin: 0 auto; padding: 0 20px;">

    <div style="margin-bottom: 25px; padding-bottom: 10px; border-bottom: 1px solid #ddd;">
        <h2 style="margin: 0; color: #333;">Risultati per: <span style="color: #007bff;">"${query}"</span></h2>
    </div>

    <c:if test="${empty medici}">
        <div style="text-align: center; padding: 50px; background: white; border-radius: 12px; border: 1px dashed #ccc;">
            <div style="font-size: 40px; margin-bottom: 10px;">🤷‍♂️</div>
            <h3 style="margin-top:0;">Nessun medico trovato.</h3>
            <p style="color: #666;">Controlla l'ortografia o prova una ricerca più generica.</p>
            <a href="${pageContext.request.contextPath}/" style="display: inline-block; margin-top: 15px; background: #007bff; color: white; padding: 10px 25px; border-radius: 50px; text-decoration: none;">Torna alla Home</a>
        </div>
    </c:if>

    <c:forEach var="medico" items="${medici}">
        <div class="doctor-card">
            <div class="doctor-avatar">${medico.nome.charAt(0)}${medico.cognome.charAt(0)}</div>
            <div class="doctor-info">
                <a href="${pageContext.request.contextPath}/medico/${medico.id}" class="doctor-name">Dr. ${medico.nome} ${medico.cognome}</a>
                <div class="doctor-specialty">${medico.specializzazione}</div>
                <div style="font-size: 0.9em; color: #777; margin-top: 8px;">
                    <i class="fa-solid fa-id-card"></i> Albo n. ${medico.numeroAlbo}
                </div>
            </div>

            <div class="doctor-actions">
                <%
                    Medico medicoCurrent = (Medico) pageContext.getAttribute("medico");

                    String destUrl = "/paziente?idMedico=" + medicoCurrent.getId() +
                            "&nomeMedico=Dr. " + medicoCurrent.getNome() + " " + medicoCurrent.getCognome() +
                            " (" + medicoCurrent.getSpecializzazione() + ")";

                    String finalLink = (u != null) ? request.getContextPath() + destUrl
                            : request.getContextPath() + "/accedi?redirect=" + URLEncoder.encode(destUrl, StandardCharsets.UTF_8);
                %>

                <a href="<%= finalLink %>" style="background: #007bff; color: white; padding: 12px; text-align: center; border-radius: 6px; text-decoration: none; font-weight: bold; transition:0.2s;">
                    Prenota
                </a>
                <a href="${pageContext.request.contextPath}/medico/${medico.id}" style="color: #666; text-align: center; text-decoration: none; font-size: 13px; margin-top: 12px; font-weight: 500;">
                    Vedi Profilo
                </a>
            </div>
        </div>
    </c:forEach>
</div>

<div style="height: 50px;"></div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const searchInput = document.getElementById('searchInput');
        const suggestionsBox = document.getElementById('suggestionsList');

        if (!searchInput || !suggestionsBox) return;

        searchInput.addEventListener('input', function() {
            const query = this.value;

            if (query.length < 2) {
                suggestionsBox.style.display = 'none';
                return;
            }

            const url = '${pageContext.request.contextPath}/api/suggerimenti?q=' + encodeURIComponent(query);

            fetch(url)
                .then(res => res.json())
                .then(data => {
                    suggestionsBox.innerHTML = '';
                    if (data && data.length > 0) {
                        data.forEach(text => {
                            const div = document.createElement('div');
                            div.className = 'suggestion-item';

                            const icon = text.startsWith("Dr.") ? "👨‍⚕️" : "🩺";
                            div.innerHTML = '<span>' + icon + '</span> <strong>' + text + '</strong>';

                            div.addEventListener('click', function() {
                                searchInput.value = text;
                                suggestionsBox.style.display = 'none';
                                // searchInput.form.submit();
                            });
                            suggestionsBox.appendChild(div);
                        });
                        suggestionsBox.style.display = 'block';
                    } else {
                        suggestionsBox.style.display = 'none';
                    }
                })
                .catch(err => {
                    console.error(err);
                    suggestionsBox.style.display = 'none';
                });
        });

        // Click fuori chiude
        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !suggestionsBox.contains(e.target)) {
                suggestionsBox.style.display = 'none';
            }
        });
    });
</script>

</body>
</html>