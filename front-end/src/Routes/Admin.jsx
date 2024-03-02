import React, { useState } from 'react';
import '../Components/Styles/Admin.css';
import { useRecipeStates } from '../Context/globalContext';
import AdminProduct from '../Components/AdminProduct';
import Modal from '../Components/Modal';
import FormEditInstrument from '../Components/FormsInstrument/FormEditInstrument';
import FormAddInstrument from '../Components/FormsInstrument/FormAddInstrument';
import { Navigate } from 'react-router-dom';

const Admin = () => {
  const { data } = useRecipeStates();

  const storedUser = JSON.parse(localStorage.getItem('user'));

  if (!storedUser.roles.includes('ROLE_ADMIN')) {
    alert('No tienes permisos para acceder');
    return <Navigate to={'/'} />;
  }

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedInstrument, setSelectedInstrument] = useState(null);

  const openModal = () => {
    setIsModalOpen(true);
    setIsEditMode(false);
  };

  const openEditModal = (instrument) => {
    setIsModalOpen(true);
    setIsEditMode(true);
    setSelectedInstrument(instrument);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'auto',
    });
  };

  return (
    <>
      <div className="mobile-message">
        <p>No se permite en dispositivos móviles.</p>
      </div>
      {isModalOpen && <div className="overlay" />}
      {isModalOpen && (
        <div className="modal">
          <Modal
            isOpen={isModalOpen}
            title={isEditMode ? 'Editar Instrumento' : 'Agregar Instrumento'}
            content={
              isEditMode ? (
                <FormEditInstrument
                  closeModal={closeModal}
                  instrument={selectedInstrument}
                />
              ) : (
                <FormAddInstrument closeModal={closeModal} />
              )
            }
          />
        </div>
      )}
      <div className="adminInstruments">
        <div className="header-admin">
          <div className="title-admin-instruments">
            <p>Instrumentos</p>
          </div>
          <div>
            <button className="add-instrument" onClick={openModal}>
              <span> Agregar</span>
            </button>
          </div>
        </div>
        <br />
        <div className="line-instrument-admin"></div>
        <div className="th">
          {data ? (
            data.length > 0 ? (
              data.map((instrument) => (
                <AdminProduct
                  key={instrument.id}
                  instrument={instrument}
                  openEditModal={() => openEditModal(instrument)}
                  scrollToTop={scrollToTop}
                />
              ))
            ) : (
              <p>No hay datos disponibles.</p>
            )
          ) : (
            <p>Cargando datos...</p>
          )}
        </div>
      </div>
    </>
  );
};

export default Admin;
