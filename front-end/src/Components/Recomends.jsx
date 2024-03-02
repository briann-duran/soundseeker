import React from 'react';
import Card from './Card';
import './Styles/Recomends.css';
import { useRecipeStates } from '../Context/globalContext';

const Recomends = () => {
  const { random } = useRecipeStates();

  return (
    <>
      <h3 className="recomends-title">Recomendados</h3>
      <div className="recomends">
        {random ? (
          random.length > 0 ? (
            random.map((instrument) => (
              <Card key={instrument.id} instrument={instrument} />
            ))
          ) : (
            <p>No hay datos disponibles.</p>
          )
        ) : (
          <p>Cargando datos...</p>
        )}
      </div>
    </>
  );
};

export default Recomends;
