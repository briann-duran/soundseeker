import React from 'react';
import shield from '../Imgs/iconPolicies/shieldIcon.svg';
import '../Components/Styles/PoliciesInstrument.css';

const PoliciesInstrument = ({ instrument }) => {
  return (
    <div className="div-policies">
      <img src={shield} alt="" />
      <h2 className="title-policies">Políticas del producto</h2>
      <div className="content-policies">
        {instrument?.categoria?.politicas &&
          instrument?.categoria?.politicas.map((politica) => (
            <article className="policies" key={politica.id}>
              <div className="policies-desc">
                <div className="policies-title-desc">
                  <img
                    className="caract-icono"
                    src={`${import.meta.env.VITE_APP_IMAGE_URL}${politica.imagen}`}
                    alt={politica.titulo}
                    style={{ width: '70px', height: '70px' }}
                  />
                  <h3 className="title-cuidados">{politica.titulo}</h3>
                  <p>{politica.descripcion}</p>
                </div>
              </div>
            </article>
          ))}
      </div>
    </div>
  );
};

export default PoliciesInstrument;
