import React from 'react';
import './CardHistoryrent.css';
import { useRecipeStates } from '../../Context/globalContext';
import { TbCalendarCheck } from 'react-icons/tb';
import { RiMoneyDollarCircleFill } from 'react-icons/ri';

const CardHistoryRent = () => {
  const { startDate, endDate, instrumentRent } = useRecipeStates();

  const formattedStartDate = startDate ? startDate.format('DD-MM-YYYY') : null;
  const formattedEndDate = endDate ? endDate.format('DD-MM-YYYY') : null;

  return (
    <div className="card-history">
      <div className="card-history-title">
        <img
          className="card-history-img"
          src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrumentRent.imagenes[0]}`}
          alt=""
        />
        <h2>{instrumentRent.nombre}</h2>
      </div>
      <div className="card-history-date-price">
        <div className="card-history-date">
          <TbCalendarCheck color="#3563e9" size={'40px'} />
          <p className="txt-card-history-date-price">Fecha retiro:</p>
          <p className="txt-card-history-date-price">{formattedStartDate}</p>
        </div>
        <hr className="vertical-line" />
        <div className="card-history-date">
          <TbCalendarCheck color="#3563e9" size={'40px'} />
          <p className="txt-card-history-date-price">Fecha entrega:</p>
          <p className="txt-card-history-date-price">{formattedEndDate}</p>
        </div>
        <hr className="vertical-line" />
        <div className="card-history-date">
          <RiMoneyDollarCircleFill color="#3563e9" size={'40px'} />
          <p className="txt-card-history-date-price">Precio total:</p>
          <p className="txt-card-history-date-price">
            $ {instrumentRent.precio}
          </p>
        </div>
      </div>
      <div className="div-rating">
        <div className="div-rating">
          <h2>Calificar</h2>
          <div className="rating-instrument">
            <input value="5" name="rating" id="star5" type="radio" />
            <label htmlFor="star5"></label>
            <input value="4" name="rating" id="star4" type="radio" />
            <label htmlFor="star4"></label>
            <input value="3" name="rating" id="star3" type="radio" />
            <label htmlFor="star3"></label>
            <input value="2" name="rating" id="star2" type="radio" />
            <label htmlFor="star2"></label>
            <input value="1" name="rating" id="star1" type="radio" />
            <label htmlFor="star1"></label>
          </div>
        </div>
        <div>
          <button className="btn-volver-alquilar">Volver alquilar</button>
        </div>
      </div>
    </div>
  );
};

export default CardHistoryRent;
