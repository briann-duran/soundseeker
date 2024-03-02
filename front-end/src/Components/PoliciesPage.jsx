import React from 'react';
import PropTypes from 'prop-types';

const PoliciesPage = ({ id, titulo, descripcion, imagen }) => {
  return (
    <article className="policies" key={id}>
      <div className="policies-desc">
        <div className="policies-title-desc">
          <img
            className="caract-icono"
            src={`${import.meta.env.VITE_APP_IMAGE_URL}${imagen}`}
            alt={titulo}
            style={{ width: '70px', height: '70px' }}
          />
          <h3 className="title-cuidados">{titulo}</h3>
          <p>{descripcion}</p>
        </div>
      </div>
    </article>
  );
};

PoliciesPage.propTypes = {
  id: PropTypes.number,
  titulo: PropTypes.string,
  descripcion: PropTypes.string,
  imagen: PropTypes.string,
};

export default PoliciesPage;
