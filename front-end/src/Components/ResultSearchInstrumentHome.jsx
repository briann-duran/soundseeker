import React from 'react';
import Card from './Card';
import './Styles/Recomends.css';

const ResultSearchInstrumentHome = ({ instruments }) => {
  return (
    <>
      <h3 className="recomends-title">Instrumentos disponibles</h3>
      <div className="recomends">
        {instruments ? (
          instruments.length > 0 ? (
            instruments.map((instrument) => (
              <Card key={instrument.id} instrument={instrument} />
            ))
          ) : (
            <p>No hay disponibilidad para las fechas seleccionadas.</p>
          )
        ) : (
          <p>Cargando datos...</p>
        )}
      </div>
    </>
  );
};

export default ResultSearchInstrumentHome;
