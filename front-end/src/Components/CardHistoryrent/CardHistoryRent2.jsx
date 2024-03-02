import React, { useEffect, useState } from 'react';
import './CardHistoryRent2.css';
import { TbCalendarCheck } from 'react-icons/tb';
import { RiMoneyDollarCircleFill } from 'react-icons/ri';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import Stepper from '../Stepper/Stepper';
import { MdOutlineCancel } from 'react-icons/md';
import { useNavigate } from 'react-router-dom';
import Ranking from './Ranking';

const CardHistoryRent2 = ({ rent, historialApi }) => {
  const navigate = useNavigate();

  const [rating, setRating] = useState(null);

  useEffect(() => {
    if (rent?.calificacion) setRating(rent.calificacion);
  }, [rent]);

  function dentroDe24Horas(fecha) {
    const MILISEGUNDOS_EN_UN_DIA = 24 * 60 * 60 * 1000;
    const fechaActual = new Date();
    const fechaComparar = new Date(fecha);
    const diferenciaEnMs = Math.abs(fechaActual - fechaComparar);
    return diferenciaEnMs <= MILISEGUNDOS_EN_UN_DIA;
  }

  const estaDentroDe24Horas = dentroDe24Horas(rent.fechaOrden);

  function esFechaPosteriorALaActual(fecha) {
    const fechaActual = new Date();
    const fechaComparar = new Date(fecha);
    return fechaComparar < fechaActual;
  }

  const resultadoComparacion = esFechaPosteriorALaActual(rent.fechaEntrega);

  function estaDentroDelRango(fechaInicio, fechaFin) {
    const fechaActual = new Date();
    const fechaInicioRango = new Date(fechaInicio);
    const fechaFinRango = new Date(fechaFin);
    return fechaActual >= fechaInicioRango && fechaActual <= fechaFinRango;
  }

  const estaEnAlquiler = estaDentroDelRango(
    rent.fechaRetiro,
    rent.fechaEntrega
  );

  const esfechaAnteriorAlRetiro = (fecha) => {
    const fechaActual = new Date();
    const fechaComparar = new Date(fecha);
    return fechaActual < fechaComparar;
  };

  const reservada = esfechaAnteriorAlRetiro(rent.fechaRetiro);

  const deleteRent = () => {
    const rentId = rent.id;
    Swal.fire({
      title: '¿Estas seguro de querer cancelar la reserva?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '¡Sí!',
      cancelButtonText: '¡No!',
    }).then((result) => {
      if (result.isConfirmed) deleteRentApi(rentId);
    });
  };

  const deleteRentApi = (rentId) => {
    fetch(`${import.meta.env.VITE_APP_BACK_END_URL}/reservas/${rentId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwt')}`,
      },
    })
      .then((response) => {
        if (response.ok) {
          Swal.fire('Reserva cancelada con éxito').then((r) =>
            Promise.resolve()
          );
          historialApi();
        } else {
          Swal.fire(
            'Error',
            'Error al intentar cancelar la reserva',
            'error'
          ).then((r) => Promise.resolve());
          throw new Error('Error al eliminar la reserva');
        }
      })
      .then((r) => historialApi())
      .catch((error) => {
        Swal.fire(
          'Error',
          'Error al intentar cancelar la reserva',
          'error'
        ).then((r) => Promise.resolve());
      });
  };

  const btnNewRent = () => {
    navigate('/details/' + rent.productos[0].id);
  };

  return (
    <div className="card-history2">
      <div className="div-card-history-img-2">
        <img
          className="card-history-img-2"
          src={`${import.meta.env.VITE_APP_IMAGE_URL}${rent.productos[0].imagenes[0]}`}
          alt={rent.productos[0].nombre}
        />
      </div>
      <div className="card-history-title-2">
        <p style={{ fontSize: '18px', margin: '0px', fontWeight: 'bold' }}>
          {rent.productos[0].nombre}
        </p>
        <div className="card-history-date-price-2">
          <div className="card-history-date">
            <TbCalendarCheck color="#3563e9" size={'40px'} />
            <p className="txt-card-history-date-price">Fecha retiro:</p>
            <p className="txt-card-history-date-price">{rent.fechaRetiro}</p>
          </div>
          <hr className="vertical-line" />
          <div className="card-history-date">
            <TbCalendarCheck color="#3563e9" size={'40px'} />
            <p className="txt-card-history-date-price">Fecha entrega:</p>
            <p className="txt-card-history-date-price">{rent.fechaEntrega}</p>
          </div>
          <hr className="vertical-line" />
          <div className="card-history-date">
            <RiMoneyDollarCircleFill color="#3563e9" size={'40px'} />
            <p className="txt-card-history-date-price">Precio total:</p>
            <p className="txt-card-history-date-price">$ {rent.total}</p>
          </div>
        </div>
        <div className="stepper-carhistory2">
          <Stepper
            reservado={reservada}
            alquilado={estaEnAlquiler}
            finalizado={resultadoComparacion}
          />
        </div>
        <div className="div-rating">
          {estaDentroDe24Horas ? (
            <div>
              <button className="btn-cancelar-reserva" onClick={deleteRent}>
                {' '}
                <span className="txt-cancelar-reserva">Cancelar</span>{' '}
                <MdOutlineCancel className="icon-cancel-reserva" />{' '}
              </button>
            </div>
          ) : resultadoComparacion ? (
            <div className="div-rating">
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'row',
                  alignItems: 'center',
                }}
              >
                <Ranking
                  setRating={setRating}
                  rating={rating}
                  rent={rent}
                  historialApi={historialApi}
                />
              </div>
              <div>
                <button className="btn-volver-alquilar" onClick={btnNewRent}>
                  Volver alquilar
                </button>
              </div>
            </div>
          ) : estaEnAlquiler ? (
            <div></div>
          ) : (
            <div>No olvides retirar tu instrumento en la fecha indicada</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CardHistoryRent2;
