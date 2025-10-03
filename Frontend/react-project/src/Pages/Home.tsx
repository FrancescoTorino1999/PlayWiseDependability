import { useContext, useEffect, useState } from "react";
import { CONSTANTS } from "../constants";
import Card from "../components/Card/Card";
import HeroBanner from "../components/HeroBanner/HeroBanner";
import { LanguageContext } from "../Contexts/LanguageProvider";
import TableSteamGames from "../components/TableSteamGames/TableSteamGames";
import Carusel from "../components/Carusel/Carusel";

function Home() {

    const { language, setLanguage } = useContext(LanguageContext);

    return (
        <>
            <HeroBanner
                    title={language == CONSTANTS.lang_it ? CONSTANTS.IT.HOMEPAGE.HERO_BANNER_TITLE : CONSTANTS.EN.HOMEPAGE.HERO_BANNER_TITLE }
                    subtitle={language == CONSTANTS.lang_it ? CONSTANTS.IT.HOMEPAGE.HERO_BANNER_SUBTITLE : CONSTANTS.EN.HOMEPAGE.HERO_BANNER_SUBTITLE }
                    buttonText={language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SCOPRI_DI_PIU_LABEL : CONSTANTS.EN.ALLPAGES.SCOPRI_DI_PIU_LABEL }
                    buttonLink="/about"
                    backgroundImage="https://media.gq-magazine.co.uk/photos/645b5c3c8223a5c3801b8b26/16:9/w_2560%2Cc_limit/100-best-games-hp-b.jpg"
                />
            <div className="page">
                                
                <div className='main-container'>

                    <div className='custom-row'>

                        <Carusel title = {"Giochi del momento"} page = {1} size={8} sort = {"releaseDate,desc"}></Carusel>

                    </div>
                    <div className='custom-row'>

                        <Carusel title = {"Migliori giochi secondo Metacritic"} page = {1} size={8} sort = {"metaScore,desc"}></Carusel>

                    </div>
                    <div className='custom-row'>

                        <Carusel title = {"Migliori giochi secondo gli utenti"} page = {1} size={8} sort = {"userScore,desc"}></Carusel>

                    </div>
                    <div className='custom-row'>

                        <Carusel title = {"Retro game"} page = {1} size={8} sort = {"releaseDate,asc"}></Carusel>

                    </div>
                    <div className='custom-row'>

                        <Carusel title = {"Consigliati in base alle tue recensioni"} page = {1} size={8} sort = {"userScore,desc"}></Carusel>

                    </div>


                </div>
            </div>
        </>

    )
}

export default Home;