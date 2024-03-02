import React, { useEffect, useState } from 'react';
import PoliciesPage from '../Components/PoliciesPage.jsx';
import shield from '../Imgs/iconPolicies/shieldIcon.svg';

const Politicas = () => {
  const [politicas, setPoliticas] = useState([]);

  useEffect(() => {
    fetch(`${import.meta.env.VITE_APP_BACK_END_URL}/politicas`)
      .then((res) => res.json())
      .then((data) => setPoliticas(data))
      .catch();
  }, []);

  const style = {
    maxWidth: 1280,
    borderRadius: '14px',
    boxShadow: '0px 20px 30px #070c8f21',
  };

  return (
    <div className="div-policies" style={style}>
      <img src={shield} alt="Escudo de políticas" />
      <h2 className="title-policies">Políticas de los productos</h2>
      <div className="content-policies">
        {politicas.map((policy) => (
          <PoliciesPage
            key={policy.id}
            id={policy.id}
            titulo={policy.titulo}
            descripcion={policy.descripcion}
            imagen={policy.imagen}
          />
        ))}
      </div>
    </div>
  );
};

export default Politicas;
