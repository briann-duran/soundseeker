import React, { useEffect, useState } from 'react';
import './Styles/FormHome.css';
import { useRecipeStates } from '../Context/globalContext';
import 'sweetalert2/dist/sweetalert2.min.css';
import './Styles/FormHome2.css';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import 'dayjs/locale/es';
import { toast } from 'sonner';

const FormHome2 = ({ setInstruments }) => {
  const { startDate, endDate, setEndDate, setStartDate } = useRecipeStates();

  const [searchInstrument, setSearchInstrument] = useState('');

  const [searchResults, setSearchResults] = useState([]);

  const [showSearchResults, setShowSearchResults] = useState(true);

  const handleStartDateChange = (value) => setStartDate(value);

  const handleEndDateChange = (value) => setEndDate(value);

  const url =
    `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/nombre?` +
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
      .catch((error) => {});
  };

  useEffect(() => {
    searchInstrument.trim() !== '' ? searchApi() : setSearchResults([]);
  }, [searchInstrument]);

  const handleInputChange = (e) => {
    const searchText = e.target.value.toLowerCase();
    setSearchInstrument(searchText);
  };

  const handleReset = () => {
    setSearchInstrument('');
    setSearchResults([]);
    setShowSearchResults(true);
  };

  const handleResultClick = (result) => {
    setSearchInstrument(result);
    setSearchResults([]);
    setShowSearchResults(false);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!startDate || !endDate || !searchInstrument) {
      toast.error('Por favor completa todos los campos.');
      return;
    }

    const formattedStartDate = startDate
      ? startDate.format('YYYY-MM-DD')
      : null;

    const formattedEndDate = endDate ? endDate.format('YYYY-MM-DD') : null;

    const params = {
      nombre: searchInstrument,
      fechaEntrega: formattedEndDate,
      fechaRetiro: formattedStartDate,
    };

    const url = new URL(
      `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/disponibles`
    );
    url.search = new URLSearchParams(params).toString();

    fetch(url)
      .then((response) => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
      })
      .then((data) => {
        setInstruments(data);
      })
      .catch(() => toast.error('Hubo un problema al obtener los datos'));
  };

  return (
    <form className="form-home" onSubmit={handleSubmit}>
      <div className="input-form-home-pickUp">
        <h3 style={{ textAlign: 'center' }}>
          Reserva Tu Instrumento Musical ¡Hoy Mismo!
        </h3>
        <div className="search-date2">
          <div className="input-form-home">
            <div className="btn-search-form">
              <button className="btn-search" title="Presiona para buscar">
                <svg
                  width="17"
                  height="16"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  role="img"
                  aria-labelledby="search"
                >
                  <path
                    d="M7.667 12.667A5.333 5.333 0 107.667 2a5.333 5.333 0 000 10.667zM14.334 14l-2.9-2.9"
                    stroke="currentColor"
                    strokeWidth="1.333"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  ></path>
                </svg>
              </button>
              <input
                className="search-input-form-home2"
                id="Buscar"
                placeholder="Buscar..."
                type="text"
                value={searchInstrument}
                onChange={handleInputChange}
              />
              <button
                className="reset-btn-search-form"
                type="reset"
                onClick={handleReset}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth="2"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M6 18L18 6M6 6l12 12"
                  ></path>
                </svg>
              </button>
            </div>{' '}
            {showSearchResults && (
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
                  {searchInstrument && (
                    <li
                      className="li-search-principal"
                      onClick={() => setShowSearchResults(false)}
                    >
                      {searchResults.length} instrumentos encontrados
                    </li>
                  )}
                </ul>
              </div>
            )}
          </div>
          <div>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="es">
              <DemoContainer components={['DatePicker']}>
                <div style={{ width: '200px', height: '60px' }}>
                  <DatePicker
                    label="Fecha retiro"
                    value={startDate}
                    onChange={handleStartDateChange}
                    minDate={dayjs().add(1, 'day')}
                    disablePast
                    format="YYYY-MM-DD"
                    inputFormat="YYYY-MM-DD"
                    className="customDatePickerStyle"
                  />
                </div>
              </DemoContainer>
            </LocalizationProvider>
          </div>
          <div>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="es">
              <DemoContainer components={['DatePicker']}>
                <div style={{ width: '200px', height: '60px' }}>
                  <DatePicker
                    label="Fecha entrega"
                    value={endDate}
                    onChange={handleEndDateChange}
                    disablePast
                    minDate={
                      startDate
                        ? dayjs(startDate).add(1, 'day')
                        : dayjs().add(1, 'day')
                    }
                    format="YYYY-MM-DD"
                    inputFormat="YYYY-MM-DD"
                    className="customDatePickerStyle"
                  />
                </div>
              </DemoContainer>
            </LocalizationProvider>
          </div>
          <button className="btn-buscar" type="submit">
            Ver opciones
          </button>
        </div>
      </div>
    </form>
  );
};

export default FormHome2;
