import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import CardHistoryRent2 from '../Components/CardHistoryrent/CardHistoryRent2.jsx';
import '../Components/Styles/HistoryRent.css';

const HistoryRent = () => {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const storedUser = JSON.parse(localStorage.getItem('user'));
  if (!storedUser) {
    return <Navigate to={'/login'} />;
  }

  const historialApi = async () => {
    const url = `${import.meta.env.VITE_APP_BACK_END_URL}/reservas?nombreUsuario=${storedUser.nombreUsuario}`;
    const config = {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwt')}`,
      },
    };

    try {
      const response = await fetch(url, config);
      if (!response.ok) throw new Error('Error al obtener datos');
      const data = await response.json();
      setHistorial(data);
      setLoading(false);
    } catch (error) {
      setError('No se pudieron cargar los datos');
      setLoading(false);
    }
  };

  useEffect(() => {
    historialApi().then();
  }, []);

  return (
    <div className="content-history">
      {loading ? (
        <p>Cargando datos...</p>
      ) : error ? (
        <p>{error}</p>
      ) : historial.length > 0 ? (
        historial.map((rent) => (
          <CardHistoryRent2
            key={rent.id}
            rent={rent}
            historialApi={historialApi}
          />
        ))
      ) : (
        <p>No hay datos disponibles.</p>
      )}
    </div>
  );
};

export default HistoryRent;
