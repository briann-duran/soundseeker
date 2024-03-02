import React, { useCallback, useState } from 'react';
import '../FormsInstrument/FormAddInstrument.css';
import { useRecipeStates } from '../../Context/globalContext';
import axios from 'axios';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import ImageUploadForm from '../UploadImageForm.jsx';

const FormAddCategory = ({ closeModal }) => {
  const { getCategorys, politicas } = useRecipeStates();

  const [imagenes, setImagenes] = useState('');

  const [formValues, setFormValues] = useState({
    nombre: '',
    descripcion: '',
    politicas: [],
  });

  const resetForm = () => {
    setFormValues({
      nombre: '',
      imagen: '',
      descripcion: '',
      politicas: [],
    });
  };

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
    } else if (name === 'politicas') {
      const politicaId = parseInt(value);
      let updatedSelectedPoliticas;
      if (checked) {
        updatedSelectedPoliticas = [
          ...formValues.politicas,
          { id: politicaId },
        ];
      } else {
        updatedSelectedPoliticas = formValues.politicas.filter(
          (politica) => politica.id !== politicaId
        );
      }

      setFormValues({
        ...formValues,
        politicas: updatedSelectedPoliticas,
      });
    } else {
      setFormValues({
        ...formValues,
        [name]: val,
      });
    }
  };

  const policticasCheckbox = politicas
    ? politicas.map((poli) => (
        <label key={poli.id}>
          <input
            type="checkbox"
            name="politicas"
            value={poli.id}
            id={poli.id}
            checked={formValues.politicas.some((p) => p.id === poli.id)}
            onChange={handleChange}
          />
          {poli.titulo}
        </label>
      ))
    : [];

  const validateForm = () => {
    const errors = {};
    if (!formValues.nombre.trim()) errors.nombre = 'El nombre es requerido';
    setErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const finalFormValues = {
      ...formValues,
      imagen: imagenes[0],
    };
    if (validateForm()) {
      axios
        .post(
          `${import.meta.env.VITE_APP_BACK_END_URL}/categorias`,
          finalFormValues,
          {
            headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` },
          }
        )
        .then((response) => {
          Swal.fire({
            position: 'top-end',
            icon: 'success',
            title: 'Categoría guardada',
            showConfirmButton: false,
            timer: 2000,
          }).then(() => {
            resetForm();
            getCategorys();
          });
          //.then(closeModal())
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
    <form id="form-create" className="form-create" onSubmit={handleSubmit}>
      <div className="enlarge-field">
        <label htmlFor="nombre" style={{ marginBottom: 5 }}>
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
        <p
          style={{
            color: '#1A202C',
            fontSize: 16,
            fontWeight: '700',
            textAlign: 'left',
            margin: '5px 0',
          }}
        >
          Imagen
        </p>
        <div className="images-input-container">
          <ImageUploadForm updateImagenes={updateImagenes} />
        </div>
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
          Políticas
        </p>
        <div className="caracteristcias-checkbox">{policticasCheckbox}</div>
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

export default FormAddCategory;
