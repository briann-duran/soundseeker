import React from 'react';
import './Styles/AdminProduct.css';
import imagenPen from '../Imgs/pencil.svg';
import imagenTrash from '../Imgs/trash.png';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import { useRecipeStates } from '../Context/globalContext';

const AdminCategory = ({ catego, openEditModal }) => {
  const { getCategorys } = useRecipeStates();
  const handleButtonClickEdit = () => openEditModal();

  const handleButtonClickDelete = () => {
    const categoryId = catego.id;
    Swal.fire({
      title: '¿Estas seguro de querer eliminar?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '¡Sí!',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) deleteInstrument(categoryId);
    });

    function deleteInstrument(categoryId) {
      fetch(
        `${import.meta.env.VITE_APP_BACK_END_URL}/categorias/${categoryId}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
          },
        }
      )
        .then((response) => {
          if (response.ok) {
            Swal.fire('Categoría eliminada con éxito').then((r) =>
              Promise.resolve()
            );
            getCategorys();
          } else {
            Swal.fire('Error', 'Error al eliminar la categoría', 'error').then(
              (r) => Promise.resolve()
            );
            throw new Error('Error al eliminar la categoría');
          }
        })
        .catch((error) => {
          Swal.fire(
            'Error',
            'No se ha podido eliminar la categoría porque contiene instrumentos reservados por usuarios.',
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
        src={`${import.meta.env.VITE_APP_IMAGE_URL}${catego.imagen}`}
      />
      <div className="text-wrapper-7">{catego.id}</div>
      <div className="text-wrapper-8">{catego.nombre} </div>
      <div className="text-wrapper-11">{catego.descripcion}</div>
      <button className="pen-button" onClick={handleButtonClickEdit}>
        <img className="pen" alt="Pen" src={imagenPen} />
      </button>
      <button className="trash-button" onClick={handleButtonClickDelete}>
        <img className="trash" alt="trash" src={imagenTrash} />
      </button>
    </div>
  );
};

export default AdminCategory;
