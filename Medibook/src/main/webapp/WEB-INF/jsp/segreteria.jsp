<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Area Segreteria - MediBook</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            color: #2c3e50;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .header h1 {
            font-size: 24px;
            font-weight: 600;
        }

        .logout-btn {
            background: #e74c3c;
            color: white;
            padding: 10px 24px;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .logout-btn:hover {
            background: #c0392b;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
        }

        .container {
            max-width: 1400px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .info-box {
            background: #e8f4f8;
            border-left: 4px solid #3498db;
            padding: 16px 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            color: #2c3e50;
        }

        .info-box strong {
            color: #2980b9;
        }

        .section-title {
            font-size: 22px;
            color: #2c3e50;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 3px solid #667eea;
            display: inline-block;
        }

        table {
            width: 100%;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        }

        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        th {
            padding: 16px;
            text-align: left;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }

        td {
            padding: 14px 16px;
            border-bottom: 1px solid #f0f0f0;
            vertical-align: middle;
        }

        tbody tr {
            transition: background 0.2s ease;
        }

        tbody tr:hover {
            background: #f8f9fa;
        }

        .stato-badge {
            padding: 6px 14px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 12px;
            display: inline-block;
        }

        .stato-programmata {
            background: #fff3cd;
            color: #856404;
        }

        .stato-effettuata {
            background: #d4edda;
            color: #155724;
        }

        .stato-annullata {
            background: #f8d7da;
            color: #721c24;
        }

        input[type="date"],
        input[type="time"] {
            padding: 8px 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 14px;
            font-family: inherit;
            transition: all 0.3s ease;
            min-width: 150px;
        }

        input[type="date"]:focus,
        input[type="time"]:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .btn-save {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            transition: all 0.3s ease;
        }

        .btn-save:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(17, 153, 142, 0.3);
        }

        .btn-save:active {
            transform: translateY(0);
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #95a5a6;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>🖥️ Area Segreteria - MediBook</h1>
    <a href="/" class="logout-btn">🚪 Esci</a>
</div>

<div class="container">
    <h3 class="section-title">📋 Gestione Agenda (Dott. Rossi)</h3>

    <div class="info-box">
        <strong>ℹ️ Istruzioni:</strong> Modifica direttamente data e ora nei campi sottostanti e premi il pulsante "💾 Salva" sulla riga corrispondente per aggiornare l'appuntamento.
    </div>

    <c:choose>
        <c:when test="${not empty visite}">
            <table>
                <thead>
                <tr>
                    <th>Paziente</th>
                    <th>Stato</th>
                    <th>Data</th>
                    <th>Ora</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${visite}" var="v">
                    <tr>
                        <form id="form-${v.id}" action="/segreteria/modifica" method="post">
                            <input type="hidden" name="id" value="${v.id}">
                        </form>

                        <td>
                            <strong>${v.paziente.cognome} ${v.paziente.nome}</strong>
                        </td>
                        <td>
                                    <span class="stato-badge stato-${v.stato.toLowerCase()}">
                                            ${v.stato}
                                    </span>
                        </td>
                        <td>
                            <input type="date" name="data" value="${v.data}" form="form-${v.id}" required>
                        </td>
                        <td>
                            <input type="time" name="ora" value="${v.ora}" form="form-${v.id}" required>
                        </td>
                        <td>
                            <button type="submit" form="form-${v.id}" class="btn-save">
                                💾 Salva modifiche
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <p style="font-size: 48px;">📅</p>
                <p style="font-size: 18px; margin-top: 10px;"><i>Nessuna visita in programma</i></p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
