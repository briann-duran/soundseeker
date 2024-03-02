import React from 'react';
import { FaUserCircle } from 'react-icons/fa';
import { IoIosCheckmarkCircle } from 'react-icons/io';
import '../Components/Styles/CardUserData.css';

const CardUserData = ({ storedUser }) => {
  return (
    <div className="container-cardUserData">
      <div className="cardUserData">
        <a href="">
          <FaUserCircle className="dataUserCircle" />
        </a>
        <IoIosCheckmarkCircle className="checkCircle" />
      </div>
      <div className="userDataInfo">
        <h2>
          {storedUser.nombre} {storedUser.apellido}{' '}
        </h2>
        <p>{storedUser.nombreUsuario}</p>
        <p>{storedUser.correoElectronico}</p>
      </div>
    </div>
  );
};

export default CardUserData;
