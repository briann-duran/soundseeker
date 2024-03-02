import React from 'react';
import { ImMusic } from 'react-icons/im';
import { TbCircleCheckFilled } from 'react-icons/tb';
import { FaCalendarCheck } from 'react-icons/fa';
import './Stepper.css';

const Stepper = ({ reservado, alquilado, finalizado }) => {
  const colorReservado = finalizado ? '#4CAF50' : reservado ? 'orange' : 'blue';
  const colorAlquilado = finalizado
    ? '#4CAF50'
    : alquilado
      ? 'blue'
      : '#F5F5F5';
  const colorFinalizado = finalizado ? '#4CAF50' : '#F5F5F5';

  return (
    <>
      <div className="stepper">
        <div style={{ margin: 0 }}>
          <FaCalendarCheck color={colorReservado} size={'30px'} />{' '}
        </div>
        <hr color={colorAlquilado} />
        <div style={{ margin: 0 }}>
          <ImMusic color={colorAlquilado} size={'30px'} />{' '}
        </div>
        <hr color={colorFinalizado} />
        <div style={{ margin: 0 }}>
          <TbCircleCheckFilled color={colorFinalizado} size={'30px'} />{' '}
        </div>
      </div>
      <div className="stepper">
        {reservado ? (
          <p
            style={{
              color: colorReservado,
              margin: 0,
              textAlign: 'left',
              width: '100%',
            }}
          >
            Reservado
          </p>
        ) : (
          ''
        )}
        {alquilado ? (
          <p
            style={{
              color: colorAlquilado,
              margin: 0,
              textAlign: 'center',
              width: '100%',
            }}
          >
            Tocando música
          </p>
        ) : (
          ''
        )}
        {finalizado ? (
          <p
            style={{
              color: colorFinalizado,
              margin: 0,
              textAlign: 'right',
              width: '100%',
            }}
          >
            Finalizado
          </p>
        ) : (
          ''
        )}
      </div>
    </>
  );
};

export default Stepper;
