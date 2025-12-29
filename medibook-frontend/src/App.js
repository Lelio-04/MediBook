import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';

function App() {
  return (
      <Router>
        <Routes>
          {/* Pagina di Default: Login */}
          <Route path="/" element={<LoginPage />} />

          {/* Placeholder per le future pagine (le faremo tra poco) */}
          <Route path="/medico" element={<h1>Dashboard Medico (Da implementare)</h1>} />
          <Route path="/segreteria" element={<h1>Dashboard Segreteria (Da implementare)</h1>} />
          <Route path="/paziente" element={<h1>Home Paziente (Da implementare)</h1>} />

          {/* Se l'utente scrive un URL a caso, torna al login */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Router>
  );
}

export default App;