import React, { useEffect, useState } from 'react';
import '../Components/Styles/Signup.css';
import { useRecipeAuth } from '../Context/authContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast, Toaster } from 'sonner';
import {
  faCircleCheck,
  faCircleXmark,
} from '@fortawesome/free-solid-svg-icons';

const Signup = () => {
  const { signUp, isLoading, setIsLoading } = useRecipeAuth();

  const [formData, setFormData] = useState({
    nombreUsuario: '',
    contrasena: '',
    contrasenaConfirmada: '',
    nombre: '',
    apellido: '',
    correoElectronico: '',
  });

  const [errors, setErrors] = useState({});
  const [showErrors, setShowErrors] = useState(false);

  const limpiarErrores = () => {
    setErrors({});
    setShowErrors(false);
  };

  const erroresToast = (keysArray) => {
    keysArray.forEach((key, index) => {
      setTimeout(
        () => {
          toast.error(errors[key]);
          if (index === keysArray.length - 1) limpiarErrores();
        },
        (index + 1) * 1000
      );
    });
  };

  useEffect(() => {
    if (showErrors) {
      const keysArray = Object.keys(errors);
      erroresToast(keysArray);
    }
  }, [showErrors, errors]);

  const keysArray = Object.keys(errors);
  erroresToast(keysArray, 0, limpiarErrores);

  const [passValidation, setPassValidation] = useState({
    hasCapital: false,
    hasLower: false,
    hasNumber: false,
    hasSpecial: false,
    hasLength: false,
  });

  const handlePasswordValidationOnChange = (e) => {
    const value = e.target.value;
    let newPassValidation = { ...passValidation };

    const regexChecks = {
      hasCapital: /[A-Z]/g,
      hasLower: /[a-z]/g,
      hasNumber: /[0-9]/g,
      hasSpecial: /[^a-zA-Z\d]/g,
      hasLength: /.{8,}/g,
    };

    for (let key in regexChecks) {
      newPassValidation[key] = regexChecks[key].test(value);
    }

    setPassValidation(newPassValidation);
    setFormData({ ...formData, contrasena: value });
  };

  const passwordRequirements = [
    { requirement: 'hasCapital', displayText: ' ABC' },
    { requirement: 'hasLower', displayText: ' abc' },
    { requirement: 'hasNumber', displayText: ' 123' },
    { requirement: 'hasSpecial', displayText: ' @#$' },
    { requirement: 'hasLength', displayText: ' 8~30' },
  ];

  const validateForm = () => {
    const newErrors = {};

    if (!formData.nombre.trim()) {
      newErrors.nombre = 'El nombre es obligatorio';
    } else if (formData.nombre.length < 2 || formData.nombre.length > 30) {
      newErrors.nombre =
        'El nombre debe tener minimo 2 caracteres y maximo 30.';
    }

    if (!formData.apellido.trim()) {
      newErrors.apellido = 'El apellido es obligatorio';
    } else if (formData.apellido.length < 2 || formData.apellido.length > 30) {
      newErrors.nombre =
        'El apellido debe tener minimo 2 caracteres y maximo 30.';
    }

    if (!formData.nombreUsuario.trim()) {
      newErrors.nombreUsuario = 'El nombre de usuario es obligatorio';
    } else if (
      formData.nombreUsuario.length < 4 ||
      formData.nombreUsuario.length > 20
    ) {
      newErrors.nombreUsuario =
        'El apellido debe tener minimo 2 caracteres y maximo 30.';
    } else if (
      !/^(?!.*([\\W_])\\1)[A-Za-z0-9_.-]{4,20}$/.test(formData.nombreUsuario)
    ) {
      newErrors.nombreUsuario =
        'El nombre de usuario solo puede contener letras sin acentos, números,-, _ y .; ningún caracter especial puede estar seguido de otro.';
    }

    if (!formData.correoElectronico.trim()) {
      newErrors.correoElectronico = 'El correo electrónico es obligatorio';
    } else if (
      !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(
        formData.correoElectronico
      )
    ) {
      newErrors.correoElectronico = 'El correo electrónico no es válido';
    }

    if (formData.contrasena.length < 8) {
      newErrors.contrasena = 'La contraseña debe tener al menos 8 caracteres';
    } else if (
      !/^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*\W).{8,}$/.test(formData.contrasena)
    ) {
      newErrors.contrasena =
        'La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial.';
    }

    if (formData.contrasena !== formData.contrasenaConfirmada) {
      newErrors.contrasenaConfirmada = 'Las contraseñas no coinciden';
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length !== 0) {
      erroresToast();
    }

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
    if (validateForm()) signUp(settings);
  };

  return (
    <>
      <Toaster position="top-right" richColors expand={true} />
      <main className="content-sign">
        {isLoading ? (
          <div className="loading-sign">
            <div id="page-loading">
              <div id="container-loading">
                <div id="ring-loading"></div>
                <div id="ring-loading"></div>
                <div id="ring-loading"></div>
                <div id="ring-loading"></div>
                <div id="h3-loading">Cargando</div>
              </div>
            </div>
            <p>
              No recibí ningún mail, quiero{' '}
              <a onClick={() => setIsLoading(false)}>registrarme</a> de nuevo.
            </p>
          </div>
        ) : (
          <form className="form-signup">
            <p className="title-signup">Crear cuenta </p>
            <p className="message-signup">
              Registrate para poder acceder a todos las opciones.{' '}
            </p>

            <input
              className="input-signup"
              type="text"
              placeholder="Nombre"
              required=""
              value={formData.nombre}
              onChange={(e) =>
                setFormData({ ...formData, nombre: e.target.value })
              }
            />
            <input
              className="input-signup"
              type="text"
              placeholder="Apellido"
              required=""
              value={formData.apellido}
              onChange={(e) =>
                setFormData({ ...formData, apellido: e.target.value })
              }
            />
            <input
              className="input-signup"
              type="text"
              placeholder="Nombre de usuario"
              required=""
              autoComplete="username"
              value={formData.nombreUsuario}
              onChange={(e) =>
                setFormData({ ...formData, nombreUsuario: e.target.value })
              }
            />
            <input
              className="input-signup"
              type="email"
              placeholder="Email"
              required=""
              autoComplete="username"
              value={formData.correoElectronico}
              onChange={(e) =>
                setFormData({ ...formData, correoElectronico: e.target.value })
              }
            />
            <input
              className="input-signup"
              type="password"
              placeholder="Contraseña"
              required=""
              autoComplete="new-password"
              value={formData.contrasena}
              onChange={handlePasswordValidationOnChange}
            />
            <p className="password-validation">
              {passwordRequirements.map(({ requirement, displayText }) => (
                <span key={requirement}>
                  <FontAwesomeIcon
                    icon={
                      passValidation[requirement]
                        ? faCircleCheck
                        : faCircleXmark
                    }
                    className={passValidation[requirement] ? 'blue' : 'grey'}
                  />
                  {displayText}
                </span>
              ))}
            </p>
            <input
              className="input-signup"
              type="password"
              placeholder="Confirmar contraseña"
              required=""
              autoComplete="new-password"
              value={formData.contrasenaConfirmada}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  contrasenaConfirmada: e.target.value,
                })
              }
            />
            <button className="submit-signup" onClick={handleSubmit}>
              Registrarse
            </button>
            <p className="signin">
              ¿Ya tiene una cuenta? <a href="/login">Ingresar</a>
            </p>
          </form>
        )}
      </main>
    </>
  );
};

export default Signup;
