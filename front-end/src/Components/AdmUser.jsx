import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../Components/Styles/AdmUser.css';
import {
  faEnvelope,
  faUser,
  faUserGear,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const AdmUser = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios
      .get(`${import.meta.env.VITE_APP_BACK_END_URL}/clientes`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwt')}`,
        },
      })
      .then((response) => setUsers(response.data))
      .catch((error) => Promise.reject(error));
  }, []);

  const handleSetAdmin = (user) => {
    axios
      .post(`${import.meta.env.VITE_APP_BACK_END_URL}/clientes`, user, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwt')}`,
        },
      })
      .then(() =>
        axios.get(`${import.meta.env.VITE_APP_BACK_END_URL}/clientes`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
          },
        })
      )
      .then((response) => setUsers(response.data))
      .catch((error) => Promise.reject(error));
  };

  const roleMapping = {
    ROLE_ADMIN: {
      icon: faUserGear,
      text: 'Admin',
    },
    ROLE_CLIENTE: {
      icon: faUser,
      text: 'Cliente',
    },
  };

  return (
    <div className="user-container">
      <h1>Usuarios </h1>
      {users.map((user) => {
        const role = roleMapping[user.roles[0]];
        return (
          <section className="horizontal" key={user.nombreUsuario}>
            <div className="vertical">
              <span className="enlarge">
                <FontAwesomeIcon icon={role.icon} />
              </span>
              <span className="shrink">{role.text}</span>
            </div>
            <div className="user-details">
              <p>
                <span className="bold">
                  {user.nombre} {user.apellido}
                </span>{' '}
                (@<span className="italic">{user.nombreUsuario}</span>)
              </p>
              <p>
                <FontAwesomeIcon icon={faEnvelope} /> {user.correoElectronico}
              </p>
            </div>
            <div>
              <button
                className="btn-cambio-rol"
                onClick={() => handleSetAdmin(user)}
              >
                Hacer {role.text === 'Admin' ? 'Cliente' : 'Admin'}
              </button>
            </div>
          </section>
        );
      })}
    </div>
  );
};

export default AdmUser;
