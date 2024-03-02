import React from 'react';
import './Styles/CardCategory.css';

const CardCategory = ({ catego }) => {
  return (
    <div className="card-category">
      <div className="card-category-content">
        <img
          src={`${import.meta.env.VITE_APP_IMAGE_URL}${catego.imagen}`}
          alt={catego.nombre}
        />
      </div>
    </div>
  );
};

export default CardCategory;
