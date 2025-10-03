import { useContext, useEffect, useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog/ConfirmDialog';
import { Link, useLocation } from 'react-router-dom'
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faBars, faTimes } from '@fortawesome/free-solid-svg-icons'
import { CONSTANTS } from "../constants";
import { LanguageContext } from '../Contexts/LanguageProvider';
import { useUser } from '../Contexts/UserProvider';
import HeroCarusel from '../components/HeroCarusel/HeroCarusel';
import HeroBanner from '../components/HeroBanner/HeroBanner';
import ProfileReview from '../components/ProfileReview/ProfileReview';
import { ENV } from '../env';
import { Pie } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Line } from 'react-chartjs-2';
import {CategoryScale, LinearScale, PointElement, LineElement, Title} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import { BarElement } from 'chart.js';
ChartJS.register(
    ArcElement,
    Tooltip,
    Legend,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    BarElement
);



function Profile() {

    const { language, setLanguage } = useContext(LanguageContext);
    const location = useLocation();
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [birthDate, setBirthDate] = useState('');
    const [gender, setGender] = useState('M');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [paths, setPaths] = useState([{ path: "", label: "" }]);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [usernameLogin, setUsernameLogin] = useState('');
    const [passwordLogin, setPasswordLogin] = useState('');
    const { usernameGlobal, updateUsername } = useUser();
    const [activeTab, setActiveTab] = useState("info");
    const [user, setUser] = useState<User | null>(null);
    const [isConfirmOpen, setIsConfirmOpen] = useState(false);
    const [confirmMessage, setConfirmMessage] = useState('');
    const [confirmAction, setConfirmAction] = useState<() => void>(() => {});
    const [userRole, setUserRole] = useState<string | null>(null);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [reviews, setReviews] = useState<ReviewFromApi[]>([]);

    const [genderStats, setGenderStats] = useState<{ gender: string; count: number }[]>([]);
    const [reviewsPerMonth, setReviewsPerMonth] = useState<{ year: number; month: number; count: number }[]>([]);
    const [platformStats, setPlatformStats] = useState<{ platform: string; count: number }[]>([]);



    interface ReviewFromApi {
        id: string;
        author: string;
        score: number;
        text: string;
        date: string;
        gameId: string;
        gameTitle: string;
        gameCover: string;
    }

    interface User {
        "id": string;
        "username": string;
        "email": string;
        "password": string;
        "gender": string;
        "birthDate": string;  
        "name": string;
        "surname": string;
      }

    const barColors = [
        'rgba(255, 99, 132, 0.7)',
        'rgba(54, 162, 235, 0.7)',
        'rgba(255, 206, 86, 0.7)',
        'rgba(75, 192, 192, 0.7)',
        'rgba(153, 102, 255, 0.7)'
    ];

    const modifyUser = () => {

        console.log("here")

        const user = {
          name,
          surname,
          birthDate,
          gender,
          username,
          email,
          password,
        };
    
        fetch(`${ ENV.ENVIRONMENT }/users/updateUser`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(user),
        })
          .then(response => response.json())
          .then(data => {
            console.log('Success:', data);

            window.location.reload();
          })
          .catch(error => {
            console.error('Error:', error);
          });
    };

    useEffect(() => {
        setPage(0);
        setReviews([]);
        fetchPage(0);
    }, [usernameGlobal]);

    useEffect(() => {
        const role = localStorage.getItem('userRole');
        setUserRole(role);
    }, []);

    useEffect(() => {
        const user = {
            username: usernameGlobal
        }
        fetch(`${ ENV.ENVIRONMENT }/users/getUserInfo`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Errore nel caricamento dei dati");
            }
            return response.json();
        })
        .then((data) => {
            setUser(data);
            console.log("here2")
            setName(data.name);
            setSurname(data.surname);
            setEmail(data.email);
            setBirthDate(data.birthDate);
            setUsername(data.username);
            setPassword(data.password);
        })
        .catch((err) => {
        });
        
    }, [usernameGlobal]);

    useEffect(() => {
        if (activeTab === 'stats') {
            fetch('http://localhost:8080/admin/stats/users-by-gender')
                .then(res => {
                    if (!res.ok) throw new Error('Errore nel fetch delle statistiche');
                    return res.json();
                })
                .then(data => setGenderStats(data))
                .catch(err => console.error(err));

            fetch('http://localhost:8080/admin/stats/reviews-per-month')
                .then(res => {
                    if (!res.ok) throw new Error('Errore fetch reviews-per-month');
                    return res.json();
                })
                .then((data: { year: number; month: number; count: number }[]) => {
                    data.sort((a, b) => a.year - b.year || a.month - b.month);

                    const now = new Date();
                    const twelveMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 11, 1);

                    const recent = data.filter(r => {
                        const d = new Date(r.year, r.month - 1, 1);
                        return d >= twelveMonthsAgo && d <= now;
                    });

                    setReviewsPerMonth(recent);
                })
                .catch(err => console.error(err));

            fetch('http://localhost:8080/admin/stats/games-by-platform')
                .then(res => {
                    if (!res.ok) throw new Error('Errore fetch games-by-platform');
                    return res.json();
                })
                .then((data: { platform: string; count: number }[]) => {
                    // ordino per count discendente e prendo le prime N
                    const N = 5;  // regola qui quante piattaforme mostrare
                    data.sort((a, b) => b.count - a.count);
                    setPlatformStats(data.slice(0, N));
                })
                .catch(err => console.error(err));
        }
    }, [activeTab]);

    const deleteUser = () => {
        const user = {
            username: usernameGlobal
        }
        fetch(`${ ENV.ENVIRONMENT }/users/deleteUser`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Errore nel caricamento dei dati");
            }
            return response.json();
        })
        .then((data) => {    
            updateUsername('');
            localStorage.removeItem('authToken'); 
            window.location.href = "/";
        })
        .catch((err) => {
        });
    };

    const askModify = () => {
        const msg = language === CONSTANTS.lang_it
            ? 'Sei sicuro di voler salvare le modifiche al tuo profilo?'
            : 'Are you sure you want to save changes to your profile?';
        setConfirmMessage(msg);
        setConfirmAction(() => modifyUser);
        setIsConfirmOpen(true);
    };

    const askDelete = () => {
        const msg = language === CONSTANTS.lang_it
            ? 'Sei sicuro di voler cancellare il tuo account? Questa operazione è irreversibile.'
            : 'Are you sure you want to delete your account? This action is irreversible.';
        setConfirmMessage(msg);
        setConfirmAction(() => deleteUser);
        setIsConfirmOpen(true);
    };

    const fetchPage = (pageNumber: number) => {
        const body = { username: usernameGlobal };
        fetch(
            `${ENV.ENVIRONMENT}/games/games/reviewsByAuthor?sort=date,desc&page=${pageNumber}&size=5`,
            {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body),
            }
        )
            .then(res => {
                if (!res.ok) throw new Error('Errore fetch reviews');
                return res.json() as Promise<{
                    content: ReviewFromApi[];
                    page: number;
                    totalPages: number;
                }>;
            })
            .then(data => {
                setTotalPages(data.totalPages);
                if (pageNumber === 0) {
                    // prima pagina: sostituisci
                    setReviews(data.content);
                } else {
                    // carica ulteriori: appendi
                    setReviews(prev => [...prev, ...data.content]);
                }
            })
            .catch(err => console.error(err));
    };


    return (
        <div className="page">
            
        <div className="main-container">
        <HeroBanner
            title={language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.YOUR_ACCOUNT : CONSTANTS.EN.ALLPAGES.YOUR_ACCOUNT }
            subtitle=""
            buttonText={language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SCOPRI_DI_PIU_LABEL : CONSTANTS.EN.ALLPAGES.SCOPRI_DI_PIU_LABEL }
            buttonLink="/about"
            backgroundImage="../imgs/img-hero-home.jpg"
        />

            <div className="tabs">
                <button
                    className={activeTab === "info" ? "tab active" : "tab"}
                    onClick={() => setActiveTab("info")}
                >
                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.INFORMATION : CONSTANTS.EN.ALLPAGES.INFORMATION}
                </button>
                <button
                    className={activeTab === "reviews" ? "tab active" : "tab"}
                    onClick={() => setActiveTab("reviews")}
                >
                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.REVIEWS : CONSTANTS.EN.ALLPAGES.REVIEWS}
                </button>
                <button
                    className={activeTab === "settings" ? "tab active" : "tab"}
                    onClick={() => setActiveTab("settings")}
                >
                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SETTINGS : CONSTANTS.EN.ALLPAGES.SETTINGS}
                </button>

                {userRole === 'ADMIN' && (
                    <button
                        className={activeTab === 'stats' ? 'tab active' : 'tab'}
                        onClick={() => setActiveTab('stats')}
                    >
                        {language === CONSTANTS.lang_it ? 'Statistiche' : 'Statistics'}
                    </button>
                )}
            </div>

        <div className="tab-content">
            {activeTab === "info" && (
                <div className="tab-panel">
                    <div className="custom-row">

                        <div className="div-game">

                            <div className="left">
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 53.977 53.977"
                                    width="100"
                                    height="100"
                                >
                                    <g>
                                        <circle fill="#010002" cx="21.839" cy="14.805" r="5.052"/>
                                        <polygon fill="#010002" points="31.488,3.711 33.035,1.401 30.314,0.275 12.985,6.195 14.158,9.631"/>
                                        <polygon fill="#010002" points="49.441,2.725 48.902,0 46.179,1.127 38.114,17.565 41.375,19.166"/>
                                        <path fill="#010002" d="M45.804,47.873c-5.871-3.949-11.725-7.967-17.744-11.689c0.05-0.502,0.023-1.021-0.1-1.539
                                            c-0.011-0.037-0.786-3.363-1.271-7.644c3.215-0.152,6.425-0.409,9.643-0.579c2.436-0.129,3.383-2.523,2.819-4.284l0.728-1.479
                                            l1.18,0.581l0.557-1.135l-4.3-2.11l-0.558,1.135l1.043,0.511L37.428,20.4c-0.313-0.092-0.662-0.135-1.053-0.115
                                            c-4.662,0.247-9.316,0.681-13.994,0.706c-0.31-0.036-0.628-0.041-0.95-0.013c-0.192,0.017-0.38,0.048-0.563,0.087
                                            c-2.319-0.572-4.637-1.145-6.955-1.717c-1.017-3.104-2.034-6.209-3.052-9.314l1.183-0.404l0.426,1.245l1.194-0.408
                                            l-1.55-4.532l-1.192,0.409l0.372,1.097L9.766,7.963c-2.028-1.837-6.103-0.224-5.062,2.956c1.242,3.786,2.483,7.574,3.725,11.362
                                            c0.79,2.416,2.575,2.668,4.717,3.198c1.353,0.334,2.705,0.667,4.059,1.001c0.419,4.508,1.184,8.312,1.494,9.734
                                            c-2.552,0.502-5.104,1.004-7.655,1.508c-1.563,0.308-2.683,2.248-2.168,3.754c1.17,3.433,2.265,6.892,3.361,10.349
                                            c1.193,3.759,7.124,2.187,5.926-1.59c-0.764-2.408-1.538-4.815-2.328-7.22c2.594-0.51,5.187-1.021,7.779-1.53
                                            c0.332-0.063,0.62-0.17,0.874-0.304c6.199,3.808,12.212,7.947,18.25,12.007C46.025,55.397,49.073,50.069,45.804,47.873z"/>
                                    </g>
                                </svg>
                                
                                
                            </div>
                            <div className="right">
                                <div className="">
                                    <div className="font-heading-xl fw-600 mb-80">
                                        {language === CONSTANTS.lang_it 
                                            ? CONSTANTS.IT.ALLPAGES.USER_PROFILE 
                                            : CONSTANTS.EN.ALLPAGES.USER_PROFILE}
                                    </div>
                                    <div className="information">                                            
                                        <div className = "font-heading-l fw-600" >
                                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERNAME : CONSTANTS.EN.ALLPAGES.USERNAME}
                                        </div>
                                        <div className = "" >
                                            {user?.username}
                                        </div>
                                    </div>
                                    <div className="information">                                            
                                        <div className = "font-heading-l fw-600" >
                                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.NAME : CONSTANTS.EN.ALLPAGES.NAME}
                                        </div>
                                        <div className = "" >
                                            {user?.name}
                                        </div>
                                    </div>
                                    <div className="information">                                            
                                        <div className = "font-heading-l fw-600" >
                                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LAST_NAME : CONSTANTS.EN.ALLPAGES.LAST_NAME}
                                        </div>
                                        <div className = "" >
                                            {user?.surname}
                                        </div>
                                    </div>
                                    <div className="information">                                            
                                        <div className = "font-heading-l fw-600" >
                                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.EMAIL : CONSTANTS.EN.ALLPAGES.EMAIL}
                                        </div>
                                        <div className = "" >
                                            {user?.email}
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                        </div>
                </div>
            )}

            {activeTab === "reviews" && (
                <div className="tab-panel">
                    <div className="tab-panel">
                        <div className="custom-row">

                            <div className="div-game">

                                <div className="left">
                                <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        viewBox="0 0 53.977 53.977"
                                        width="100"
                                        height="100"
                                    >
                                        <g>
                                            <circle fill="#010002" cx="21.839" cy="14.805" r="5.052"/>
                                            <polygon fill="#010002" points="31.488,3.711 33.035,1.401 30.314,0.275 12.985,6.195 14.158,9.631"/>
                                            <polygon fill="#010002" points="49.441,2.725 48.902,0 46.179,1.127 38.114,17.565 41.375,19.166"/>
                                            <path fill="#010002" d="M45.804,47.873c-5.871-3.949-11.725-7.967-17.744-11.689c0.05-0.502,0.023-1.021-0.1-1.539
                                                c-0.011-0.037-0.786-3.363-1.271-7.644c3.215-0.152,6.425-0.409,9.643-0.579c2.436-0.129,3.383-2.523,2.819-4.284l0.728-1.479
                                                l1.18,0.581l0.557-1.135l-4.3-2.11l-0.558,1.135l1.043,0.511L37.428,20.4c-0.313-0.092-0.662-0.135-1.053-0.115
                                                c-4.662,0.247-9.316,0.681-13.994,0.706c-0.31-0.036-0.628-0.041-0.95-0.013c-0.192,0.017-0.38,0.048-0.563,0.087
                                                c-2.319-0.572-4.637-1.145-6.955-1.717c-1.017-3.104-2.034-6.209-3.052-9.314l1.183-0.404l0.426,1.245l1.194-0.408
                                                l-1.55-4.532l-1.192,0.409l0.372,1.097L9.766,7.963c-2.028-1.837-6.103-0.224-5.062,2.956c1.242,3.786,2.483,7.574,3.725,11.362
                                                c0.79,2.416,2.575,2.668,4.717,3.198c1.353,0.334,2.705,0.667,4.059,1.001c0.419,4.508,1.184,8.312,1.494,9.734
                                                c-2.552,0.502-5.104,1.004-7.655,1.508c-1.563,0.308-2.683,2.248-2.168,3.754c1.17,3.433,2.265,6.892,3.361,10.349
                                                c1.193,3.759,7.124,2.187,5.926-1.59c-0.764-2.408-1.538-4.815-2.328-7.22c2.594-0.51,5.187-1.021,7.779-1.53
                                                c0.332-0.063,0.62-0.17,0.874-0.304c6.199,3.808,12.212,7.947,18.25,12.007C46.025,55.397,49.073,50.069,45.804,47.873z"/>
                                        </g>
                                    </svg>
                                    
                                </div>
                                <div className="right">
                                <div className="information">
                                    <div className="font-heading-xl fw-600 mb-80">
                                            {language === CONSTANTS.lang_it 
                                                ? CONSTANTS.IT.ALLPAGES.REVIEWS 
                                                : CONSTANTS.EN.ALLPAGES.REVIEWS}
                                        </div>
                                    </div>
                                    {reviews && (
                                        <div className="information">
                                            {reviews.map(rev => (
                                                <ProfileReview
                                                    key={rev.id}
                                                    review={rev}
                                                    language={language}
                                                />
                                            ))}
                                        </div>


                                    )}
                                </div>

                            </div>

                            </div>
                    </div>
                </div>
            )}

            {activeTab === "settings" && (
                <div className="tab-panel">
                    <div className="div-game">

                        <div className="left">
                        <svg
                                xmlns="http://www.w3.org/2000/svg"
                                viewBox="0 0 53.977 53.977"
                                width="100"
                                height="100"
                            >
                                <g>
                                    <circle fill="#010002" cx="21.839" cy="14.805" r="5.052"/>
                                    <polygon fill="#010002" points="31.488,3.711 33.035,1.401 30.314,0.275 12.985,6.195 14.158,9.631"/>
                                    <polygon fill="#010002" points="49.441,2.725 48.902,0 46.179,1.127 38.114,17.565 41.375,19.166"/>
                                    <path fill="#010002" d="M45.804,47.873c-5.871-3.949-11.725-7.967-17.744-11.689c0.05-0.502,0.023-1.021-0.1-1.539
                                        c-0.011-0.037-0.786-3.363-1.271-7.644c3.215-0.152,6.425-0.409,9.643-0.579c2.436-0.129,3.383-2.523,2.819-4.284l0.728-1.479
                                        l1.18,0.581l0.557-1.135l-4.3-2.11l-0.558,1.135l1.043,0.511L37.428,20.4c-0.313-0.092-0.662-0.135-1.053-0.115
                                        c-4.662,0.247-9.316,0.681-13.994,0.706c-0.31-0.036-0.628-0.041-0.95-0.013c-0.192,0.017-0.38,0.048-0.563,0.087
                                        c-2.319-0.572-4.637-1.145-6.955-1.717c-1.017-3.104-2.034-6.209-3.052-9.314l1.183-0.404l0.426,1.245l1.194-0.408
                                        l-1.55-4.532l-1.192,0.409l0.372,1.097L9.766,7.963c-2.028-1.837-6.103-0.224-5.062,2.956c1.242,3.786,2.483,7.574,3.725,11.362
                                        c0.79,2.416,2.575,2.668,4.717,3.198c1.353,0.334,2.705,0.667,4.059,1.001c0.419,4.508,1.184,8.312,1.494,9.734
                                        c-2.552,0.502-5.104,1.004-7.655,1.508c-1.563,0.308-2.683,2.248-2.168,3.754c1.17,3.433,2.265,6.892,3.361,10.349
                                        c1.193,3.759,7.124,2.187,5.926-1.59c-0.764-2.408-1.538-4.815-2.328-7.22c2.594-0.51,5.187-1.021,7.779-1.53
                                        c0.332-0.063,0.62-0.17,0.874-0.304c6.199,3.808,12.212,7.947,18.25,12.007C46.025,55.397,49.073,50.069,45.804,47.873z"/>
                                </g>
                            </svg>
                            
                        </div>
                        <div className="right">
                        <div className="information">
                            <div className="font-heading-xl fw-600 mb-80">
                                    {language === CONSTANTS.lang_it 
                                        ? CONSTANTS.IT.ALLPAGES.SETTINGS 
                                        : CONSTANTS.EN.ALLPAGES.SETTINGS}
                                </div>
                            </div>
                            {user &&
                                <div className="information">                                            
                                    <div >
                                        <div className="">
                                            <div className="information">                                            
                                                <div className = "font-heading-l fw-600" >
                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERNAME : CONSTANTS.EN.ALLPAGES.USERNAME}
                                                </div>
                                                <div className = "div-game" >
                                                <div className = "" >
                                                    {user?.username}
                                                </div>
                                                </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "font-heading-l fw-600" >
                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.NAME : CONSTANTS.EN.ALLPAGES.NAME}
                                                </div>
                                                <div className = "div-game" >
                                                    <div className='left'>
                                                    <input 
                                                        className="modify-info" 
                                                        type="text" 
                                                        value={name} 
                                                        onChange={(e) => setName(e.target.value)}
                                                    />
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "font-heading-l fw-600" >
                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LAST_NAME : CONSTANTS.EN.ALLPAGES.LAST_NAME}
                                                </div>
                                                <div className = "" >
                                                    <div className = "div-game" >
                                                        <div className='left'>
                                                        <input 
                                                            className="modify-info" 
                                                            type="text" 
                                                            value={surname}
                                                            onChange={(e) => setSurname(e.target.value)}
                                                        />                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "font-heading-l fw-600" >
                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.EMAIL : CONSTANTS.EN.ALLPAGES.EMAIL}
                                                </div>
                                                <div className = "" >
                                                    <div className = "div-game" >
                                                            <div className='left'>
                                                            <input 
                                                                className="modify-info" 
                                                                type="text" 
                                                                value={email} 
                                                                onChange={(e) => setEmail(e.target.value)}
                                                            />         
                                                        </div>
                                                        </div>
                                                    </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "font-heading-l fw-600" >
                                                    Password
                                                </div>
                                                <div className = "" >
                                                    <div className = "div-game" >
                                                            <div className='left'>
                                                            <input 
                                                                className="modify-info" 
                                                                type="text" 
                                                                value={password} 
                                                                onChange={(e) => setPassword(e.target.value)}
                                                            />         
                                                        </div>
                                                        </div>
                                                    </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "" >
                                                    <div className = "div-game" >
                                                            <div className='left'>
                                                                <a className = "button-primary" onClick={askModify}>

                                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.MODIFY : CONSTANTS.EN.ALLPAGES.MODIFY}

                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>
                                            </div>
                                            <div className="information">                                            
                                                <div className = "" >
                                                    <div className = "div-game" >
                                                            <div className='left'>
                                                                <a className = "button-primary-del" onClick={askDelete}>

                                                                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.DELETE_ACCOUNT : CONSTANTS.EN.ALLPAGES.DELETE_ACCOUNT}

                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            }
                        </div>

                        </div>
                </div>
            )}
            {activeTab === 'stats' && userRole === 'ADMIN' &&(
                <div className="tab-panel">
                    <div className="font-heading-xl fw-600 mb-80">
                        {language === CONSTANTS.lang_it
                            ? 'Statistiche PlayWise'
                            : 'PlayWise`s Statisthic'}
                    </div>
                    <div className="chart-container">
                        <Pie
                            data={{
                                labels: genderStats.map(s =>
                                    language === CONSTANTS.lang_it
                                        ? (s.gender === 'M' ? 'Maschi' : 'Femmine')
                                        : s.gender
                                ),
                                datasets: [{
                                    data: genderStats.map(s => s.count),
                                    backgroundColor: ['#36A2EB', '#FF6384'],
                                    borderColor: ['#FFFFFF', '#FFFFFF'],
                                    borderWidth: 2,
                                }],
                            }}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: {
                                        position: 'bottom',
                                        labels: {
                                            font: {
                                                size: 20,
                                            },
                                            padding: 20,
                                        },
                                    },
                                    title: {
                                        display: true, // attiva il titolo
                                        text: language === CONSTANTS.lang_it
                                            ? 'Distribuzione utenti per genere'
                                            : 'Users Distribution by Gender',
                                        font: {
                                            size: 18,
                                        },
                                    },
                                    tooltip: {
                                        enabled: true,
                                        titleFont: { size: 14 },
                                        bodyFont: { size: 12 },
                                    },
                                },
                            }}
                            redraw
                        />
                    </div>

                    <div className="chart-container">
                        <Line
                            data={{
                                labels: reviewsPerMonth.map(r => `${r.year}-${String(r.month).padStart(2,'0')}`),
                                datasets: [{
                                    label: language === CONSTANTS.lang_it
                                        ? 'Recensioni per mese'
                                        : 'Reviews per month',
                                    data: reviewsPerMonth.map(r => r.count),
                                    borderColor: '#36A2EB',
                                    backgroundColor: 'rgba(54,162,235,0.2)',
                                    pointBackgroundColor: '#FF6384',
                                    pointBorderColor: '#FFFFFF',
                                    fill: true,
                                    tension: 0.2,
                                    pointRadius: 4,
                                }]
                            }}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    title: {
                                        display: true,
                                        text: language === CONSTANTS.lang_it
                                            ? 'Recensioni Mensili'
                                            : 'Monthly Reviews',
                                        font: { size: 18 },
                                    },
                                    legend: {
                                        position: 'bottom',
                                        labels: { font: { size: 14 } },
                                    },
                                },
                                scales: {
                                    x: {
                                        title: {
                                            display: true,
                                            text: language === CONSTANTS.lang_it ? 'Mese' : 'Month',
                                            font: { size: 14 },
                                        }
                                    },
                                    y: {
                                        title: {
                                            display: true,
                                            text: language === CONSTANTS.lang_it
                                                ? 'Numero Recensioni'
                                                : 'Review Count',
                                            font: { size: 14 },
                                        }
                                    }
                                }
                            }}
                            redraw
                        />
                    </div>


                    <div className="chart-container">
                        <Bar
                            data={{
                                labels: platformStats.map(p => p.platform),
                                datasets: [{
                                    data: platformStats.map(p => p.count),
                                    backgroundColor: barColors,
                                    borderColor: barColors.map(color => color.replace('0.7', '1')),
                                    borderWidth: 1,
                                }]
                            }}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: {
                                        display: false  // Rimuove la legenda
                                    },
                                    title: {
                                        display: true,
                                        text: language === CONSTANTS.lang_it
                                            ? 'Top piattaforme'
                                            : 'Top platforms',
                                        font: { size: 18 }
                                    },
                                    tooltip: { enabled: true }
                                },
                                scales: {
                                    x: {
                                        title: {
                                            display: true,
                                            text: language === CONSTANTS.lang_it ? 'Piattaforma' : 'Platform',
                                            font: { size: 14 }
                                        }
                                    },
                                    y: {
                                        title: {
                                            display: true,
                                            text: language === CONSTANTS.lang_it ? 'Numero giochi' : 'Game count',
                                            font: { size: 14 }
                                        },
                                        beginAtZero: true
                                    }
                                }
                            }}
                            redraw
                        />
                    </div>
                </div>
            )}
        </div>

    </div>
            <ConfirmDialog
                isOpen={isConfirmOpen}
                message={confirmMessage}
                onConfirm={() => {
                    setIsConfirmOpen(false);
                    confirmAction();
                }}
                onCancel={() => setIsConfirmOpen(false)}
            />
    </div>
    );
}

export default Profile;