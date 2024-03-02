import React from 'react';
import '../Components/Styles/InstrumentAvailability.css';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DateCalendar } from '@mui/x-date-pickers/DateCalendar';
import dayjs from 'dayjs';
import 'dayjs/locale/es';
import { useRecipeStates } from '../Context/globalContext';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import { useNavigate } from 'react-router-dom';

const InstrumentAvailability = ({ instrument }) => {
  const { startDate, endDate, setEndDate, setStartDate, setInstrument } =
    useRecipeStates();

  const navigate = useNavigate();

  const storedUser = JSON.parse(localStorage.getItem('user'));

  const currentDate = dayjs();

  const handleStartDateChange = (newDate) => {
    setStartDate(newDate);
    setViewDate(newDate);
  };

  const fechasDeshabilitadas =
    instrument && instrument.fechasReservadas
      ? instrument.fechasReservadas.filter(
          (fecha) =>
            dayjs(fecha).isAfter(currentDate, 'day') ||
            dayjs(fecha).isSame(currentDate, 'day')
        )
      : [];

  const shouldDisableDate = (date) => {
    const formattedDate = date.format('YYYY-MM-DD');
    return fechasDeshabilitadas.includes(formattedDate);
  };

  const submitAvailability = () => {
    if (!startDate || !endDate) {
      Swal.fire({
        title: 'No has seleccionado fechas. ¿Deseas hacerlo?',
        showDenyButton: true,
        confirmButtonText: 'Sí',
        denyButtonText: 'No, gracias después',
        confirmButtonColor: '#3563E9',
        denyButtonColor: '#3563E9',
      }).then((result) => {
        if (result.isConfirmed) {
        } else if (result.isDenied) {
          if (storedUser) {
            setInstrument(instrument);
            navigate('/rent');
          } else {
            Swal.fire('¡Para alquilar, ingresa a tu cuenta!').then((r) =>
              Promise.resolve()
            );
          }
        }
      });
    } else {
      setInstrument(instrument);
      navigate('/rent');
    }
  };

  return (
    <div className="div-availability">
      <div>
        <h2>Disponibilidad del producto</h2>
      </div>
      <div className="div-calendars">
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="es">
          <DateCalendar
            label="Fecha retiro"
            style={{ margin: 0 }}
            value={startDate}
            onChange={handleStartDateChange}
            minDate={dayjs().add(1, 'day')}
            disablePast
            format="YYYY-MM-DD"
            shouldDisableDate={shouldDisableDate}
          />
        </LocalizationProvider>
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="es">
          <DateCalendar
            label="Fecha entrega"
            style={{ margin: 0 }}
            value={endDate}
            onChange={(newValue) => setEndDate(newValue)}
            minDate={
              startDate ? dayjs(startDate).add(1, 'day') : dayjs().add(1, 'day')
            }
            disablePast
            format="YYYY-MM-DD"
            shouldDisableDate={shouldDisableDate}
          />
        </LocalizationProvider>
      </div>
      <div className="confirm-availability">
        <p>Agregá tus fechas para alquilarlo</p>
        <button className="submit-availability" onClick={submitAvailability}>
          Alquilar ahora
        </button>
      </div>
    </div>
  );
};

export default InstrumentAvailability;
