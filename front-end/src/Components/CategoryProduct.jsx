import React, { useState } from 'react';
import '../Components/FormsCategory/FormAddCategory';
import { useRecipeStates } from '../Context/globalContext';
import Modal from '../Components/Modal';
import FormAddCategory from './FormsCategory/FormAddCategory';
import FormEditCategory from './FormsCategory/FormEditCategory';
import AdminCategory from './AdminCategory';

const CategoryProduct = () => {
  const { cate } = useRecipeStates();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);

  const openModal = () => {
    setIsModalOpen(true);
    setIsEditMode(false);
  };

  const openEditModal = (catego) => {
    setIsModalOpen(true);
    setIsEditMode(true);
    setSelectedCategory(catego);
  };

  const closeModal = () => setIsModalOpen(false);

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
            title={isEditMode ? 'Editar Categoría' : 'Agregar Categoría'}
            content={
              isEditMode ? (
                <FormEditCategory
                  closeModal={closeModal}
                  catego={selectedCategory}
                />
              ) : (
                <FormAddCategory closeModal={closeModal} />
              )
            }
          />
        </div>
      )}
      <div className="adminInstruments">
        <div className="header-admin">
          <div className="title-admin-instruments">
            <p>Categorías</p>
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
          {cate ? (
            cate.length > 0 ? (
              cate.map((catego) => (
                <AdminCategory
                  key={catego.id}
                  catego={catego}
                  openEditModal={() => openEditModal(catego)}
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

export default CategoryProduct;
