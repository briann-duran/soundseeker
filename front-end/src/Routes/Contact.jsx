import React, { useState } from 'react';
import '../Components/Styles/Contact.css';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import imgContact from '../Imgs/undraw_compose_music_re_wpiw.svg';

const Contact = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    comment: '',
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.name) newErrors.name = 'El nombre es requerido.';
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    if (!formData.email.match(emailRegex))
      newErrors.email = 'Introduzca un correo electrónico válido.';
    if (!formData.comment)
      newErrors.comment = 'Los comentarios son requeridos.';
    return newErrors;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const newErrors = validateForm();
    if (Object.keys(newErrors).length === 0) {
      Swal.fire({
        position: 'top-end',
        icon: 'success',
        title: 'Formulario enviado correctamente',
        showConfirmButton: false,
        timer: 1500,
      }).then();
      document.querySelector('.form2-contact').reset();
    } else {
      setErrors(newErrors);
    }
  };

  return (
    <div className="page-contact">
      <div className="form-contact">
        <div className="card-contact">
          <span className="title">
            ¡Contáctanos para reservar tu instrumento deal!
          </span>
          <form className="form2-contact">
            {errors.name && (
              <div className="error-form-contact">{errors.name}</div>
            )}
            <div className="group">
              <input
                placeholder=""
                type="text"
                id="name"
                name="name"
                required=""
                onChange={handleChange}
              />
              <label htmlFor="name">Nombre</label>
            </div>
            {errors.email && (
              <div className="error-form-contact">{errors.email}</div>
            )}
            <div className="group">
              <input
                placeholder=""
                type="email"
                id="email"
                name="email"
                onChange={handleChange}
                required=""
              />
              <label htmlFor="email">Correo</label>
            </div>
            {errors.comment && (
              <div className="error-form-contact">{errors.comment}</div>
            )}
            <div className="group">
              <textarea
                placeholder=""
                id="comment"
                name="comment"
                rows="5"
                required=""
                onChange={handleChange}
              ></textarea>
              <label htmlFor="comment">Comentarios</label>
            </div>
            <button type="submit" onClick={handleSubmit}>
              Enviar
            </button>
          </form>
        </div>
        <div></div>
        <img src={imgContact} alt="Persona tocando la guitarra" />
      </div>
    </div>
  );
};

export default Contact;
