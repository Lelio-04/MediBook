# MediBook
Progetto realizzato per il corso di Ingegneria del Software - Università degli studi di Salerno 2025/2026

## 🏥 MediBook - Medical Management System

**MediBook** è un'applicazione Web Full-Stack per la gestione di servizi medici. Il progetto utilizza un'architettura moderna separando il Backend (API REST in Java) dal Frontend (Interfaccia Utente in React).

---

## 🚀 Tecnologie Utilizzate

### Backend
* **Java 17+**
* **Spring Boot 3** (Web, Data JPA)
* **Hibernate** (ORM)
* **MySQL Connector**
* **Maven**

### Frontend
* **React.js**
* **Node.js** & **NPM**
* **Axios** (Client HTTP)
* **React Router Dom**

### Database
* **MySQL 8.0**

---

## 🛠️ Prerequisiti

* **Java JDK 17** o superiore
* **Node.js** (Versione LTS)
* **MySQL Server**
* **IntelliJ IDEA** (Consigliato)

---

## ⚙️ Configurazione Iniziale

### 1. Database
Creare lo schema nel database MySQL eseguendo questo comando SQL:
```sql
CREATE DATABASE medibook;
```

### 2. Configurazione Credenziali

```bash
spring.datasource.url=jdbc:mysql://localhost:3306/medibook?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=TUA_PASSWORD_QUI
```

---

# 🏥 MediBook - Frontend Client

Questo è il client interfaccia utente per il progetto **MediBook**, sviluppato in **JavaScript**.

---

## 🚀 Tecnologie Frontend

* **JavaScript** 

---

## Accesso al sito

Aprire Intellij ed esegui il file MedibookApplication.
In seguito cerca sul tuo browser http://localhost:8080

---
