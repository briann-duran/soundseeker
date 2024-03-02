import React from 'react';
import { FaRegStar } from 'react-icons/fa';
import Rating from 'react-rating';

const Ranking = ({ setRating, rating, rent, historialApi }) => {
  const rankingApi = async (rentId, ratingValue) => {
    const url = `${import.meta.env.VITE_APP_BACK_END_URL}/reservas?id=${rentId}&calificacion=${ratingValue}`;
    const config = {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwt')}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ calificacion: ratingValue }),
    };

    try {
      const response = await fetch(url, config);
      if (!response.ok) throw new Error('Error al obtener datos');
      const data = await response.text();
      historialApi();
      if (data) JSON.parse(data);
    } catch (error) {}
  };

  const handleRatingChange = (value) => {
    setRating(Number(value));
    rankingApi(rent.id, value).then((r) => Promise.resolve());
  };

  const isRated = rating !== null && rating !== undefined;

  return (
    <>
      <h2>{isRated ? 'Calificado' : 'Calificar'}</h2>
      <Rating
        style={{ marginLeft: '15px' }}
        initialRating={rating}
        emptySymbol={<FaRegStar color="grey" size={'25px'} />}
        fullSymbol={<FaRegStar color="blue" size={'25px'} />}
        onChange={handleRatingChange}
        readonly={isRated}
      />
    </>
  );
};

export default Ranking;
