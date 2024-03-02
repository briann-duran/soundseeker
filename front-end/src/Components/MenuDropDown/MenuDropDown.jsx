import React from 'react';
import '../MenuDropDown/MenuDropDown.css';
import { FaHeart, FaUserCircle } from 'react-icons/fa';
import { RxExit } from 'react-icons/rx';
import { PiUserListBold } from 'react-icons/pi';
import { BsBookmarkCheckFill } from 'react-icons/bs';
import { Link } from 'react-router-dom';
import { useRecipeAuth } from '../../Context/authContext';
import { FaGear } from 'react-icons/fa6';

const MenuDropDown = ({ isAdmin }) => {
  const { logout } = useRecipeAuth();

  return (
    <div className="dropdown">
      <FaUserCircle className="FaUserCircle" />
      <div className="menu">
        {isAdmin && (
          <Link to={'/admin'}>
            {' '}
            <FaGear /> Panel admin
          </Link>
        )}
        <Link className="item" to={'/favoritos'}>
          <FaHeart /> Mis favoritos
        </Link>
        <Link to={'/alquileres'}>
          <BsBookmarkCheckFill /> Mis alquileres
        </Link>
        <Link to={'/usuario'}>
          <PiUserListBold /> Mis datos
        </Link>
        <Link to={'/'} onClick={() => logout()}>
          <RxExit /> Cerrar sesión
        </Link>
      </div>
    </div>
  );
};

export default MenuDropDown;
