import React from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css"; 
import "slick-carousel/slick/slick-theme.css";
import "./HeroCarusel.scss"; 

interface HeroCarouselProps {
    screens: string[];
}

function HeroCarusel({ screens }: HeroCarouselProps) {
    const settings = {
        infinite: true,
        speed: 500,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 3000,
        arrows: false
    };

    return (
        <div className="hero-carusel">
            <div className="hero-banner">
                <Slider {...settings}>
                    {screens.map((screen, index) => (
                        <div key={index} className="carousel-slide">
                            <div>
                            <img src={"https:" + screen} alt={`Screenshot ${index + 1}`} className="carousel-image" />
                            </div>
                            
                        </div>
                    ))}
                </Slider>
            </div>
        </div>
    );
}

export default HeroCarusel;