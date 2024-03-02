import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar/Navbar';
import Footer from '../Footer/Footer';
import Whatsapp from '../Whatsapp/Whatsapp';

const LayoutHome = () => {
  return (
    <>
      <Navbar />
      <main className="content-main">
        <Outlet />
        <Whatsapp phone="+59898535255" textWhats="Hola, quiero saber más" />
      </main>
      <Footer />
    </>
  );
};

export default LayoutHome;
