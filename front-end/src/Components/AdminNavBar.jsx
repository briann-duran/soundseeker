import React, { useState } from 'react';
import '../Components/Styles/AdminNavBar.css';
import navHome from '../Imgs/iconNavHome/homeWhite.png';
import navHomeBlack from '../Imgs/iconNavHome/homeBlack.png';
import navCategory from '../Imgs/iconNavHome/categoryWhite.svg';
import navCategoryBlack from '../Imgs/iconNavHome/categoryBlack.svg';
import navInstrument from '../Imgs/iconNavHome/instrumentWhite.svg';
import navInstrumentBlack from '../Imgs/iconNavHome/instrumentBlack.svg';
import navUser from '../Imgs/iconNavHome/userWhite.svg';
import navUserBlack from '../Imgs/iconNavHome/userBlack.svg';
import navCharasteristic from '../Imgs/iconNavHome/characteristicWhite.svg';
import navCharasteristicBlack from '../Imgs/iconNavHome/characteristicBlack.svg';
import logOut from '../Imgs/iconNavHome/logout.svg';
import { Link, useNavigate } from 'react-router-dom';
import { useRecipeAuth } from '../Context/authContext';

const AdminNavBar = () => {
  const [activeButton, setActiveButton] = useState('instrument');

  const storedUser = JSON.parse(localStorage.getItem('user'));

  const { user, logout } = useRecipeAuth();

  const handleButtonClick = (buttonName) => {
    setActiveButton(buttonName);
  };

  const navigate = useNavigate();

  const handleCerrar = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="box">
      <div>
        <h2 className="title-panel">| Panel Administrador</h2>
        <p>{storedUser.nombre}</p>
      </div>
      <nav className="navlinks">
        <Link to="/">
          <button
            className={`nav-button-admin ${
              activeButton === 'home' ? 'active-button' : ''
            }`}
            onClick={() => handleButtonClick('home')}
          >
            <img
              className="navHome"
              alt="home"
              src={activeButton === 'home' ? navHomeBlack : navHome}
            />
            <p>Inicio</p>
          </button>
        </Link>
        <Link to="/admin/category">
          <button
            className={`nav-button-admin ${
              activeButton === 'category' ? 'active-button' : ''
            }`}
            onClick={() => handleButtonClick('category')}
          >
            <img
              className="navHome"
              alt="home"
              src={activeButton === 'category' ? navCategoryBlack : navCategory}
            />
            <p>Categoría</p>
          </button>
        </Link>
        <Link to="/admin">
          <button
            className={`nav-button-admin ${
              activeButton === 'instrument' ? 'active-button' : ''
            }`}
            onClick={() => handleButtonClick('instrument')}
          >
            <img
              className="navHome"
              alt="home"
              src={
                activeButton === 'instrument'
                  ? navInstrumentBlack
                  : navInstrument
              }
            />
            <p>Instrumento</p>
          </button>
        </Link>
        <Link to={'/admin/users'}>
          <button
            className={`nav-button-admin ${
              activeButton === 'user' ? 'active-button' : ''
            }`}
            onClick={() => handleButtonClick('user')}
          >
            <img
              className="navHome"
              alt="home"
              src={activeButton === 'user' ? navUserBlack : navUser}
            />
            <p>Usuario</p>
          </button>
        </Link>
        <Link to="/admin/caracteristicas">
          <button
            className={`nav-button-admin ${
              activeButton === 'charac' ? 'active-button' : ''
            }`}
            onClick={() => handleButtonClick('charac')}
          >
            <img
              className="navHome"
              alt="home"
              src={
                activeButton === 'charac'
                  ? navCharasteristicBlack
                  : navCharasteristic
              }
            />
            <p>Característica</p>
          </button>
        </Link>
      </nav>

      <div>
        <button className="nav-button-admin" onClick={handleCerrar}>
          <img className="navHome" alt="home" src={logOut} />
          <p>Cerrar Sesión</p>
        </button>
      </div>
    </div>
  );
};

export default AdminNavBar;
