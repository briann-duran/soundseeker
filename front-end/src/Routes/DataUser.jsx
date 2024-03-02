import React from 'react';
import CardUserData from '../Components/CardUserData';
import '../Components/Styles/DataUser.css';

const DataUser = () => {
  const storedUser = JSON.parse(localStorage.getItem('user'));

  return (
    <main className="main-data-user">
      <div>
        <CardUserData storedUser={storedUser} />
      </div>
    </main>
  );
};

export default DataUser;
