import React, { useCallback, useEffect, useState } from 'react';
import axios from 'axios';
import './AdmCaracteristicas.css';
import ImageUploadForm from '../UploadImageForm.jsx';
import Swal from 'sweetalert2';

const AdmCaracteristicas = () => {
  const [imagenes, setImagenes] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [caracteristicas, setCaracteristicas] = useState([]);
  const [nombre, setNombre] = useState('');
  const [url, setUrl] = useState('');
  const [editingCaracteristicaId, setEditingCaracteristicaId] = useState(null);
  const [isNewCaracteristica, setIsNewCaracteristica] = useState(false);

  const updateImagenes = useCallback((newImagenes) => {
    setImagenes(newImagenes);
  }, []);

  const getCaracteristicas = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_UR}/caracteristicas`
      );
      setCaracteristicas(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    getCaracteristicas().then((r) => Promise.resolve());
  }, []);

  const handleDeleteCaracteristica = async (caracteristicaId) => {
    try {
      await axios.delete(
        `${import.meta.env.VITE_APP_BACK_END_URL}/caracteristicas/${caracteristicaId}`,
        { headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` } }
      );
      await getCaracteristicas().then((value) =>
        Swal.fire('Característica eliminada con éxito', '', 'success')
      );
    } catch (error) {
      await Swal.fire(
        'Error',
        'No se ha podido eliminar la característica porque contiene instrumentos reservados por usuarios.',
        'error'
      );
    }
  };

  const handleModificarCaracteristica = async (caracteristicaId) => {
    const caracteristicaSeleccionada = caracteristicas.find(
      (caracteristica) => caracteristica.id === caracteristicaId
    );
    if (caracteristicaSeleccionada) {
      setNombre(caracteristicaSeleccionada.nombre);
      setImagenes([caracteristicaSeleccionada.icono]);
      setEditingCaracteristicaId(caracteristicaId);
      setShowForm(true);
      setIsNewCaracteristica(false);
    }
  };

  const handleModificarCaracteristicaSubmit = async (e) => {
    e.preventDefault();
    if (!editingCaracteristicaId) return;
    const icono = imagenes.length > 0 ? imagenes[0] : imagenes;
    const caracteristicaModificada = {
      nombre,
      icono,
    };
    try {
      await axios.put(
        `${import.meta.env.VITE_APP_BACK_END_URL}/caracteristicas/${editingCaracteristicaId}`,
        caracteristicaModificada,
        { headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` } }
      );
      setCaracteristicas((prevCaracteristicas) =>
        prevCaracteristicas.map((c) =>
          c.id === editingCaracteristicaId
            ? { ...c, ...caracteristicaModificada }
            : c
        )
      );
      setNombre('');
      setUrl('');
      setEditingCaracteristicaId(null);
      setShowForm(false);
    } catch (error) {}
  };

  const handleAddCaracteristica = () => {
    setNombre('');
    setUrl('');
    setEditingCaracteristicaId(null);
    setIsNewCaracteristica(true);
    setShowForm(true);
  };
  const handleAnadirCaracteristicaSubmit = async () => {
    const nuevaCaracteristica = {
      nombre,
      icono: imagenes[0],
    };
    try {
      const response = await axios.post(
        `${import.meta.env.VITE_APP_BACK_END_URL}/caracteristicas`,
        nuevaCaracteristica,
        { headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` } }
      );
      setCaracteristicas((prevCaracteristicas) => [
        ...prevCaracteristicas,
        response.data,
      ]);
      setNombre('');
      setUrl('');
      setShowForm(false);
    } catch (error) {}
  };

  return (
    <div className="main-container">
      <div className="titulo">
        <h2>Características</h2>
        <button
          className="añadir-caracteristica"
          onClick={handleAddCaracteristica}
        >
          Agregar característica
        </button>
      </div>
      <hr />
      <div className="caracteristicas-container">
        {caracteristicas.map((caracteristica, index) => {
          if (!caracteristica) {
            console.error(
              `Caracteristica at index ${index} is undefined or null`
            );
            return null;
          }
          return (
            <div key={index} className="caracteristica">
              <img
                src={`${import.meta.env.VITE_APP_IMAGE_URL}${caracteristica.icono}`}
                alt={caracteristica.nombre}
              />
              <p>{caracteristica.nombre}</p>
              <button
                onClick={() => handleDeleteCaracteristica(caracteristica.id)}
              >
                Eliminar
              </button>
              <button
                onClick={() => handleModificarCaracteristica(caracteristica.id)}
              >
                Modificar
              </button>
            </div>
          );
        })}
      </div>
      {showForm && (
        <div className="modal-caracteristica">
          <div className="modal-content">
            <h3>
              {isNewCaracteristica ? 'Agregar' : 'Modificar'} Característica
            </h3>
            <p className="lbl-nombre-caracteristica">Nombre: </p>
            <input
              type="text"
              id="nombre"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />
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
            <div className="btns-caracteristicas">
              <button
                onClick={
                  isNewCaracteristica
                    ? handleAnadirCaracteristicaSubmit
                    : handleModificarCaracteristicaSubmit
                }
              >
                {isNewCaracteristica ? 'Agregar' : 'Modificar'}
              </button>
              <button onClick={() => setShowForm(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdmCaracteristicas;
