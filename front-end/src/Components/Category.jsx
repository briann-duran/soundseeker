import React from 'react';
import './Styles/Category.scss';
import { useRecipeStates } from '../Context/globalContext';
import 'swiper/css';
import 'swiper/css/free-mode';
import 'swiper/css/pagination';

const Category = () => {
  const { cate } = useRecipeStates();

  if (!cate || !Array.isArray(cate)) return <p>Cargando categorías...</p>;

  return (
    <>
      <div className="cate">
        <h3 className="recomends-title">Categorías</h3>
        <div className="slider-categorias2">
          <div className="slide-track">
            {cate.map((image) => (
              <div className="slide" key={image.id}>
                <img
                  src={`${import.meta.env.VITE_APP_IMAGE_URL}${image.imagen}`}
                  alt={image.nombre}
                  style={{
                    height: '300px',
                    width: 'auto',
                    margin: '10px',
                    borderRadius: '8px',
                  }}
                />
                <h3>{image.nombre}</h3>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
};
export default Category;
