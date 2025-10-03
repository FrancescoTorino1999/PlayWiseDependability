import "./Carusel.scss"
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Slider from "react-slick";
import Card from "../Card/Card";
import { CONSTANTS } from "../../constants";
import { ENV } from "../../env";

interface CaruselProps {
    page: number;
    size: number;
    sort: string;
    title: string;
}

function Carusel({ page, size, sort, title }: CaruselProps) {

    const [games, setGames] = useState<any[]>([]);

    const settings = {
        dots: true,
        infinite: true,
        speed: 500,
        slidesToShow: 3,
        slidesToScroll: 1,
        autoplay: false,
        autoplaySpeed: 3000,
        arrows: true,
        responsive: [
            {
                breakpoint: 1000,
                settings: {
                    slidesToShow: 1,
                    arrows: false,
                }
            },
            {
                breakpoint: 1500,
                settings: {
                    slidesToShow: 2,
                }
            }
        ]
    };

    useEffect(() => {
            console.log("here")
            fetch(`${ ENV.ENVIRONMENT }/games/games?page=${page}&size=${size}&sort=${sort}`)
                .then(response => response.json())
                .then(data => {
                    setGames(data.content || []);
                })
                .catch(error => console.error('Error fetching data:', error));
        }, []); 

    return (
        
        <>
           <div className="carousel-container">
                <div className = "font-heading-desktop-xl font-heading-l mb-80">{title}</div>

                <Slider {...settings}>
                    {games.map((game, index) => (
                        <div key={index}>

                            <Card game = {game}>

                            </Card>
                            
                        </div>
                        
                    ))}
                </Slider>
            </div>
        </>

    )

}

export default Carusel;