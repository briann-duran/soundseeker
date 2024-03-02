import { createContext, useContext, useEffect, useState } from 'react';
import axios from 'axios';

export const ContextGlobal = createContext();

export const ContextProvider = ({ children }) => {
  const [data, setData] = useState();
  const [cate, setCate] = useState();
  const [random, setRandom] = useState();
  const [caracteristicas, setCaracteristicas] = useState([]);
  const [favoritos, setFavoritos] = useState([]);
  const [favs, setfavs] = useState([]);
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [instrumentRent, setInstrument] = useState();

  const storedUser = JSON.parse(localStorage.getItem('user'));

  const getfavs = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_URL}/clientes/${storedUser.nombreUsuario}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
          },
        }
      );
      setfavs(response.data);
    } catch (error) {}
  };

  if (storedUser) {
    useEffect(() => {
      getfavs().then();
    }, []);
  }

  const addToFavoritos = (params) => {
    const url = new URL(
      `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/favoritos`
    );
    url.search = new URLSearchParams(params).toString();
    fetch(url)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Error en la solicitud');
        }
        getfavs().then();
      })
      .catch();
  };

  const removeFromFavoritos = (params) => {
    const url = new URL(
      `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/favoritos`
    );
    url.search = new URLSearchParams(params).toString();
    fetch(url)
      .then((response) => {
        if (!response.ok) throw new Error('Error en la solicitud');
        getfavs().then(Promise.resolve);
      })
      .catch();
  };

  const getInstruments = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos`
      );
      setData(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    getInstruments().then();
  }, []);

  const getRandom = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_URL}/instrumentos/aleatorio`
      );
      setRandom(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    getRandom().then();
  }, []);

  const getCategorys = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_URL}/categorias`
      );
      setCate(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    getCategorys().then();
  }, []);

  const getCaracteristicas = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_APP_BACK_END_URL}/caracteristicas`
      );
      setCaracteristicas(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    getCaracteristicas().then();
  }, []);

  const [filters, setFilters] = useState({
    category: [],
    minPrice: 250,
  });

  const filterProducts = (instruments) => {
    return instruments.filter((instrument) => {
      return (
        instrument.precio >= filters.minPrice &&
        (filters.category.length === 0 ||
          filters.category.includes(instrument.categoria.id) ||
          filters.category.includes('all'))
      );
    });
  };

  const [politicas, setPoliticas] = useState([]);

  useEffect(() => {
    fetch(`${import.meta.env.VITE_APP_BACK_END_URL}/politicas`)
      .then((res) => res.json())
      .then((data) => setPoliticas(data))
      .catch(() => {});
  }, []);

  return (
    <ContextGlobal.Provider
      value={{
        data,
        cate,
        filters,
        setFilters,
        filterProducts,
        random,
        getCategorys,
        getInstruments,
        caracteristicas,
        favoritos,
        addToFavoritos,
        removeFromFavoritos,
        favs,
        startDate,
        setStartDate,
        endDate,
        setEndDate,
        instrumentRent,
        setInstrument,
        politicas,
      }}
    >
      {children}
    </ContextGlobal.Provider>
  );
};

export const useRecipeStates = () => useContext(ContextGlobal);
