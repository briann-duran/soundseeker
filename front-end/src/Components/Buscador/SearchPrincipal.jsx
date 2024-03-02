import React, { useEffect, useState } from 'react';
import '../Buscador/SearchPrincipal.css';
import { Link } from 'react-router-dom';

const SearchPrincipal = ({ closeModal }) => {
  const [searchInstrument, setSearchInstrument] = useState('');
  const [searchResults, setSearchResults] = useState([]);

  const url =
    `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/busqueda?` +
    new URLSearchParams({ nombre: searchInstrument }).toString();

  const searchApi = () => {
    fetch(url)
      .then((response) => {
        if (response.ok) return response.json();
        throw new Error('Error en la solicitud');
      })
      .then((data) => {
        setSearchResults(data);
      })
      .catch();
  };

  useEffect(() => {
    searchInstrument.trim() !== '' ? searchApi() : setSearchResults([]);
  }, [searchInstrument]);

  const handleInputChange = (e) => {
    const searchText = e.target.value.toLowerCase();
    setSearchInstrument(searchText);
  };

  const cleanResult = () => {
    setSearchResults([]);
    closeModal();
  };

  return (
    <>
      <div className="modal-search-principal">
        <div className="modal-content-search-principal">
          <div className="div-input-search-principal">
            <input
              className="input-search-principal"
              type="text"
              placeholder="Ingrese el nombre del instrumento"
              value={searchInstrument}
              onChange={handleInputChange}
            />
            <span className="underline-search-principal"></span>
          </div>
          <span className="close-modal-search-principal" onClick={closeModal}>
            &times;
          </span>
          <div className="search-results-container">
            <ul className="ul-search-principal">
              {searchResults.map((result) => (
                <Link
                  className="link-card"
                  to={'/details/' + result.id}
                  onClick={cleanResult}
                  key={result.id}
                >
                  <li className="li-search-principal">
                    <img
                      src={`${import.meta.env.VITE_APP_IMAGE_URL}${result.imagenes[0]}`}
                      alt={result.nombre}
                    />
                    <p>
                      <span>{result.nombre}</span>
                      <span>Marca: {result.marca}</span>
                    </p>
                  </li>
                </Link>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </>
  );
};

export default SearchPrincipal;
