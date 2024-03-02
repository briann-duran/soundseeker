import React from 'react';
import './Footer.css';
import { Link } from 'react-router-dom';

const Footer = () => {
  const nombreSitio = 'SoundSeeker';

  const socialMediaLinks = [
    { name: 'Tik Tok', url: 'https://www.tiktok.com/' },
    { name: 'Instagram', url: 'https://www.instagram.com/' },
    { name: 'Twitter', url: 'https://twitter.com/' },
    { name: 'Facebook', url: 'https://www.facebook.com/' },
  ];

  return (
    <div>
      <footer className="ft-footer">
        <div className="footer-left">
          <div className="sub-left">
            <div className="footer-logo">
              <Link to="/">
                <p className="sound">Sound</p>
              </Link>
              <Link to="/">
                {' '}
                <p className="seeker">Seeker</p>
              </Link>
            </div>
            <div className="p-footer-left">
              <p>
                Nuestra visión: brindar a los músicos las herramientas perfectas
                para expresar su pasión y creatividad musical.
              </p>
            </div>
          </div>
        </div>
        <div className="footer-right">
          <div className="r-sub-left">
            <h3>Empresa</h3>
            <Link to="/contact">Contacto</Link>
            <Link to="/politicas">Políticas</Link>
          </div>
          <div className="r-sub-right">
            <h3>Redes</h3>
            {socialMediaLinks.map((link, index) => (
              <a key={index} href={link.url}>
                {link.name}
              </a>
            ))}
          </div>
        </div>
      </footer>
      <hr />
      <div className="derechos">
        <p>
          ©{new Date().getFullYear()} {nombreSitio}. Todos los derechos
          reservados.
        </p>
      </div>
    </div>
  );
};

export default Footer;
