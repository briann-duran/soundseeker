import React from 'react';
import { IoShareOutline } from 'react-icons/io5';
import CardSocial from '../../CardSocial';
import './SocialDropDown.css';

const SocialDropDown = ({ instrument }) => {
  return (
    <div className="dropdown">
      <IoShareOutline className="IoShareOutline" />
      <div className="menu-cardsocial">
        <CardSocial instrument={instrument} />
      </div>
    </div>
  );
};

export default SocialDropDown;
