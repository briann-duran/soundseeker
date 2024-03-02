import React, { useState } from 'react';
import './Styles/Modal.css';
import { useRecipeStates } from '../Context/globalContext';
import axios from 'axios';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

const Modal = ({ closeModal }) => {
  const { cate } = useRecipeStates();

  const [formValues, setFormValues] = useState({
    nombre: '',
    descripcion: '',
    marca: '',
    precio: 1,
    imagenes: [],
    disponible: false,
    categoria: {
      id: '1',
    },
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const val = type === 'checkbox' ? checked : value;
    if (name === 'imagenes') {
      const imageUrls = val.split(',');
      setFormValues({
        ...formValues,
        [name]: imageUrls,
      });
    } else {
      setFormValues({
        ...formValues,
        [name]: val,
      });
    }
  };

  const options = cate
    ? cate.map((category) => (
        <option key={category.id} value={category.id}>
          {category.nombre}
        </option>
      ))
    : [];

  const handleInputChange = (e) => {
    const value = e.target.value;
    setFormValues({
      ...formValues,
      categoria: {
        id: parseInt(value),
      },
    });
  };

  const validateForm = () => {
    const errors = {};
    if (!formValues.nombre.trim()) errors.nombre = 'El nombre es requerido';
    if (
      !formValues.precio ||
      isNaN(formValues.precio) ||
      formValues.precio < 1
    ) {
      errors.precio = 'Debe ser un número mayor a 0';
    }
    setErrors(errors);
    return Object.keys(errors).length === 0; // Devuelve true si no hay errores
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      axios
        .post(
          `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos`,
          formValues
        )
        .then((response) => {
          Swal.fire({
            position: 'top-end',
            icon: 'success',
            title: 'Instrumento guardado',
            showConfirmButton: false,
            timer: 2000,
          }).then((r) => Promise.resolve());
        })
        .catch((e) => {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: e.response.data.message,
          }).then((r) => Promise.resolve());
        });
    }
  };

  return (
    <div className="page-add-instrument">
      <div className="modal-add-instrument">
        <h2>Agregar Nuevo Instrumento</h2>
        <form id="form-create" className="form-create" onSubmit={handleSubmit}>
          <div>
            <div>
              <label
                htmlFor="nombre"
                style={{
                  color: '#1A202C',
                  fontSize: 16,
                  fontWeight: '700',
                }}
              >
                Nombre
                <input
                  className="input-form-product"
                  id="nombre"
                  type="text"
                  name="nombre"
                  value={formValues.nombre}
                  onChange={handleChange}
                />
                {errors.nombre && <div className="error">{errors.nombre}</div>}
              </label>
            </div>
            <div>
              <label
                htmlFor="categoria"
                style={{
                  color: '#1A202C',
                  fontSize: 16,
                  fontWeight: '700',
                }}
              >
                Categoría{' '}
              </label>
              <div className="div-form-instrument-category">
                <select
                  id="categoria"
                  className="select-search"
                  name="categoria"
                  placeholder="Seleccione la categoria"
                  onChange={handleInputChange}
                  value={options.category}
                >
                  {options}
                </select>
                <button className="btn-add-category">Agregar</button>
              </div>
            </div>
            <div>
              <label
                htmlFor="categoria"
                style={{
                  color: '#1A202C',
                  fontSize: 16,
                  fontWeight: '700',
                }}
              >
                Imagen (URLs, separado por comas):
                <input
                  className="input-form-product"
                  id="imagenes"
                  type="text"
                  name="imagenes"
                  value={formValues.imagenes}
                  onChange={handleChange}
                />
              </label>
            </div>
            <label
              htmlFor="disponible"
              style={{
                color: '#1A202C',
                fontSize: 16,
                fontWeight: '700',
              }}
            >
              Disponible
              <input
                id="disponible"
                type="checkbox"
                name="disponible"
                checked={formValues.disponible}
                onChange={handleChange}
              />
            </label>
          </div>
          <div>
            <label
              htmlFor="precio"
              style={{
                color: '#1A202C',
                fontSize: 16,
                fontWeight: '700',
              }}
            >
              Precio
              <input
                className="input-form-product"
                id="precio"
                type="number"
                name="precio"
                value={formValues.precio}
                onChange={handleChange}
                min="1"
              />
              {errors.precio && <div className="error">{errors.precio}</div>}
            </label>
          </div>
          <div>
            <label
              htmlFor="marca"
              style={{
                color: '#1A202C',
                fontSize: 16,
                fontWeight: '700',
              }}
            >
              Marca
              <input
                className="input-form-product"
                id="marca"
                type="text"
                name="marca"
                value={formValues.marca}
                onChange={handleChange}
              />
            </label>
          </div>
          <div>
            <label
              htmlFor="descripcion"
              style={{
                color: '#1A202C',
                fontSize: 16,
                fontWeight: '700',
              }}
            >
              Descripción
              <textarea
                className="textarea-form-addinstrument"
                id="descripcion"
                name="descripcion"
                value={formValues.descripcion}
                onChange={handleChange}
              />
            </label>
          </div>
          <div className="group-btn-submit-instrument">
            <button className="btn-submit-instrument" type="submit">
              Modificar
            </button>
            <button
              className="btn-cancel-instrument"
              type="button"
              onClick={closeModal}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Modal;
