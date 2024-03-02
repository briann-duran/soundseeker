import React from 'react';
import { FaWhatsapp } from 'react-icons/fa';
import './Whatsapp.css';

const Whatsapp = ({ phone, textWhats }) => {
  const handleWhats = () => {
    const whatsappURL = `https://api.whatsapp.com/send?phone=${phone}&text=${textWhats}`;
    window.open(whatsappURL, '_blank');
  };

  return (
    <div className="whatss">
      <button
        className="btn-whats"
        onClick={handleWhats}
        title="Contáctanos por WhatsApp"
        type="button"
      >
        <FaWhatsapp />
      </button>
    </div>
  );
};

export default Whatsapp;
