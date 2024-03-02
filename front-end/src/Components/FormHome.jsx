import React, { useState } from 'react';
import './Styles/FormHome.css';
import 'sweetalert2/dist/sweetalert2.min.css';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

const FormHome = () => {
  const [searchInstrument, setSearchInstrument] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const handleStartDateChange = (date) => setStartDate(date);

  const handleEndDateChange = (date) => setEndDate(date);

  const handleInputChange = (e) => {
    const searchText = e.target.value.toLowerCase();
    setSearchInstrument(searchText);
    const mockResults = ['Opción 1', 'Opción 2', 'Otra opción', 'Algo más'];
    const filteredResults = mockResults.filter((result) =>
      result.toLowerCase().includes(searchText)
    );
    setSearchResults(filteredResults);
  };

  const handleResultClick = (result) => {
    setSearchInstrument(result);
    setSearchResults([]);
  };

  const handleSubmit = (e) => e.preventDefault();

  return (
    <form className="form-home" onSubmit={handleSubmit}>
      <div className="input-form-home-pickUp">
        <h3>RESERVA TU INSTRUMENTO AHORA</h3>
        <div className="search-date">
          <div className="centering-form-home">
            <div className="input-form-home">
              <input
                className="search-input-form-home"
                placeholder=" "
                type="text"
                value={searchInstrument}
                onChange={handleInputChange}
              />
              <div className="search-results-container-form-home">
                <ul className="ul-search-principal">
                  {searchResults.map((result, index) => (
                    <li
                      className="li-search-principal"
                      key={index}
                      onClick={() => handleResultClick(result)}
                    >
                      {result}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DemoContainer components={['DatePicker']}>
              <DatePicker
                label="Fecha retiro"
                value={startDate}
                onChange={handleStartDateChange}
              />
            </DemoContainer>
          </LocalizationProvider>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DemoContainer components={['DatePicker']}>
              <DatePicker
                label="Fecha entrega"
                value={endDate}
                onChange={handleEndDateChange}
              />
            </DemoContainer>
          </LocalizationProvider>
        </div>
        <div className="div-btn-buscar">
          <button className="btn-buscar" type="submit">
            Ver opciones
          </button>
        </div>
      </div>
    </form>
  );
};

export default FormHome;
