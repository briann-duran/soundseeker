import './App.css';
import { Route, Routes } from 'react-router-dom';
import { Home } from './Routes/Home';
import Contact from './Routes/Contact';
import Categories from './Routes/Categories';
import Admin from './Routes/Admin';
import Producto from './Components/Producto/Producto';
import Modals from './Routes/Modals';
import Login from './Routes/Login';
import Signup from './Routes/Signup';
import LayoutHome from './Components/Layout/LayoutHome';
import LayoutAdmin from './Components/Layout/LayoutAdmin';
import CategoryProduct from './Components/CategoryProduct';
import AdmCaracteristicas from './Components/AdmCaracteristicas/AdmCaracteristicas';
import Rent from './Routes/Rent';
import AdmUser from './Components/AdmUser';
import Error from './Components/Error';
import Favoritos from './Routes/Favoritos';
import HistoryRent from './Routes/HistoryRent';
import Politicas from './Routes/Politicas.jsx';
import DataUser from './Routes/DataUser.jsx';

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<LayoutHome />}>
          <Route index element={<Home />} />
          <Route path="/home" element={<Home />} />
          <Route path="/details/:id" element={<Producto />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/instrumentos" element={<Categories />} />
          <Route path="/favoritos" element={<Favoritos />} />
          <Route path="/rent" element={<Rent />} />
          <Route path="/alquileres" element={<HistoryRent />} />
          <Route path="/politicas" element={<Politicas />} />
          <Route path="/usuario" element={<DataUser />} />
          <Route path="/modals" element={<Modals />} />
          <Route path="*" element={<Error />} />
        </Route>
        <Route path="/admin" element={<LayoutAdmin />}>
          <Route index element={<Admin />} />
          <Route path="/admin/category" element={<CategoryProduct />} />
          <Route
            path="/admin/caracteristicas"
            element={<AdmCaracteristicas />}
          />
          <Route path="/admin/users" element={<AdmUser />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="*" element={<Error />} />
      </Routes>
    </>
  );
}

export default App;
