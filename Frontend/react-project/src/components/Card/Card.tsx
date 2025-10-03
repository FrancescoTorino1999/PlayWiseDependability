import { CONSTANTS } from "../../constants";
import "./Card.scss"
import { Link } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { LanguageContext } from "../../Contexts/LanguageProvider";


function Card({game}) {

    const { language, setLanguage } = useContext(LanguageContext);


    return (

        <>
            <a href={`/game/${game.id}`}  className = {`game-card-${Math.round(game.metaScore / 10) * 10} card light-neutral-20 button-primary` }>

                <div className="game-card">
                    <img
                        src={game.cover} 
                        alt={game.cover} 
                        className="game-image" />
                    <div className = "font-body-desktop-xl fw-600" >{game.title}</div>
                    <div className="card-bottom">
                        <div className="tag-cont">
                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.METASCORE : CONSTANTS.EN.ALLPAGES.METASCORE}
                            <div
                            className={`tag-genre tag-genre-${Math.round(game.metaScore / 10) * 10}`}
                            >{game.metaScore}</div>
                        </div>
                        
                        <div className="tag-cont">
                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERSCORE : CONSTANTS.EN.ALLPAGES.USERSCORE}
                            <div
                            className={`tag-genre tag-genre-${Math.round(game.userScore / 10) * 10}`}
                            >{game.userScore}</div>
                        </div>
                    </div>
                    
                </div>

            </a>
        </>

    )

}

export default Card;