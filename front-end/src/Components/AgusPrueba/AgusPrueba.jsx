import React, { useState } from 'react';

const AgusPrueba = () => {
  const [showForm, setShowForm] = useState(false);
  const [imageName, setImageName] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [imageList, setImageList] = useState([]);

  const toggleForm = () => {
    setShowForm(!showForm);
  };

  const handleImageNameChange = (e) => {
    setImageName(e.target.value);
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setSelectedFile(file);
  };

  const handleAddImage = () => {
    if (imageName && selectedFile) {
      const imageUrl = URL.createObjectURL(selectedFile);
      const newImage = { name: imageName, url: imageUrl };
      setImageList([...imageList, newImage]);
      setImageName('');
      setSelectedFile(null);
      setShowForm(false);
    }
  };

  return (
    <div>
      <button onClick={toggleForm}>Añadir imagen</button>
      {showForm && (
        <div>
          <input
            type="text"
            placeholder="Nombre de la imagen"
            value={imageName}
            onChange={handleImageNameChange}
          />
          <input type="file" onChange={handleFileChange} />
          <button onClick={handleAddImage}>Aceptar</button>
        </div>
      )}
      <div>
        {imageList.map((image, index) => (
          <div key={index}>
            <img src={image.url} alt={image.name} />
            <p>{image.name}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AgusPrueba;
