import React, { useEffect, useState } from 'react';
import UploadImagePreview from './UploadImagePreview.jsx';
import './Styles/UploadImageForm.css';

const UploadImageForm = ({ updateImagenes }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState([]);
  const [data, setData] = useState(null);
  const [fetchComplete, setFetchComplete] = useState(false);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setSelectedFile(file);
  };

  useEffect(() => {
    const uploadImage = async (file) => {
      const formData = new FormData();
      formData.append('file', file);
      const response = await fetch(
        `${import.meta.env.VITE_APP_IMAGE_URL}/uploadImg`,
        {
          method: 'POST',
          body: formData,
        }
      );
      const data = await response.text();
      setData(data);
      setFetchComplete(true);
    };

    if (selectedFile) {
      uploadImage(selectedFile).then();
    }
  }, [selectedFile]);

  useEffect(() => {
    if (fetchComplete) {
      setPreview((prevState) => {
        return [...prevState, data];
      });
      setFetchComplete(false);
    }
  }, [fetchComplete, data]);

  useEffect(() => {
    if (preview.length > 0) {
      updateImagenes(preview);
    }
  }, [preview, updateImagenes]);

  return (
    <>
      <label htmlFor="image" className="drop-container">
        <span className="drop-title">Arrastra imágenes aquí</span>
        <span style={{ fontWeight: 'normal' }}>o</span>
        <input
          type="file"
          id="image"
          accept=".jpg,.jpeg,.png,.svg"
          onChange={handleFileChange}
          aria-label="Seleccionar imágenes"
          placeholder="Selecciona una imagen"
          className="image-upload-input"
        />
      </label>
      {preview.length > 0 && (
        <>
          <span className="drop-title">Imágenes cargadas</span>
          <div className="uploaded-images-preview">
            {preview.map((img, index) => (
              <UploadImagePreview image={img} key={index} />
            ))}
          </div>
        </>
      )}
    </>
  );
};

export default UploadImageForm;
