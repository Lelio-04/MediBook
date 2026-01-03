<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Area Medico - MediBook</title>
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
            background: rgba(255, 255, 255, 0.2);
            color: white;
            padding: 10px 24px;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s ease;
            border: 2px solid rgba(255, 255, 255, 0.3);
        }

        .logout-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
        }

        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
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
            padding: 16px;
            border-bottom: 1px solid #f0f0f0;
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

        .btn-concludi {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            color: white;
            border: none;
            padding: 8px 18px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            transition: all 0.3s ease;
        }

        .btn-concludi:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(17, 153, 142, 0.3);
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
    <h1>👨‍⚕️ Benvenuto, Dott. ${nomeMedico}</h1>
    <a href="/" class="logout-btn">🚪 Logout</a>
</div>

<div class="container">
    <h3 class="section-title">📅 Le tue visite programmate</h3>

    <table>
        <thead>
        <tr>
            <th>Data</th>
            <th>Ora</th>
            <th>Paziente</th>
            <th>Stato</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${visite}" var="v">
            <tr>
                <td><strong>${v.data}</strong></td>
                <td>${v.ora}</td>
                <td>${v.paziente.nome} ${v.paziente.cognome}</td>
                <td>
                            <span class="stato-badge ${v.stato == 'EFFETTUATA' ? 'stato-effettuata' : 'stato-programmata'}">
                                    ${v.stato}
                            </span>
                </td>
                <td>
                    <c:if test="${v.stato != 'EFFETTUATA'}">
                        <form action="/medico/cambiaStato" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${v.id}">
                            <input type="hidden" name="stato" value="EFFETTUATA">
                            <button type="submit" class="btn-concludi">✅ Concludi visita</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty visite}">
        <div class="empty-state">
            <p style="font-size: 48px;">📋</p>
            <p style="font-size: 18px; margin-top: 10px;">Nessuna visita programmata</p>
        </div>
    </c:if>
</div>
</body>
</html>
