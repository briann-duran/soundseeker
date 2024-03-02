import React, { useEffect, useState } from 'react';
import './Producto.scss';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Toaster } from 'sonner';
import PoliciesInstrument from '../PoliciesInstrument';
import InstrumentAvailability from '../InstrumentAvailability';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { useRecipeStates } from '../../Context/globalContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast, ToastContainer } from 'react-toastify';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import { FaStar } from 'react-icons/fa';
import SocialDropDown from '../AdmCaracteristicas/SocialDropDown/SocialDropDown';

const Producto = () => {
  const [instrument, setInstrument] = useState([]);
  const params = useParams();
  const [imgSeleccionada, setImgSeleccionada] = useState(0);
  const [addFavorito, setAddFavorito] = useState(false);
  const { addToFavoritos, favs } = useRecipeStates();
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

  const getInstrument = async () => {
    const res = await fetch(
      `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/${params.id}`
    );
    const data = await res.json();
    setInstrument(data);
  };

  useEffect(() => {
    getInstrument().then();
  }, []);

  return (
    <div>
      <Toaster position="top-right" richColors />
      <div className="producto">
        <div className="izquierda">
          <div className="mainImg">
            {instrument.imagenes && instrument.imagenes.length > 0 ? (
              <img
                src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrument.imagenes[imgSeleccionada]}`}
                alt={instrument.nombre}
              />
            ) : (
              <p>No hay imágenes disponibles.</p>
            )}
          </div>
          <div className="imgs">
            {instrument.imagenes && instrument.imagenes.length > 0 ? (
              <Carousel
                showThumbs={false}
                selectedItem={imgSeleccionada}
                onChange={setImgSeleccionada}
                emulateTouch={true}
                showStatus={true}
                infiniteLoop
                interval={0}
              >
                {instrument.imagenes.map((imagen, index) => (
                  <div key={index}>
                    <img
                      src={`${import.meta.env.VITE_APP_IMAGE_URL}${imagen}`}
                      alt={`Imagen ${index + 1}`}
                    />
                  </div>
                ))}
              </Carousel>
            ) : (
              <p>No hay imágenes disponibles.</p>
            )}
          </div>
        </div>
        <div className="derecha">
          <div className="social-favs">
            <SocialDropDown instrument={instrument} />
            <div className="fav-card">
              <button
                className="btn-icon-heart"
                onClick={handleToggleFavorito}
                title={
                  addFavorito ? 'Eliminar de favoritos' : 'Añadir a favoritos'
                }
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
          <div className="title">
            <h1>{instrument.nombre}</h1>
          </div>
          <div className="descripcion">
            <p className="p-descripcion">{instrument.descripcion}</p>
          </div>
          <div className="footer">
            <div className="precio">
              <span>
                <strong>$ {instrument.precio}</strong> / día
              </span>
            </div>
            <div className="cali-instrument-availa">
              <FaStar color="#3563e9" size={20} />{' '}
              <p>Calificación {instrument.calificacion}</p>
            </div>
          </div>
        </div>
      </div>
      <div className="caracteristicas">
        <h2>Características</h2>
        <div className="caracteristicas-list">
          {instrument.caracteristicas &&
            instrument.caracteristicas.map((caracteristica) => (
              <div key={caracteristica.id} className="caracteristica-item">
                <p>{caracteristica.nombre}</p>
                <img
                  className="caract-icono"
                  src={`${import.meta.env.VITE_APP_IMAGE_URL}${caracteristica.icono}`}
                  alt={caracteristica.nombre}
                  style={{ width: '70px', height: '70px' }}
                />
              </div>
            ))}
        </div>
      </div>
      <div className="div-InstrumentAvailability">
        <InstrumentAvailability instrument={instrument} />
      </div>
      <div>
        <PoliciesInstrument instrument={instrument} />
      </div>
      <ToastContainer />
    </div>
  );
};

export default Producto;
