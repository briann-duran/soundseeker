import React from 'react';
import { Navigate } from 'react-router-dom';
import { useRecipeStates } from '../Context/globalContext';
import '../Components/Styles/Favoritos.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Card from '../Components/Card';
import 'sweetalert2/dist/sweetalert2.min.css';

const Favoritos = () => {
  const { favs } = useRecipeStates();

  const storedUser = JSON.parse(localStorage.getItem('user'));

  if (!storedUser) return <Navigate to={'/login'} />;

  return (
    <div className="list-favs">
      {favs ? (
        favs.length > 0 ? (
          favs.map((instrument) => (
            <Card key={instrument.id} instrument={instrument} />
          ))
        ) : (
          <div className="div-sin-favs">
            <p>No tienes favoritos.</p>
          </div>
        )
      ) : (
        <p>Cargando datos...</p>
      )}
      <ToastContainer />
    </div>
  );
};

export default Favoritos;
