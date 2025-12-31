import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DashboardMedico from './pages/DashboardMedico';
import DashboardSegreteria from './pages/DashboardSegreteria'; // <--- IMPORTA QUESTO

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/medico" element={<DashboardMedico />} />

                {/* Colleghiamo la Dashboard Segreteria */}
                <Route path="/segreteria" element={<DashboardSegreteria />} />

                <Route path="/paziente" element={<h1>Home Paziente (In costruzione)</h1>} />
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </Router>
    );
}

export default App;