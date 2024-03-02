import React from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import '../Banners/Banner.css';
import im1 from './banners/b1.webp';
import im2 from './banners/b2.webp';
import im3 from './banners/b3.webp';
import im4 from './banners/b4.webp';
import { Navigation } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';

const imagenesBanner = [
  { id: 1, url: im1 },
  { id: 2, url: im2 },
  { id: 3, url: im3 },
  { id: 4, url: im4 },
];

const Banner = () => {
  return (
    <div className="banner-principal">
      <Swiper navigation={true} modules={[Navigation]} className="mySwiper">
        {imagenesBanner.map((imagen) => (
          <SwiperSlide key={imagen.id}>
            <img src={imagen.url} alt={`Banner ${imagen.id}`} />
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default Banner;
