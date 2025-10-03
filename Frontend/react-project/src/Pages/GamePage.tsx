import { useParams } from "react-router-dom";
import Card from "../components/Card/Card";
import HeroCarusel from "../components/HeroCarusel/HeroCarusel";
import { CONSTANTS } from "../constants";
import { LanguageContext } from "../Contexts/LanguageProvider";
import { useContext, useEffect, useState } from "react";
import Review from "../components/Review/Review";
import { useUser } from '../Contexts/UserProvider';
import LeaveReview from "../components/LeaveReview/LeaveReview";
import Carusel from "../components/Carusel/Carusel";
import { ENV } from "../env";



function GamePage() {

    const { language, setLanguage } = useContext(LanguageContext);
    const { usernameGlobal, updateUsername } = useUser();

    interface Game {
        id: string;
        title: string;
        releaseDate: string;
        rating: string;
        genre: string;
        developers: string[];
        publishers: string[];
        themes: string[];
        platforms: string[];
        metaScore: number;
        metaScoreCount: number;
        description: string;
        storyline: string;
        summary: string;
        cover: string;
        video: string;
        userScore: number;
        reviewCount: number;
        screenshots: string[];
    }

    interface Reviews{
        "id": string;
        "author": string;
        "score": number;
        "text": string;
        "date": string;
    }
    
    const { id } = useParams();
    const [game, setGame] = useState<Game | null>(null);
    const [reviews, setReviews] = useState<Reviews[] | null>(null);
    const [userReview, setUserReview] = useState<Reviews | null | undefined>(undefined);

    const login = () => {
        document.getElementById("log-in-button")?.click();    
    };


    useEffect(() => {
        fetch(`${ ENV.ENVIRONMENT }/games/${id}`)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Errore nel caricamento dei dati");
                }
                return response.json();
            })
            .then((data) => {
                setGame(data);
            })
            .catch((err) => {
            });
            fetch(`${ ENV.ENVIRONMENT }/reviews/games/${id}/reviews?sort=date,desc`)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Errore nel caricamento dei dati");
                }
                return response.json();
            })
            .then((data) => {
                setReviews(data.content);
            })
            .catch((err) => {
            });
    }, [id]);

    useEffect(() => {
        // se non ho un utente loggato, setto subito null
        if (!usernameGlobal) {
            setUserReview(null);
            return;
        }

        fetch(`${ENV.ENVIRONMENT}/reviews/game/${id}/review?author=${usernameGlobal}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Nessuna recensione trovata");
                }
                return response.json();
            })
            .then(data => {
                setUserReview(data);
            })
            .catch(() => {
                setUserReview(null);
            });
    }, [id, usernameGlobal]);

    return (
        <div className="page">
            {game &&
                <div className="main-container">

                    <HeroCarusel screens = {game.screenshots}/>

                    <div className="custom-row">

                        <div className="div-game">

                            <div className="left">
                                <div className="title font-heading-desktop-xl font-heading-l fw-700">
                                    {game.title}
                                </div>
                                <img src = {game.cover} alt=""></img>
                                <div className="tag-container">
                                    {game.publishers.map((publisher, index) => (
                                        <div key={index} className="tag font-body-desktop-xl fw-500">
                                            {publisher}
                                        </div>
                                    ))}
                                </div>
                                <div className = "font-heading-s fw-400" >
                                        {
                                            language == CONSTANTS.lang_it ?
                                            new Date(game.releaseDate).toLocaleDateString("it-IT") :
                                            game.releaseDate
                                        }
                                </div>
                                <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.METASCORE : CONSTANTS.EN.ALLPAGES.METASCORE}
                                </div>
                                <div
                                className={`tag-genre tag-genre-${Math.round(game.metaScore / 10) * 10}  font-heading-l fw-700`}
                                >{game.metaScore}</div>
                                <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERSCORE : CONSTANTS.EN.ALLPAGES.USERSCORE}
                                </div>
                                <div
                                className={`tag-genre tag-genre-${Math.round(game.userScore / 10) * 10}  font-heading-l fw-700`}
                                >{game.userScore}</div>
                                <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.GENERE : CONSTANTS.EN.ALLPAGES.GENERE}
                                    </div>
                                <div className="tag-container">
                                    {game.themes.map((theme, index) => (
                                        <div key={index} className="tag font-body-desktop-xl fw-500">
                                            {theme}
                                        </div>
                                    ))}
                                </div>
                                <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.PLATFORMS : CONSTANTS.EN.ALLPAGES.PLATFORMS}
                                    </div>
                                <div className="tag-container">
                                    {game.platforms.map((platform, index) => (
                                        <div key={index} className="tag font-body-desktop-xl fw-500">
                                            {platform}
                                        </div>
                                    ))}
                                </div>
                            </div>
                            <div className="right">
                                <div className="information">
                                    <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.DESCRIPTION : CONSTANTS.EN.ALLPAGES.DESCRIPTION}
                                    </div>
                                    <div className = "font-heading-m fw-400" >
                                        {game.description}
                                    </div>
                                </div>
                                <div className="information">
                                    <div className = "font-heading-l fw-600" >
                                        {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.PLOT : CONSTANTS.EN.ALLPAGES.PLOT}
                                    </div>
                                    <div className = "font-heading-m fw-400" >
                                        {game.summary}
                                    </div>
                                </div>
                                <div className="information">
                                    <div className="font-heading-l fw-600">
                                        {language === CONSTANTS.lang_it 
                                            ? CONSTANTS.IT.ALLPAGES.VIDEO 
                                            : CONSTANTS.EN.ALLPAGES.VIDEO}
                                    </div>

                                    {game.video && (
                                        <div className="video-container">
                                            <iframe 
                                                src={game.video.replace("watch?v=", "embed/")} 
                                                title="Game Trailer" 
                                                frameBorder="0" 
                                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                                allowFullScreen
                                            ></iframe>
                                        </div>
                                    )}
                                </div>
                                {reviews &&
                                    <div className="information">
                                        <div className="font-heading-l fw-600">
                                            {language === CONSTANTS.lang_it 
                                                ? CONSTANTS.IT.ALLPAGES.REVIEWS 
                                                : CONSTANTS.EN.ALLPAGES.REVIEWS}
                                        </div>

                                        {usernameGlobal && userReview === undefined && (
                                            // opzionale: puoi mettere uno spinner o un testo
                                            <div>Sto controllando la tua recensione…</div>
                                        )}

                                        {usernameGlobal && userReview !== undefined && (
                                            userReview
                                                // 1) l'utente ha già recensito: mostro la sua recensione
                                                ? (
                                                    <div className="user-review-block">
                                                        <div className="font-heading-m fw-600">
                                                            {language === CONSTANTS.lang_it
                                                                ? "La tua recensione"
                                                                : "Your review"}
                                                        </div>
                                                        <Review review={userReview} language={language} />
                                                    </div>
                                                )
                                                // 2) l'utente non ha recensito: mostro il form
                                                : (
                                                    <LeaveReview gameId={game.id} />
                                                )
                                        )}

                                        {!usernameGlobal &&
                                            <div className="light-neutral-20 unauthorized-div">
                                                <div className="fw-500 font-heading-l align-text-center">
                                                    {language === CONSTANTS.lang_it 
                                                    ? CONSTANTS.IT.ALLPAGES.UNAUTHORIZED 
                                                    : CONSTANTS.EN.ALLPAGES.UNAUTHORIZED}
                                                </div>
                                                <a className="button-primary btn-small" onClick={login}>
                                                    {language === CONSTANTS.lang_it 
                                                    ? CONSTANTS.IT.ALLPAGES.LOGIN 
                                                    : CONSTANTS.EN.ALLPAGES.LOGIN}</a>
                                            </div>
                                        }

                                        {reviews
                                            .filter(rev => rev.author !== usernameGlobal)
                                            .map((rev, index) => (
                                                <div key={index}>
                                                    <Review review={rev} language={language} />
                                                </div>
                                            ))
                                        }

                                    </div>
                                }
                            </div>

                        </div>
                        
                    </div>
                </div>
                
            }
            <div className='custom-row'>

                <Carusel title = {"Consigliati in base alle tue recensioni"} page = {1} size={8} sort = {"userScore,desc"}></Carusel>

            </div>
        </div>
    );
}

export default GamePage;