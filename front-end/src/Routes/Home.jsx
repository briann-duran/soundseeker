import React, { useRef, useState } from 'react';
import Category from '../Components/Category';
import Recomends from '../Components/Recomends';
import FormHome2 from '../Components/FormHome2';
import ResultSearchInstrumentHome from '../Components/ResultSearchInstrumentHome';
import { Toaster } from 'sonner';
import Banner from '../Components/Banners/Banner';

export const Home = () => {
  const [instruments, setInstruments] = useState();

  const resultRef = useRef(null);

  const scrollToResult = () => {
    if (resultRef.current) {
      resultRef.current.scrollIntoView({
        behavior: 'smooth',
        top: resultRef.current.offsetTop,
      });
    }
  };

  return (
    <div className="page">
      <Toaster position="top-right" richColors />
      <Banner />
      <FormHome2
        setInstruments={setInstruments}
        scrollToResult={scrollToResult}
      />
      <Category />
      <div ref={resultRef}>
        {instruments ? (
          <ResultSearchInstrumentHome instruments={instruments} />
        ) : (
          <Recomends />
        )}
      </div>
    </div>
  );
};
