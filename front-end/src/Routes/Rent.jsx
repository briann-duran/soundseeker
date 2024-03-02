import React, { useEffect, useState } from 'react';
import { useRecipeAuth } from '../Context/authContext';
import { Navigate, useNavigate } from 'react-router-dom';
import '../Components/Styles/Rent.css';
import { useRecipeStates } from '../Context/globalContext';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import 'dayjs/locale/es';
import axios from 'axios';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

const Rent = () => {
  const { user } = useRecipeAuth();
  const { startDate, endDate, setEndDate, setStartDate, instrumentRent } =
    useRecipeStates();
  const [textArea, setTextArea] = useState();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [dataRent, setDataRent] = useState({
    fechaRetiro: '',
    fechaEntrega: '',
    notas: '',
    usuario: {
      nombreUsuario: '',
    },
    productos: [
      {
        id: '',
      },
    ],
  });

  const storedUser = JSON.parse(localStorage.getItem('user'));

  if (!storedUser) return <Navigate to={'/login'} />;

  useEffect(() => {
    if (storedUser && instrumentRent) {
      setDataRent((prevData) => ({
        ...prevData,
        usuario: { nombreUsuario: storedUser.nombreUsuario },
        productos: [{ id: instrumentRent.id }],
      }));
    }
  }, []);

  const handleStartDateChange = (value) => setStartDate(value);

  const handleEndDateChange = (value) => setEndDate(value);

  const handleInputChange = (event) => {
    setTextArea(event.target.value);
    setDataRent((prevData) => ({
      ...prevData,
      notas: event.target.value,
    }));
  };

  const currentDate = dayjs();

  const fechasDeshabilitadas =
    instrumentRent && instrumentRent.fechasReservadas
      ? instrumentRent.fechasReservadas.filter(
          (fecha) =>
            dayjs(fecha).isAfter(currentDate, 'day') ||
            dayjs(fecha).isSame(currentDate, 'day')
        )
      : [];

  const shouldDisableDate = (date) => {
    const formattedDate = date.format('YYYY-MM-DD');
    return fechasDeshabilitadas.includes(formattedDate);
  };

  const diferenciaEnMs = startDate - endDate;
  const unDiaEnMs = 1000 * 60 * 60 * 24;
  const diferenciaEnDias = Math.floor(Math.abs(diferenciaEnMs / unDiaEnMs));

  const submitRent = (event) => {
    event.preventDefault();
    if (!startDate || !endDate) {
      Swal.fire({
        icon: 'error',
        title: 'Fechas faltantes',
        text: 'Debes seleccionar las fechas de retiro y entrega',
      }).then((r) => Promise.resolve());
      return;
    }

    setLoading(true);

    const fechaRetiro = startDate ? startDate.format('YYYY-MM-DD') : '';
    const fechaEntrega = endDate ? endDate.format('YYYY-MM-DD') : '';

    const updatedDataRent = {
      ...dataRent,
      fechaRetiro,
      fechaEntrega,
    };

    axios
      .post(
        `${import.meta.env.VITE_APP_BACK_END_URL}/reservas`,
        updatedDataRent,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` },
        }
      )
      .then((response) => {
        Swal.fire({
          position: 'center',
          icon: 'success',
          title: 'Instrumento reservado con éxito',
          showConfirmButton: false,
          timer: 2000,
        }).then((r) => Promise.resolve());
        setEndDate(null);
        setStartDate(null);
        setTimeout(() => {
          navigate('/');
        }, 2000);
      })
      .catch((e) => {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: e.response?.data?.message || 'Error al enviar la solicitud',
        }).then((r) => Promise.resolve());
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <>
      {loading ? (
        <main className="main-loading-rent">
          <div className="loading">
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            <span></span>
          </div>
        </main>
      ) : (
        <div className="content-rent">
          <div className="rental-summary">
            <div className="div-rental-summary-title">
              <div className="text-wrapper-5">Resumen de alquiler</div>
              <p className="text-p">
                Los precios pueden cambiar dependiendo de la duración del
                alquiler y del precio del instrumento.
              </p>
            </div>
            <div className="look">
              <img
                className="image-removebg"
                alt="Image removebg"
                src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrumentRent.imagenes[0]}`}
              />
              <div className="text-instruiment-name">
                {instrumentRent.nombre}
              </div>
            </div>
            <hr className="line" />
            <div className="total-price">
              <div className="text-total-price">Precio total del alquiler</div>
              <div className="price-total">
                ${diferenciaEnDias * instrumentRent.precio}
              </div>
            </div>
          </div>
          <div className="payment">
            <div className="billing-info">
              <div className="text-wrapper-5">Información de facturación</div>
              <div className="data-user-info">
                <div className="coolinput">
                  <label htmlFor="input" className="text-rent-name">
                    Nombre:
                  </label>
                  <input
                    type="text"
                    placeholder={storedUser.nombre}
                    name="input-nombre-rent"
                    className="input-nombre-rent"
                    readOnly
                  />
                </div>
                <div className="coolinput">
                  <label htmlFor="input" className="text-rent-name">
                    Apellido:
                  </label>
                  <input
                    type="text"
                    placeholder={storedUser.apellido}
                    name="input-nombre-rent"
                    className="input-nombre-rent"
                    readOnly
                  />
                </div>
                <div className="coolinput">
                  <label htmlFor="input" className="text-rent-name">
                    Nombre de usuario:
                  </label>
                  <input
                    type="text"
                    placeholder={storedUser.nombreUsuario}
                    name="input-nombre-rent"
                    className="input-nombre-rent"
                    readOnly
                  />
                </div>
                <div className="coolinput">
                  <label htmlFor="input" className="text-rent-name">
                    Mail:
                  </label>
                  <input
                    type="text"
                    placeholder={storedUser.correoElectronico}
                    name="input-nombre-rent"
                    className="input-nombre-rent"
                    readOnly
                  />
                </div>
              </div>
            </div>
            <div className="rental-info">
              <div className="text-wrapper-5">Información de alquiler</div>
              <div className="date">
                <LocalizationProvider
                  dateAdapter={AdapterDayjs}
                  adapterLocale="es"
                >
                  <DemoContainer components={['DatePicker']}>
                    <DatePicker
                      label="Fecha retiro"
                      value={startDate}
                      onChange={handleStartDateChange}
                      minDate={dayjs().add(1, 'day')}
                      disablePast
                      format="YYYY-MM-DD"
                      inputFormat="YYYY-MM-DD"
                      shouldDisableDate={(date) =>
                        shouldDisableDate(dayjs(date))
                      }
                    />
                  </DemoContainer>
                </LocalizationProvider>
              </div>
              <div className="date-2">
                <LocalizationProvider
                  dateAdapter={AdapterDayjs}
                  adapterLocale="es"
                >
                  <DemoContainer components={['DatePicker']}>
                    <DatePicker
                      label="Fecha entrega"
                      value={endDate}
                      onChange={handleEndDateChange}
                      minDate={
                        startDate
                          ? dayjs(startDate).add(1, 'day')
                          : dayjs().add(1, 'day')
                      }
                      format="YYYY-MM-DD"
                      inputFormat="YYYY-MM-DD"
                      shouldDisableDate={(date) =>
                        shouldDisableDate(dayjs(date))
                      }
                    />
                  </DemoContainer>
                </LocalizationProvider>
              </div>
              <div className="div-notes">
                <div className="text-wrapper-7">Notas</div>
                <textarea
                  className="dynamic-textarea"
                  value={textArea}
                  onChange={handleInputChange}
                  rows={1}
                  placeholder="Escribe aquí..."
                />
              </div>
            </div>
            <div className="confirmation-rental">
              <div className="div-title-confirmation-rental">
                <div className="text-wrapper-5">Estamos llegando al final.</div>
              </div>
              <div className="overlap-group-confirmation">
                <p>
                  Después de realizar la reserva, recibirás un correo
                  electrónico detallado con toda la información relevante.
                </p>
              </div>
              <div className="button-rental">
                <button className="text-wrapper-20" onClick={submitRent}>
                  Confirmar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Rent;
