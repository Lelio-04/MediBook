# MediBook
Progetto realizzato per il corso di Ingegneria del Software - Università degli studi di Salerno 2025/2026

## 🛠️ Prerequisiti

* **Java JDK 17** o superiore
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

## Accesso al sito

Aprire Intellij ed esegui il file MedibookApplication.
In seguito cerca sul tuo browser http://localhost:8080

---
