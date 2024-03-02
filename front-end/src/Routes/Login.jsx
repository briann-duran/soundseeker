import React, { useState } from 'react';
import '../Components/Styles/Login.css';
import { useRecipeAuth } from '../Context/authContext';

const Login = () => {
  const { login } = useRecipeAuth();

  const [formData, setFormData] = useState({
    nombreUsuario: '',
    contrasena: '',
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};
    if (!formData.nombreUsuario.trim())
      newErrors.nombreUsuario = 'El correo electrónico es obligatorio';
    if (formData.contrasena.length < 8)
      newErrors.contrasena = 'La contraseña debe tener al menos 8 caracteres';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const settings = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(formData),
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) login(settings);
  };

  return (
    <>
      <main className="content-sign">
        <form className="form-login">
          <p className="form-login-title">Iniciar sesión</p>
          <div className="form-login-title">
            <div className="input-login-container">
              <input
                className="input-login"
                type="nombreUsuario"
                placeholder="Email"
                value={formData.nombreUsuario}
                onChange={(e) =>
                  setFormData({ ...formData, nombreUsuario: e.target.value })
                }
              />
            </div>
            {errors.nombreUsuario && (
              <div className="errors">{errors.nombreUsuario}</div>
            )}
          </div>
          <div className="input-login-container">
            <input
              className="input-login"
              type="password"
              placeholder="Contraseña"
              value={formData.contrasena}
              onChange={(e) =>
                setFormData({ ...formData, contrasena: e.target.value })
              }
            />
          </div>
          {errors.contrasena && (
            <div className="errors">{errors.contrasena}</div>
          )}
          <button type="submit" className="submit-login" onClick={handleSubmit}>
            Ingresar
          </button>
          <p className="signup-link">
            ¿No tienes una cuenta? <a href="/signup">Regístrate aquí</a>
          </p>
        </form>
      </main>
    </>
  );
};

export default Login;
