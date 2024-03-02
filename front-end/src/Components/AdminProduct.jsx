import React from 'react';
import './Styles/AdminProduct.css';
import imagenPen from '../Imgs/pencil.svg';
import imagenTrash from '../Imgs/trash.png';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import { useRecipeStates } from '../Context/globalContext';

const AdminProduct = ({ instrument, openEditModal, scrollToTop }) => {
  const { getInstruments } = useRecipeStates();

  const handleButtonClickEdit = () => {
    openEditModal();
    scrollToTop();
  };

  const handleButtonClickDelete = () => {
    const instrumentId = instrument.id;
    Swal.fire({
      title: '¿Estas seguro de querer eliminar?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '¡Sí!',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) deleteInstrument(instrumentId);
    });

    function deleteInstrument(instrumentId) {
      fetch(
        `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/${instrumentId}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
          },
        }
      )
        .then((response) => {
          if (response.ok) {
            Swal.fire('Instrumento eliminado con éxito').then((r) =>
              Promise.resolve()
            );
            getInstruments();
          } else {
            Swal.fire(
              'Error',
              'Error al eliminar el instrumento',
              'error'
            ).then((r) => Promise.resolve());
            throw new Error('Error al eliminar el instrumento');
          }
        })
        .catch(() => {
          Swal.fire(
            'Error',
            'No se ha podido eliminar el instrumento porque está reservado por un usuario.',
            'error'
          ).then((r) => Promise.resolve());
        });
    }
  };

  return (
    <div className="div-2">
      <img
        className="profile"
        alt="Profile"
        src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrument.imagenes[0]}`}
      />
      <div className="text-wrapper-7">{instrument.id}</div>
      <div className="text-wrapper-8">{instrument.nombre}</div>
      <div className="text-wrapper-9">{instrument.categoria.id}</div>
      <div className="text-wrapper-10">{instrument.marca}</div>
      <div className="text-wrapper-11">{instrument.descripcion}</div>
      <button className="pen-button" onClick={handleButtonClickEdit}>
        <img className="pen" alt="Pen" src={imagenPen} />
      </button>
      <button className="trash-button" onClick={handleButtonClickDelete}>
        <img className="trash" alt="trash" src={imagenTrash} />
      </button>
    </div>
  );
};

export default AdminProduct;
