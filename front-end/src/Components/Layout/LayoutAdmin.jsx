import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import AdminNavBar from '../AdminNavBar';

const LayoutAdmin = () => {
  const storedUser = JSON.parse(localStorage.getItem('user'));
  if (!storedUser) {
    return <Navigate to={'/login'} />;
  } else if (storedUser.roles[0] !== 'ROLE_ADMIN') {
    alert('No tienes permisos para acceder');
    return <Navigate to={'/'} />;
  }

  return (
    <>
      <AdminNavBar />
      <main className="content-main-admin">
        <Outlet />
      </main>
    </>
  );
};

export default LayoutAdmin;
