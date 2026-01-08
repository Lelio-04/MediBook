<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Area Medico - MediBook</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* Badge di stato */
        .badge { padding: 6px 12px; border-radius: 20px; font-size: 0.85em; font-weight: bold; display: inline-flex; align-items: center; gap: 5px; }
        .bg-warning { background-color: #ffc107; color: #333; }
        .bg-success { background-color: #28a745; color: white; }
        .bg-info { background-color: #17a2b8; color: white; }
        .bg-danger { background-color: #dc3545; color: white; }

        /* Pulsanti Azione */
        .action-btn {
            text-decoration: none; padding: 6px 12px; border-radius: 5px;
            font-size: 0.9em; transition: 0.3s; border: none; cursor: pointer;
            display: inline-flex; align-items: center; gap: 5px; color: white;
        }
        .btn-referto { background-color: #007bff; }
        .btn-referto:hover { background-color: #0056b3; }
        .btn-esegui { background-color: #28a745; }
        .btn-esegui:hover { background-color: #1e7e34; }
        .btn-annulla { background-color: #dc3545; }
        .btn-annulla:hover { background-color: #bd2130; }
        .btn-vedi { background-color: #6c757d; }
        .btn-vedi:hover { background-color: #545b62; }

        /* Nuovo pulsante info paziente */
        .btn-info-paziente {
            background: none; border: none; color: #007bff;
            cursor: pointer; font-size: 1.1em; margin-left: 8px;
            transition: transform 0.2s;
        }
        .btn-info-paziente:hover { transform: scale(1.2); color: #0056b3; }

        .btn-group { display: flex; gap: 5px; }

        /* Sezioni */
        .section-title { margin-top: 40px; margin-bottom: 15px; border-bottom: 2px solid #eee; padding-bottom: 10px; color: #333; display:flex; align-items:center; gap:10px; }
        .section-title.primary { color: #007bff; border-color: #007bff; }
        .section-title.secondary { color: #6c757d; border-color: #6c757d; }

        /* --- STILE DASHBOARD HERO --- */
        .dashboard-hero {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: white;
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 40px;
            box-shadow: 0 10px 20px rgba(0, 123, 255, 0.2);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .dashboard-hero h2 { margin: 0 0 10px 0; font-size: 24px; }
        .dashboard-hero p { margin: 0; opacity: 0.9; font-size: 16px; }

        .btn-calendar-hero {
            background-color: white;
            color: #0056b3;
            padding: 12px 25px;
            border-radius: 30px;
            font-weight: bold;
            text-decoration: none;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .btn-calendar-hero:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
            background-color: #f8f9fa;
        }

        /* Navbar Link */
        .nav-link-custom {
            color: #555;
            text-decoration: none;
            margin-right: 20px;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 5px;
            transition: color 0.3s;
        }
        .nav-link-custom:hover { color: #007bff; }

        /* Modale Generale */
        .modal-overlay {
            display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.5); z-index: 9999;
            align-items: center; justify-content: center;
        }
        .modal-overlay.active { display: flex; animation: fadeIn 0.2s ease-out; }
        .modal-box {
            background: white; padding: 25px; border-radius: 10px;
            max-width: 400px; width: 90%; text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            position: relative;
        }

        /* Stili specifici per dettaglio paziente */
        .patient-detail-row {
            display: flex; align-items: center; gap: 10px;
            margin-bottom: 12px; padding-bottom: 8px;
            border-bottom: 1px solid #f0f0f0; text-align: left;
        }
        .patient-detail-row i { color: #007bff; width: 20px; text-align: center; }
        .patient-detail-label { font-weight: bold; color: #555; font-size: 0.9em; display: block; }
        .patient-detail-value { color: #333; font-size: 1em; }
        .close-modal-btn {
            position: absolute; top: 15px; right: 20px;
            background: none; border: none; font-size: 1.5em; cursor: pointer; color: #aaa;
        }
        .close-modal-btn:hover { color: #333; }

        .modal-buttons { display: flex; justify-content: center; gap: 15px; margin-top: 20px; }
        .btn-modal { padding: 10px 20px; border-radius: 5px; border: none; cursor: pointer; font-weight: bold; }
        .btn-cancel { background-color: #e2e6ea; color: #333; }
        .btn-confirm { background-color: #007bff; color: white; }
        @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }

        /* Stile barra filtro aggiornata */
        .filter-bar {
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }
        .btn-filter {
            padding: 8px 15px;
            border-radius: 6px;
            border: 1px solid #ced4da;
            background: white;
            cursor: pointer;
            font-weight: 600;
            color: #555;
            transition: 0.2s;
            font-size: 0.9em;
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .btn-filter:hover { background: #e9ecef; }
        .btn-filter.active { background: #007bff; color: white; border-color: #0056b3; }
    </style>
</head>
<body>

<nav class="navbar" style="background-color: white; padding: 15px; border-bottom: 1px solid #eee; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
    <div class="container" style="display: flex; justify-content: space-between; align-items: center;">
        <h2 style="margin: 0; color: #0056b3; font-size: 1.5rem;"><i class="fa-solid fa-user-doctor"></i> MediBook Medico</h2>

        <div style="display: flex; align-items: center;">
            <a href="${pageContext.request.contextPath}/medico/calendario" class="nav-link-custom">
                <i class="fa-regular fa-calendar-days" style="color: #007bff;"></i> Agenda
            </a>
            <div style="height: 20px; width: 1px; background: #ddd; margin: 0 15px;"></div>
            <span style="font-weight: bold; margin-right: 15px; color: #333;">Dott. ${nomeMedico}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm" style="border-radius: 20px;">Esci</a>
        </div>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty param.successo}">
        <div class="alert alert-success">✅ Operazione completata con successo!</div>
    </c:if>
    <c:if test="${not empty param.errore}">
        <div class="alert alert-danger">⚠️ Errore: <c:out value="${param.errore}"/></div>
    </c:if>

    <div class="dashboard-hero">
        <div>
            <h2><i class="fa-regular fa-calendar-check"></i> La tua Agenda</h2>
            <p>Visualizza i tuoi appuntamenti confermati in una comoda vista mensile o settimanale.</p>
        </div>
        <a href="${pageContext.request.contextPath}/medico/calendario" class="btn-calendar-hero">
            Apri Calendario <i class="fa-solid fa-arrow-right"></i>
        </a>
    </div>

    <h3 class="section-title primary">
        <i class="fa-solid fa-clipboard-list"></i> Visite da Gestire
    </h3>

    <div class="filter-bar">
        <span style="font-weight: 600; color:#555; margin-right: 5px;"><i class="fa-solid fa-filter"></i> Organizza:</span>

        <button onclick="filtraOggi()" class="btn-filter">
            <i class="fa-regular fa-calendar-check"></i> Solo Oggi
        </button>

        <button onclick="ordinaDate('asc')" class="btn-filter">
            <i class="fa-solid fa-arrow-down-short-wide"></i> Data Crescente
        </button>

        <button onclick="ordinaDate('desc')" class="btn-filter">
            <i class="fa-solid fa-arrow-up-wide-short"></i> Data Decrescente
        </button>

        <button onclick="mostraTutte()" class="btn-filter" style="margin-left:auto;">
            <i class="fa-solid fa-rotate-left"></i> MostraTutto
        </button>
    </div>

    <table class="table-custom" id="tabellaDaGestire">
        <thead>
        <tr>
            <th>Data e Ora</th>
            <th>Paziente</th>
            <th>Stato</th>
            <th>Azioni Richieste</th>
        </tr>
        </thead>
        <tbody>
        <c:set var="hasVisiteAttive" value="false" />

        <c:forEach items="${visite}" var="v">
            <c:if test="${v.stato == 'PRENOTATA' || v.stato == 'EFFETTUATA'}">
                <c:set var="hasVisiteAttive" value="true" />
                <tr>
                    <td>
                        <i class="fa-regular fa-calendar"></i> ${v.data}<br>
                        <i class="fa-regular fa-clock"></i> ${v.ora}
                    </td>
                    <td>
                        <strong>${v.paziente.nome} ${v.paziente.cognome}</strong>

                        <button type="button" class="btn-info-paziente" title="Vedi dettagli paziente"
                                onclick="mostraPaziente(this)"
                                data-nome="${v.paziente.nome} ${v.paziente.cognome}"
                                data-cf="${v.paziente.codiceFiscale}"
                                data-email="${v.paziente.email}"
                                data-tel="${v.paziente.telefono}"
                                data-ind="${v.paziente.indirizzo}">
                            <i class="fa-solid fa-circle-info"></i>
                        </button>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${v.stato == 'PRENOTATA'}">
                                <span class="badge bg-info"><i class="fa-solid fa-calendar-check"></i> Prenotata</span>
                            </c:when>
                            <c:when test="${v.stato == 'EFFETTUATA'}">
                                <span class="badge bg-warning text-dark"><i class="fa-solid fa-hourglass-half"></i> Serve Referto</span>
                            </c:when>
                        </c:choose>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${v.stato == 'PRENOTATA'}">
                                <div class="btn-group">
                                    <form id="form-esegui-${v.id}" action="${pageContext.request.contextPath}/medico/cambiaStato" method="post" style="display:none;">
                                        <input type="hidden" name="id" value="${v.id}">
                                        <input type="hidden" name="stato" value="EFFETTUATA">
                                    </form>
                                    <button type="button" class="action-btn btn-esegui"
                                            onclick="chiediConferma('form-esegui-${v.id}', 'Confermi di aver eseguito la visita?', 'Esegui')">
                                        <i class="fa-solid fa-stethoscope"></i> Esegui
                                    </button>

                                    <form id="form-annulla-${v.id}" action="${pageContext.request.contextPath}/medico/cambiaStato" method="post" style="display:none;">
                                        <input type="hidden" name="id" value="${v.id}">
                                        <input type="hidden" name="stato" value="ANNULLATA">
                                    </form>
                                    <button type="button" class="action-btn btn-annulla"
                                            onclick="chiediConferma('form-annulla-${v.id}', 'Vuoi rifiutare questa visita?', 'Annulla')">
                                        <i class="fa-solid fa-ban"></i> Annulla
                                    </button>
                                </div>
                            </c:when>
                            <c:when test="${v.stato == 'EFFETTUATA'}">
                                <a href="${pageContext.request.contextPath}/medico/referto/nuovo?id=${v.id}" class="action-btn btn-referto">
                                    <i class="fa-solid fa-file-pen"></i> Scrivi Referto
                                </a>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>

    <div id="noResultsMsg" style="padding: 20px; text-align: center; color: #777; background: #fff; border: 1px dashed #ddd; border-radius: 8px; display: none;">
        Nessuna visita per oggi.
    </div>

    <c:if test="${!hasVisiteAttive}">
        <div style="padding: 20px; text-align: center; color: #777; background: #fff; border: 1px dashed #ddd; border-radius: 8px;">
            Nessuna visita in attesa di gestione.
        </div>
    </c:if>


    <h3 class="section-title secondary">
        <i class="fa-solid fa-box-archive"></i> Storico e Archivio
    </h3>

    <table class="table-custom" style="opacity: 0.9;">
        <thead>
        <tr>
            <th>Data</th>
            <th>Paziente</th>
            <th>Esito</th>
            <th>Dettagli</th>
        </tr>
        </thead>
        <tbody>
        <c:set var="hasStorico" value="false" />

        <c:forEach items="${visite}" var="v">
            <c:if test="${v.stato == 'CONCLUSA' || v.stato == 'ANNULLATA'}">
                <c:set var="hasStorico" value="true" />
                <tr>
                    <td style="color: #555;">${v.data}</td>
                    <td>${v.paziente.nome} ${v.paziente.cognome}</td>
                    <td>
                        <c:choose>
                            <c:when test="${v.stato == 'CONCLUSA'}">
                                <span class="badge bg-success"><i class="fa-solid fa-check"></i> Conclusa</span>
                            </c:when>
                            <c:when test="${v.stato == 'ANNULLATA'}">
                                <span class="badge bg-danger">Annullata</span>
                            </c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${v.stato == 'CONCLUSA'}">
                            <a href="${pageContext.request.contextPath}/medico/referto/visualizza?id=${v.id}" class="action-btn btn-vedi">
                                <i class="fa-regular fa-eye"></i> Vedi Referto
                            </a>
                        </c:if>
                        <c:if test="${v.stato == 'ANNULLATA'}">
                            <span style="color: #ccc;">-</span>
                        </c:if>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${!hasStorico}">
        <div style="padding: 20px; text-align: center; color: #777; margin-top: 10px;">
            Lo storico è vuoto.
        </div>
    </c:if>

</div>

<div id="modalConferma" class="modal-overlay">
    <div class="modal-box">
        <div style="font-size: 40px; margin-bottom: 10px;">⚠️</div>
        <div class="modal-title" style="font-size: 1.2em; font-weight:bold;">Conferma Azione</div>
        <p class="modal-text" id="modalMessaggio">Sei sicuro?</p>
        <div class="modal-buttons">
            <button class="btn-modal btn-cancel" onclick="chiudiModalConferma()">No, indietro</button>
            <button class="btn-modal btn-confirm" id="btnConfermaFinale" onclick="procediConInvio()">Sì, procedi</button>
        </div>
    </div>
</div>

<div id="modalPaziente" class="modal-overlay">
    <div class="modal-box" style="text-align: left;">
        <button class="close-modal-btn" onclick="chiudiModalPaziente()">&times;</button>

        <h3 style="margin-top:0; color: #007bff; border-bottom: 1px solid #eee; padding-bottom: 10px;">
            <i class="fa-solid fa-id-card"></i> Scheda Paziente
        </h3>

        <div style="margin-top: 20px;">
            <div class="patient-detail-row">
                <i class="fa-solid fa-user"></i>
                <div>
                    <span class="patient-detail-label">Nome e Cognome</span>
                    <span class="patient-detail-value" id="pazNome"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-barcode"></i>
                <div>
                    <span class="patient-detail-label">Codice Fiscale</span>
                    <span class="patient-detail-value" id="pazCF"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-envelope"></i>
                <div>
                    <span class="patient-detail-label">Email</span>
                    <span class="patient-detail-value" id="pazEmail"></span>
                </div>
            </div>

            <div class="patient-detail-row">
                <i class="fa-solid fa-phone"></i>
                <div>
                    <span class="patient-detail-label">Telefono</span>
                    <span class="patient-detail-value" id="pazTel"></span>
                </div>
            </div>

            <div class="patient-detail-row" style="border-bottom: none;">
                <i class="fa-solid fa-map-location-dot"></i>
                <div>
                    <span class="patient-detail-label">Indirizzo</span>
                    <span class="patient-detail-value" id="pazInd"></span>
                </div>
            </div>
        </div>

        <div style="text-align: center; margin-top: 20px;">
            <button class="btn-modal btn-confirm" style="width: 100%;" onclick="chiudiModalPaziente()">Chiudi</button>
        </div>
    </div>
</div>

<script>
    // --- FUNZIONALITÀ FILTRO E ORDINAMENTO ---

    function filtraOggi() {
        const oggi = new Date();
        const dd = String(oggi.getDate()).padStart(2, '0');
        const mm = String(oggi.getMonth() + 1).padStart(2, '0');
        const yyyy = oggi.getFullYear();
        const oggiIT = dd + '/' + mm + '/' + yyyy;
        const oggiISO = yyyy + '-' + mm + '-' + dd;

        let table = document.getElementById('tabellaDaGestire');
        let rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
        let visibleCount = 0;

        for (let i = 0; i < rows.length; i++) {
            let dateCell = rows[i].getElementsByTagName('td')[0];
            if (dateCell) {
                let cellText = dateCell.innerText;
                if (cellText.includes(oggiIT) || cellText.includes(oggiISO)) {
                    rows[i].style.display = '';
                    visibleCount++;
                } else {
                    rows[i].style.display = 'none';
                }
            }
        }
        document.getElementById('noResultsMsg').style.display = (visibleCount === 0) ? 'block' : 'none';
    }

    function mostraTutte() {
        let rows = document.querySelectorAll('#tabellaDaGestire tbody tr');
        rows.forEach(row => row.style.display = '');
        document.getElementById('noResultsMsg').style.display = 'none';
    }

    function ordinaDate(direzione) {
        mostraTutte();
        let table = document.getElementById("tabellaDaGestire");
        let tbody = table.querySelector("tbody");
        let rows = Array.from(tbody.querySelectorAll("tr"));

        rows.sort((a, b) => {
            let dateTextA = a.cells[0].innerText.trim();
            let dateTextB = b.cells[0].innerText.trim();
            let dateA = parseCustomDate(dateTextA);
            let dateB = parseCustomDate(dateTextB);

            if (direzione === 'asc') return dateA - dateB;
            else return dateB - dateA;
        });
        rows.forEach(row => tbody.appendChild(row));
    }

    function parseCustomDate(str) {
        const isoMatch = str.match(/(\d{4})-(\d{2})-(\d{2})/);
        if (isoMatch) {
            const timeMatch = str.match(/(\d{2}):(\d{2})/);
            let timeStr = timeMatch ? "T" + timeMatch[0] + ":00" : "T00:00:00";
            return new Date(isoMatch[0] + timeStr);
        }
        const itMatch = str.match(/(\d{2})\/(\d{2})\/(\d{4})/);
        if (itMatch) {
            let isoStr = itMatch[3] + "-" + itMatch[2] + "-" + itMatch[1];
            const timeMatch = str.match(/(\d{2}):(\d{2})/);
            let timeStr = timeMatch ? "T" + timeMatch[0] + ":00" : "T00:00:00";
            return new Date(isoStr + timeStr);
        }
        return new Date(0);
    }

    // --- MODALE CONFERMA AZIONE ---
    let formDaInviareId = null;

    function chiediConferma(formId, messaggio, testoBottone = "Sì, procedi") {
        formDaInviareId = formId;
        document.getElementById('modalMessaggio').innerText = messaggio;
        const btn = document.getElementById('btnConfermaFinale');
        btn.innerText = testoBottone;

        if(testoBottone === 'Annulla') {
            btn.style.backgroundColor = '#dc3545';
        } else {
            btn.style.backgroundColor = '#28a745';
        }
        document.getElementById('modalConferma').classList.add('active');
    }

    function chiudiModalConferma() {
        document.getElementById('modalConferma').classList.remove('active');
        formDaInviareId = null;
    }

    function procediConInvio() {
        if (formDaInviareId) document.getElementById(formDaInviareId).submit();
    }

    // --- MODALE DETTAGLI PAZIENTE ---
    function mostraPaziente(btn) {
        // Legge i dati dagli attributi data- del bottone cliccato
        document.getElementById('pazNome').innerText = btn.getAttribute('data-nome') || "N/D";
        document.getElementById('pazCF').innerText = btn.getAttribute('data-cf') || "N/D";
        document.getElementById('pazEmail').innerText = btn.getAttribute('data-email') || "N/D";
        document.getElementById('pazTel').innerText = btn.getAttribute('data-tel') || "N/D";
        document.getElementById('pazInd').innerText = btn.getAttribute('data-ind') || "N/D";

        document.getElementById('modalPaziente').classList.add('active');
    }

    function chiudiModalPaziente() {
        document.getElementById('modalPaziente').classList.remove('active');
    }

    // Chiusura modali cliccando fuori
    window.onclick = function(e) {
        if (e.target.classList.contains('modal-overlay')) {
            e.target.classList.remove('active');
        }
    }
</script>

</body>
</html>