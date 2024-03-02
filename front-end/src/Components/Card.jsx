import React, { useEffect, useState } from 'react';
import './Styles/Card.css';
import { Link, useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { useRecipeStates } from '../Context/globalContext';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import { FaStar } from 'react-icons/fa';
import SocialDropDown from './AdmCaracteristicas/SocialDropDown/SocialDropDown';

const Card = ({ instrument }) => {
  const [addFavorito, setAddFavorito] = useState(false);
  const { addToFavoritos, favs, setInstrument } = useRecipeStates();
  const navigate = useNavigate();

  const storedUser = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    const instrumentoEnFavoritos = favs.some((fav) => fav.id === instrument.id);
    setAddFavorito(instrumentoEnFavoritos);
  }, [favs, instrument.id]);

  const handleToggleFavorito = () => {
    if (!storedUser) {
      Swal.fire({
        title: '¡Hola! Para agregar favoritos, ingresa a tu cuenta',
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: 'Ingresar',
        denyButtonText: `Registrarme`,
        confirmButtonColor: '#3563E9',
        denyButtonColor: '#3563E9',
      }).then((result) => {
        if (result.isConfirmed) {
          window.location.href = '/login';
        } else if (result.isDenied) {
          window.location.href = '/signup';
        }
      });
    } else {
      const paramsAddFavs = {
        productoId: instrument.id,
        nombreUsuario: storedUser.nombreUsuario,
      };
      addToFavoritos(paramsAddFavs);
      toast.success(
        addFavorito
          ? 'Producto eliminado de favoritos'
          : '¡Tu producto se ha añadido a favoritos!',
        {
          position: toast.POSITION.TOP_CENTER,
          autoClose: 1200,
        }
      );
    }
  };

  const btnRent = () => {
    setInstrument(instrument);
    navigate('/rent');
  };

  return (
    <div className="card">
      <div className="card-modal-container-social"></div>
      <div className="social-favs">
        <SocialDropDown instrument={instrument} />
        <div className="fav-card">
          <button
            className="btn-icon-heart"
            onClick={handleToggleFavorito}
            title={addFavorito ? 'Eliminar de favoritos' : 'Añadir a favoritos'}
          >
            <div>
              <FontAwesomeIcon
                className="heart"
                icon={faHeart}
                color={addFavorito ? 'red' : 'silver'}
              />
            </div>
          </button>
        </div>
      </div>
      <Link className="link-card" to={'/details/' + instrument.id}>
        <h4 className="card-h4">{instrument.nombre}</h4>
        <div className="card-star" style={{ width: '40px', height: '40px' }}>
          <FaStar color="#3563e9" size={20} /> <p>{instrument.calificacion}</p>
        </div>
        <img
          className="card-image"
          src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrument.imagenes[0]}`}
          alt={instrument.nombre}
        />
      </Link>
      <div className="card-price-button">
        <h3 className="card-price">$ {instrument.precio} / día</h3>
        <Link to={'/rent'}>
          <button className="btn-rent" onClick={btnRent}>
            Alquilar
          </button>
        </Link>
      </div>
      <ToastContainer />
    </div>
  );
};

export default Card;
