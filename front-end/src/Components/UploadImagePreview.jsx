import React from 'react';
import './Styles/UploadImagePreview.css';

const UploadImagePreview = ({ image }) => {
  return (
    <div className="image-upload-preview">
      <img
        src={`${import.meta.env.VITE_APP_BACK_END_URL}${image}`}
        alt="Vista previa de imagen cargada"
      />
    </div>
  );
};

UploadImagePreview.propTypes = {};

export default UploadImagePreview;
