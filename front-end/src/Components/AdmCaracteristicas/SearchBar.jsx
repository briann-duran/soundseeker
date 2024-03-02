import React from 'react';
import './AdmCaracteristicas.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';

const SearchBar = () => {
  return (
    <div>
      <div className="search-bar">
        <div className="search">
          <input
            id="quick_search"
            className="xs-hide"
            name="quick_search"
            placeholder="Buscar..."
            type="text"
          />
          <div className="icon">
            <button>
              <FontAwesomeIcon icon={faMagnifyingGlass} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SearchBar;
