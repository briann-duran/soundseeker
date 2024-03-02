import React, { useCallback, useState } from 'react';
import '../FormsInstrument/FormAddInstrument.css';
import { useRecipeStates } from '../../Context/globalContext';
import axios from 'axios';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import ImageUploadForm from '../UploadImageForm.jsx';

const FormAddInstrument = ({ closeModal }) => {
  const { cate, getInstruments, caracteristicas } = useRecipeStates();
  const [imagenes, setImagenes] = useState([]);
  const [formValues, setFormValues] = useState({
    nombre: '',
    descripcion: '',
    marca: '',
    precio: 1,
    disponible: false,
    categoria: {
      id: '1',
    },
    caracteristicas: [],
  });

  const updateImagenes = useCallback((newImagenes) => {
    setImagenes(newImagenes);
  }, []);

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value, type, checked, id } = e.target;
    const val = type === 'checkbox' ? checked : value;
    if (name === 'imagenes') {
      const imageUrls = val.split(',');
      setFormValues({
        ...formValues,
        [name]: imageUrls,
      });
    } else if (name === 'caracteristicas') {
      const caracteristicaId = parseInt(id);
      const updatedSelectedCaracteristicas = checked
        ? [...formValues.caracteristicas, { id: caracteristicaId }]
        : formValues.caracteristicas.filter(
            (caracteristica) => caracteristica.id !== caracteristicaId
          );
      setFormValues({
        ...formValues,
        caracteristicas: updatedSelectedCaracteristicas,
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

  const caracteristicasCheckbox = caracteristicas
    ? caracteristicas.map((caract) => (
        <label key={caract.id}>
          <input
            type="checkbox"
            name="caracteristicas"
            value={caract.id}
            id={caract.id}
            checked={formValues.caracteristicas.some((c) => c.id === caract.id)}
            onChange={handleChange}
          />
          {caract.nombre}
        </label>
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
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const finalFormValues = {
      ...formValues,
      imagenes: imagenes,
    };

    if (validateForm()) {
      axios
        .post(
          `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos`,
          finalFormValues,
          {
            headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` },
          }
        )
        .then((response) => {
          Swal.fire({
            position: 'top-end',
            icon: 'success',
            title: 'Instrumento guardado',
            showConfirmButton: false,
            timer: 2000,
          }).then(() => getInstruments());
        })
        .catch((e) => {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: e.response.data.message,
          }).then(Promise.resolve);
        });
    }
  };

  return (
    <form id="form-create" className="form-create" onSubmit={handleSubmit}>
      <div className="enlarge-field">
        <label htmlFor="nombre">
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
      <div className="enlarge-field">
        <label htmlFor="categoria" style={{ marginBottom: 5 }}>
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
            style={{ marginBottom: 0, marginRight: 10 }}
          >
            {options}
          </select>
          <button className="btn-add-category">Agregar</button>
        </div>
      </div>
      <div className="enlarge-field">
        <p
          style={{
            color: '#1A202C',
            fontSize: 16,
            fontWeight: '700',
            textAlign: 'left',
            margin: '5px 0',
          }}
        >
          Imágenes
        </p>
        <div className="images-input-container">
          <ImageUploadForm updateImagenes={updateImagenes} />
        </div>
      </div>
      <div className="enlarge-field">
        <label htmlFor="disponible">
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
      <div className="enlarge-field">
        <label htmlFor="precio">
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
      <div className="enlarge-field">
        <label htmlFor="marca">
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
      <div className="enlarge-field">
        <label htmlFor="descripcion">
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
      <div className="div-caracteristcias">
        <p
          style={{
            color: '#1A202C',
            fontSize: 16,
            fontWeight: '700',
            margin: '0 0 5px',
          }}
        >
          Características
        </p>
        <div className="caracteristcias-checkbox">
          {caracteristicasCheckbox}
        </div>
      </div>
      <div className="group-btn-submit-instrument">
        <button className="btn-submit-instrument" type="submit">
          Agregar
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
  );
};

export default FormAddInstrument;
