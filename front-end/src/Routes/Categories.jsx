import React, { useId, useState } from 'react';
import '../Components/Styles/Categories.css';
import { useRecipeStates } from '../Context/globalContext';
import Card from '../Components/Card';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';

const Categories = () => {
  const { data, cate, filters, setFilters, filterProducts } = useRecipeStates();

  const minPriceFilterId = useId();

  const [isAsideVisible, setAsideVisible] = useState(true);

  const itemsPerPage = 10;

  const [page, setPage] = useState(1);

  const pageCount = data
    ? Math.ceil(filterProducts(data).length / itemsPerPage)
    : 0;

  const handleChangePage = (event, newPage) => setPage(newPage);

  const startIndex = (page - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentItems = data
    ? filterProducts(data).slice(startIndex, endIndex)
    : 0;

  const estadoAside = () => {
    setAsideVisible(!isAsideVisible);
  };

  const handleChangeMinPrice = (event) => {
    setFilters((prevState) => ({
      ...prevState,
      minPrice: event.target.value,
    }));
  };

  const handleCheckboxChange = (e) => {
    const { name, id, checked } = e.target;
    if (name === 'all') {
      if (checked) {
        setFilters((prevState) => ({
          ...prevState,
          category: ['all'],
        }));
      } else {
        setFilters((prevState) => ({
          ...prevState,
          category: [],
        }));
      }
    } else {
      const categoryId = parseInt(id);
      const newCategory = checked
        ? [...filters.category, categoryId]
        : filters.category.filter((category) => category !== categoryId);
      setFilters((prevState) => ({
        ...prevState,
        category: newCategory.includes('all')
          ? newCategory.filter((category) => category !== 'all')
          : newCategory,
      }));
    }
  };

  const allOptionCategory = (
    <label key="all" className="checkbox-container">
      <input
        className="custom-checkbox"
        type="checkbox"
        name="all"
        value={data}
        checked={filters.category.length === 0}
        onChange={handleCheckboxChange}
      />
      <div className="checkmark">Todos</div>
    </label>
  );

  const categoryOptions = cate
    ? cate.map((category) => (
        <label key={category.id} className="checkbox-container">
          <input
            className="custom-checkbox"
            type="checkbox"
            name={category.nombre}
            value={category.id}
            id={category.id}
            checked={filters.category.includes(category.id)}
            onChange={handleCheckboxChange}
          />
          <div className="checkmark">{category.nombre}</div>
        </label>
      ))
    : [];

  return (
    <>
      <div className="container-sidebar">
        <div className="instruments">
          {isAsideVisible && (
            <aside className="categories-sidebar">
              <div className="div-inputs-aside">
                <p className="pcate">CATEGORÍA</p>
                {allOptionCategory}
                {categoryOptions}
              </div>
              <div className="form-label-precio">
                <p className="pcate">Precio</p>
                <input
                  type="range"
                  id={minPriceFilterId}
                  min="0"
                  max="50000"
                  onChange={handleChangeMinPrice}
                  value={filters.minPrice}
                />
                <span>${filters.minPrice}</span>
              </div>
            </aside>
          )}{' '}
          <div className="list-products">
            <div className="div-btn-aside">
              <button className="btn-aside" onClick={estadoAside}>
                {isAsideVisible ? 'Ocultar filtros' : 'Filtros'}
              </button>
            </div>
            {data ? (
              data.length > 0 ? (
                currentItems.map((instrument) => (
                  <Card key={instrument.id} instrument={instrument} />
                ))
              ) : (
                <p>No hay datos disponibles.</p>
              )
            ) : (
              <p>Cargando datos...</p>
            )}
          </div>
        </div>
        <div className="pagination-categories">
          <Stack spacing={2}>
            <Pagination
              count={pageCount}
              page={page}
              onChange={handleChangePage}
              variant="outlined"
              shape="rounded"
            />
          </Stack>
        </div>
      </div>
    </>
  );
};

export default Categories;
