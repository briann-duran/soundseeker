import React, { useState } from 'react';
import '../Components/Styles/CardSocial.css';
import { FaFacebook, FaTwitter, FaWhatsapp } from 'react-icons/fa';
import {
  FacebookShareButton,
  TwitterShareButton,
  WhatsappShareButton,
} from 'react-share';
import { FaLink } from 'react-icons/fa6';
import { toast } from 'sonner';
import CopyToClipboard from 'react-copy-to-clipboard';

const CardSocial = ({ instrument }) => {
  const [copied, setCopied] = useState(false);
  const linkToCopy = `${import.meta.env.VITE_APP_SERVER_URL}/details/${instrument.id}`;

  const handleCopy = () => {
    setCopied(true);
    toast.success('Enlace copiado');
  };

  return (
    <div>
      <div className="card3">
        <div className="imge-social-instrument">
          <div className="usericon-social-card3">
            {instrument.imagenes && instrument.imagenes.length > 0 && (
              <img
                src={`${import.meta.env.VITE_APP_IMAGE_URL}${instrument.imagenes[0]}`}
                alt={instrument.nombre}
              />
            )}
          </div>
          <div>
            <p className="userName-card3-social">{instrument.nombre}</p>
          </div>
        </div>

        <div className="description-card3-social">
          <p>{instrument.descripcion}</p>
        </div>

        <div className="card3-social">
          <CopyToClipboard text={linkToCopy} onCopy={handleCopy}>
            <a className="card3-social-link1">
              <FaLink size={30} color="white" />
            </a>
          </CopyToClipboard>

          <TwitterShareButton
            url={`${import.meta.env.VITE_APP_SERVER_URL}/details/${instrument.id}`}
            title={
              '¡Mira este increíble instrumento que encontré en SoundSeeker!'
            }
          >
            <a className="card3-social-link2">
              <FaTwitter size={30} color="white" />
            </a>
          </TwitterShareButton>
          <FacebookShareButton
            url={`${import.meta.env.VITE_APP_SERVER_URL}/details/${instrument.id}`}
            hashtag="#soundseeker"
            quote={
              '¡Mira este increíble instrumento que encontré en SoundSeeker!'
            }
          >
            <a className="card3-social-link3">
              <FaFacebook size={30} color="white" />
            </a>
          </FacebookShareButton>
          <WhatsappShareButton
            url={`${import.meta.env.VITE_APP_SERVER_URL}/details/${instrument.id}`}
            title={
              '¡Mira este increíble instrumento que encontré en SoundSeeker!'
            }
          >
            <a className="card3-social-link4">
              <FaWhatsapp size={30} color="white" />
            </a>
          </WhatsappShareButton>
        </div>
      </div>
    </div>
  );
};

export default CardSocial;
