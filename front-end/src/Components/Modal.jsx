import React from 'react';
import './Styles/Modal.css';
import 'sweetalert2/dist/sweetalert2.min.css';

const Modal = ({ title, content }) => {
  return (
    <div className="page-add-instrument">
      <div className="modal-add-instrument">
        <h2>{title}</h2>
        {content}
      </div>
    </div>
  );
};

export default Modal;
