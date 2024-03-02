import React, { useState } from 'react';
import './Navbar.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars, faUser } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import { useRecipeAuth } from '../../Context/authContext';
import SearchPrincipal from '../Buscador/SearchPrincipal';
import searchIcon from '/src/Imgs/search.webp';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

const Navbar = () => {
  const { user, logout } = useRecipeAuth();

  const [showModal, setShowModal] = useState(false);

  const storedUser = JSON.parse(localStorage.getItem('user'));

  const [isNavOpen, setIsNavOpen] = useState(false);

  let isAdmin = false;

  if (storedUser) isAdmin = storedUser.roles.includes('ROLE_ADMIN');

  const toggleNav = () => setIsNavOpen(!isNavOpen);

  const handleClick = () => {
    window.location.reload();
    window.location.href = to;
  };

  const handleSearchClick = () => setShowModal(true);

  const closeModal = () => setShowModal(false);

  const favsButton = () => {
    const storedUser = JSON.parse(localStorage.getItem('user'));
    if (!storedUser) {
      Swal.fire({
        title: '¡Hola! Para ver tus favoritos, ingresa a tu cuenta',
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
      window.location.href = '/favoritos';
    }
  };

  return (
    <>
      {!showModal && (
        <div className="navbar">
          <div className="nav-logo">
            <Link to="/" onClick={handleClick}>
              <div className="logo-principal">
                <p className="sound">Sound</p>
                <p className="seeker">Seeker</p>
              </div>
              <div className="logo-lema">
                <p className="lema">Reservá tu melodía perfecta.</p>
              </div>
            </Link>
          </div>
          <button
            className="btn-search-principal"
            onClick={handleSearchClick}
            title="Presiona para buscar un instrumento"
            type="button"
          >
            <img
              className="icon-btn-search"
              src={searchIcon}
              alt="Icono de buscar"
              width="24"
              height="24"
            />
            <p>Buscar</p>
          </button>
          <div className={`div-nav ${isNavOpen ? 'show-links' : ''}`}>
            <Link className="linkButton" to="/" onClick={handleClick}>
              <span className="hover-underline-animation">Inicio</span>
            </Link>
            <Link className="linkButton" to="/categories">
              <span className="hover-underline-animation">Instrumentos</span>
            </Link>
            <Link className="linkButton" to="/contact">
              <span className="hover-underline-animation">Contacto</span>
            </Link>
          </div>

          {storedUser ? (
            <div className="div-cerrar-sesion">
              <div className="cerrar-sesion-nombreUsuario">
                <p>Hola, {storedUser.nombreUsuario}</p>
              </div>
              <MenuDropDown isAdmin={isAdmin} />
            </div>
          ) : (
            <div className="nav-login">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <Link to="/login">
                <button className="btn-nav-iniciar">Iniciar sesión</button>
              </Link>
            </div>
          )}
          <button
            className={`burger ${isNavOpen ? 'active' : ''}`}
            onClick={toggleNav}
            title={'Menú'}
            type="button"
          >
            <FontAwesomeIcon icon={faBars} className="icon" />
          </button>
        </div>
      )}
      {showModal && <SearchPrincipal closeModal={closeModal} />}
    </>
  );
};

export default Navbar;
