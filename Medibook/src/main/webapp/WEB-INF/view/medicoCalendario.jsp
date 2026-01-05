<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Calendario Appuntamenti - MediBook</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.10/index.global.min.js'></script>

    <style>
        /* Stile Calendario */
        #calendar {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.05);
        }
        .fc-event { cursor: pointer; border: none; }
        .fc-daygrid-event { font-size: 0.9em; padding: 2px 5px; border-radius: 4px; }
        .fc-toolbar-title { font-size: 1.5em !important; color: #333; text-transform: capitalize; }
        .fc-button-primary { background-color: #007bff !important; border-color: #007bff !important; }

        /* --- STILE MODALE POPUP --- */
        .modal-overlay {
            display: none; /* Nascosto di default */
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.5); /* Sfondo scuro semitrasparente */
            z-index: 9999;
            align-items: center; justify-content: center;
            animation: fadeIn 0.2s ease-out;
        }

        .modal-card {
            background: white;
            width: 90%; max-width: 450px;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            overflow: hidden;
            font-family: sans-serif;
        }

        .modal-header {
            background-color: #007bff; color: white;
            padding: 15px 20px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .modal-header h3 { margin: 0; font-size: 18px; display: flex; align-items: center; gap: 10px; }
        .close-btn { background: none; border: none; color: white; font-size: 20px; cursor: pointer; }

        .modal-body { padding: 25px; line-height: 1.6; color: #333; }
        .info-row { display: flex; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .info-label { font-weight: bold; width: 100px; color: #555; }

        .modal-footer {
            padding: 15px 20px; background-color: #f8f9fa;
            display: flex; justify-content: flex-end; gap: 10px;
            border-top: 1px solid #eee;
        }

        .btn-modal { padding: 8px 16px; border-radius: 5px; text-decoration: none; font-size: 14px; cursor: pointer; border:none; }
        .btn-chiudi { background: #6c757d; color: white; }
        .btn-azione { background: #28a745; color: white; font-weight: bold; display: flex; align-items: center; gap: 5px;}
        .btn-azione:hover { background: #218838; }

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    </style>
</head>
<body style="background-color: var(--bg-body);">

<nav class="navbar" style="background-color: white; padding: 15px; border-bottom: 1px solid #eee; margin-bottom: 30px;">
    <div class="container" style="display: flex; justify-content: space-between; align-items: center;">
        <h2 style="margin: 0; color: #0056b3;"><i class="fa-solid fa-calendar-days"></i> Agenda Medico</h2>
        <a href="${pageContext.request.contextPath}/medico" class="btn btn-primary">← Torna alla Dashboard</a>
    </div>
</nav>

<div class="container">
    <div id='calendar'></div>
</div>

<div id="eventoModal" class="modal-overlay">
    <div class="modal-card">
        <div class="modal-header">
            <h3><i class="fa-regular fa-user"></i> Dettaglio Visita</h3>
            <button class="close-btn" onclick="chiudiModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="info-row">
                <span class="info-label">Paziente:</span>
                <span id="modalPaziente">Rossi Mario</span>
            </div>
            <div class="info-row">
                <span class="info-label">Data:</span>
                <span id="modalData">12/10/2026</span>
            </div>
            <div class="info-row">
                <span class="info-label">Ora:</span>
                <span id="modalOra">10:30</span>
            </div>
            <div class="info-row" style="border: none;">
                <span class="info-label">Stato:</span>
                <span class="badge bg-success" style="background:#d4edda; color:#155724; padding:2px 8px; border-radius:10px;">Confermata</span>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn-modal btn-chiudi" onclick="chiudiModal()">Chiudi</button>
            <a href="#" id="btnVaiAlReferto" class="btn-modal btn-azione">
                <i class="fa-solid fa-file-pen"></i> Gestisci / Referta
            </a>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var calendarEl = document.getElementById('calendar');
        var eventiDaDb = ${eventiJson}; // Recupera il JSON dal Controller

        var calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'it',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,listWeek'
            },
            events: eventiDaDb, // Carica i dati

            // --- QUANDO CLICCHI SU UN EVENTO ---
            eventClick: function(info) {
                info.jsEvent.preventDefault(); // Evita comportamenti default del browser

                // 1. Recupera i dati dell'evento cliccato
                var titolo = info.event.title;
                var dataInizio = info.event.start;
                var idVisita = info.event.id; // L'ID che abbiamo passato dal Controller

                // Formattazione Data e Ora per l'Italia
                var dataStringa = dataInizio.toLocaleDateString('it-IT', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
                var oraStringa = dataInizio.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' });

                // 2. Inserisce i dati dentro l'HTML del Modale
                document.getElementById('modalPaziente').innerText = titolo;
                document.getElementById('modalData').innerText = dataStringa;
                document.getElementById('modalOra').innerText = oraStringa;

                // 3. Aggiorna il link del pulsante "Gestisci / Referta"
                // Nota: Assicurati che l'URL porti alla pagina corretta (es. scrivi referto o dettaglio visita)
                var linkBtn = document.getElementById('btnVaiAlReferto');
                linkBtn.href = '${pageContext.request.contextPath}/medico?id=' + idVisita;

                // 4. Mostra il Modale
                document.getElementById('eventoModal').style.display = 'flex';
            }
        });

        calendar.render();
    });

    // Funzione per chiudere il popup
    function chiudiModal() {
        document.getElementById('eventoModal').style.display = 'none';
    }

    // Chiude il modale se clicchi fuori dalla finestra bianca
    window.onclick = function(event) {
        var modal = document.getElementById('eventoModal');
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
</script>

</body>
</html>