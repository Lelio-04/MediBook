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
    <title>Dr. ${medico.nome} ${medico.cognome} - MediBook</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { background-color: #f4f7f6; margin: 0; font-family: 'Segoe UI', sans-serif; display: flex; flex-direction: column; min-height: 100vh; }

        /* --- NAVBAR (STILE UNIFORMATO) --- */
        .header {
            background: linear-gradient(135deg, #007bff, #0056b3);
            padding: 15px 20px;
            color: white;
            display: flex; justify-content: space-between; align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            position: sticky; top: 0; z-index: 1000;
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

        /* PROFILO */
        .profile-header {
            background: white; padding: 40px 20px; text-align: center;
            border-bottom: 1px solid #ddd; margin-bottom: 30px;
        }
        .profile-avatar {
            width: 120px; height: 120px; background: #eef5ff; color: #007bff;
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            font-size: 40px; font-weight: bold; margin: 0 auto 15px auto;
            border: 4px solid white; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        .profile-name { margin: 0; font-size: 2em; font-weight: 700; color: #333; }
        .profile-specialty {
            color: #666; font-size: 1.1em; margin-top: 5px; display: inline-block;
            background: #f8f9fa; padding: 5px 15px; border-radius: 20px; border: 1px solid #eee;
        }

        /* GRID LAYOUT */
        .profile-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 30px; max-width: 1000px; margin: 0 auto; padding: 0 20px; width: 100%; box-sizing: border-box; }

        .info-card {
            background: white; border-radius: 12px; padding: 25px; margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.03); border: 1px solid #eee;
        }

        .info-item { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #f0f0f0; }
        .info-item:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
        .info-icon { width: 40px; height: 40px; background: #f0f7ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #007bff; font-size: 1.2em; }
        .info-label { font-size: 0.85em; color: #777; display: block; margin-bottom: 2px; text-transform: uppercase; font-weight: bold; }
        .info-value { font-size: 1.1em; color: #333; font-weight: 500; }

        /* LISTA ORARI */
        .orari-list { list-style: none; padding: 0; margin: 0; }
        .orari-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px dashed #eee; font-size: 0.95em; }
        .orari-row:last-child { border-bottom: none; }
        .giorno-label { font-weight: 600; color: #333; }
        .ora-label { color: #555; background: #f8f9fa; padding: 2px 8px; border-radius: 4px; font-family: monospace; }

        /* CTA BOX */
        .cta-box {
            position: sticky; top: 100px; background: white; padding: 25px;
            border-radius: 12px; border: 1px solid #007bff;
            box-shadow: 0 5px 20px rgba(0, 123, 255, 0.15); text-align: center;
        }
        .btn-primary {
            background-color: #007bff; color: white; border: none;
            padding: 12px 20px; border-radius: 6px; font-size: 1rem; cursor: pointer;
            text-decoration: none; display: inline-block; transition: background 0.2s;
        }
        .btn-primary:hover { background-color: #0056b3; }

        @media (max-width: 768px) { .profile-grid { grid-template-columns: 1fr; } .cta-box { position: static; margin-top: 20px; } }
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

<div class="profile-header">
    <div class="profile-avatar">
        ${medico.nome.charAt(0)}${medico.cognome.charAt(0)}
    </div>
    <h1 class="profile-name">Dr. ${medico.nome} ${medico.cognome}</h1>
    <span class="profile-specialty">${medico.specializzazione}</span>
</div>

<div class="profile-grid">
    <div>
        <div class="info-card">
            <h3 style="margin-top:0; margin-bottom:20px; color: #007bff;">
                <i class="fa-solid fa-circle-info"></i> Dettagli Professionali
            </h3>

            <div class="info-item">
                <div class="info-icon"><i class="fa-solid fa-user-doctor"></i></div>
                <div>
                    <span class="info-label">Medico</span>
                    <span class="info-value">Dr. ${medico.nome} ${medico.cognome}</span>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon"><i class="fa-solid fa-stethoscope"></i></div>
                <div>
                    <span class="info-label">Specializzazione</span>
                    <span class="info-value">${medico.specializzazione}</span>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon"><i class="fa-solid fa-id-card"></i></div>
                <div>
                    <span class="info-label">Numero Albo</span>
                    <span class="info-value">${medico.numeroAlbo}</span>
                </div>
            </div>
        </div>

        <div class="info-card">
            <h3 style="margin-top:0; margin-bottom:15px; color: #007bff;">
                <i class="fa-regular fa-clock"></i> Orari di Ricevimento
            </h3>
            <c:choose>
                <c:when test="${not empty medico.turni}">
                    <ul class="orari-list">
                        <%
                            it.unisa.medibook.model.Medico mCorrente = (it.unisa.medibook.model.Medico) request.getAttribute("medico");
                            String rawTurni = mCorrente.getTurni();
                            if(rawTurni != null && !rawTurni.isEmpty()) {
                                String[] slots = rawTurni.split(",");
                                String[] giorniSettimana = {"", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
                                for(String slot : slots) {
                                    try {
                                        String[] parti = slot.split(":");
                                        int giornoIndex = Integer.parseInt(parti[0]);
                                        String orario = slot.substring(slot.indexOf(':') + 1);
                                        orario = orario.replace(":00-", "-").replace(":00", "");
                                        if (orario.endsWith("-")) orario += "00";
                                        String nomeGiorno = (giornoIndex >= 1 && giornoIndex <= 7) ? giorniSettimana[giornoIndex] : "Giorno " + giornoIndex;
                        %>
                        <li class="orari-row">
                            <span class="giorno-label"><%= nomeGiorno %></span>
                            <span class="ora-label"><%= orario %></span>
                        </li>
                        <%
                                    } catch(Exception e) {}
                                }
                            }
                        %>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p style="color: #777; font-style: italic;">Nessun orario specificato. Verifica la disponibilità prenotando.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div>
        <div class="cta-box">
            <h3 style="margin-top:0;">Vuoi prenotare una visita?</h3>
            <p style="color:#666; margin-bottom: 20px;">Verifica le disponibilità in tempo reale.</p>
            <%
                it.unisa.medibook.model.Medico m = (it.unisa.medibook.model.Medico) request.getAttribute("medico");
                String params = "idMedico=" + m.getId() + "&nomeMedico=" + URLEncoder.encode("Dr. " + m.getNome() + " " + m.getCognome() + " (" + m.getSpecializzazione() + ")", StandardCharsets.UTF_8);
                String destUrl = "/paziente?" + params;
                String finalLink = (u != null) ? request.getContextPath() + destUrl : request.getContextPath() + "/accedi?redirect=" + URLEncoder.encode(destUrl, StandardCharsets.UTF_8);
            %>
            <a href="<%= finalLink %>" class="btn-primary" style="width: 100%; display:block; box-sizing:border-box;">
                <i class="fa-regular fa-calendar-check"></i> Prenota Visita
            </a>

        </div>
    </div>
</div>
<div class="container" style="max-width: 1000px; margin: 30px auto; padding: 0 20px;">

    <div class="info-card">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
            <h3 style="margin:0; color: #007bff;">⭐ Recensioni Pazienti</h3>
            <span style="font-size: 1.2em; font-weight: bold; background: #ffc107; padding: 5px 15px; border-radius: 20px;">
                Media: ${mediaVoti} / 5
            </span>
        </div>

        <c:if test="${empty recensioni}">
            <p style="color:#777; font-style:italic;">Nessuna recensione ancora presente.</p>
        </c:if>

        <c:forEach var="rec" items="${recensioni}">
            <div style="border-bottom: 1px solid #eee; padding-bottom: 15px; margin-bottom: 15px;">
                <div style="display:flex; justify-content:space-between;">
                    <strong>${rec.paziente.nome} ${rec.paziente.cognome.charAt(0)}.</strong>
                    <span style="color:#ffc107;">
                        <c:forEach begin="1" end="${rec.voto}">⭐</c:forEach>
                    </span>
                </div>
                <p style="margin: 5px 0; color: #555;">${rec.commento}</p>
                <small style="color: #999;">${rec.dataInserimento}</small>
            </div>
        </c:forEach>
    </div>

</div>

<div style="margin-top: auto; background: #f8f9fa; padding: 20px; text-align: center; color: #777; border-top: 1px solid #eee;">
    <p>&copy; 2026 MediBook System. Tutti i diritti riservati.</p>
</div>

</body>
</html>