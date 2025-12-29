import axios from 'axios';

// Creiamo un'istanza di Axios collegata al tuo Backend Spring Boot
const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Porta 8080 dove gira Spring
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;