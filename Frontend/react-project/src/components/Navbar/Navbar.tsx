import { useContext, useEffect, useState } from 'react'
import './Navbar.scss'
import { Link, useLocation, useNavigate} from 'react-router-dom'
import axios from 'axios';
import ConfirmDialog from '../ConfirmDialog/ConfirmDialog';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faBars, faTimes } from '@fortawesome/free-solid-svg-icons'
import { CONSTANTS } from "../../constants";
import { LanguageContext } from '../../Contexts/LanguageProvider';
import { useUser } from '../../Contexts/UserProvider';
import Autocomplete from '../Autocomplete/Autocomplete';
import { ENV } from '../../env';

function Navbar() {

  const [isVisible, setIsVisible] = useState(false);
  const [isVisibleLogin, setIsVisibleLogin] = useState(false);
  const [isVisibleSignup, setIsVisibleSignup] = useState(false);
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
  const [feedback, setFeedback] = useState({ message: '', type: '' });
  const [showFeedback, setShowFeedback] = useState(false);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [confirmMessage, setConfirmMessage] = useState('');

  const { usernameGlobal, updateUsername } = useUser();

  const navigate = useNavigate();

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  }

  function handleLang(event: { target: { value: any; }; }) {
    setLanguage(event.target.value);
  }

  const toggleLogin = () => {
    if(isVisibleSignup)
      setIsVisibleSignup(!isVisibleSignup);
    setIsVisibleLogin(!isVisibleLogin);

    console.log("click")
  };

  const toggleSignUp = () => {
    if(isVisibleLogin)
      setIsVisibleSignup(!isVisibleLogin);
    setIsVisibleSignup(!isVisibleSignup);
    console.log("click")
  };

  const triggerFeedback = ({ message, type }) => {
    setFeedback({ message, type });
    setShowFeedback(true);
    setTimeout(() => setShowFeedback(false), 3000);
  };

  const login = () => {
    const user = { username: usernameLogin, password: passwordLogin };

    fetch(`${ENV.ENVIRONMENT}/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user),
    })
        .then(res => {
          if (res.ok) {
            return res.json().then(data => {
              localStorage.setItem('authToken', data.username);
              localStorage.setItem('userRole', data.role);
              localStorage.setItem('userId', data.userId);
              updateUsername(data.username);
              triggerFeedback({ message: 'Login avvenuto con successo!', type: 'success' });
            });
          } else {
            let msg;
            if (res.status === 401) {
              msg = 'Username o password errati';
            } else {
              msg = 'Login fallito, riprova';
            }
            triggerFeedback({ message: msg, type: 'error' });
          }
        })
        .catch(() => {
          triggerFeedback({ message: 'Errore di rete, riprova!', type: 'error' });
        });

    toggleLogin();
  };

  const logOut = () => {
    updateUsername('');
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    navigate('/');
  };

  const askLogout = () => {
    const msg =
        language === CONSTANTS.lang_it
            ? 'Sei sicuro di voler effettuare il logout?'
            : 'Are you sure you want to log out?';
    setConfirmMessage(msg);
    setIsConfirmOpen(true);
  };

  const handleConfirm = () => {
    setIsConfirmOpen(false);
    logOut();
  };

  const handleCancel = () => {
    setIsConfirmOpen(false);
  };


  const signup = () => {
    const user = { name, surname, birthDate, gender, username, email, password };

    fetch(`${ENV.ENVIRONMENT}/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user),
    })
        .then(res => {
          if (res.ok) {
            triggerFeedback({ message: 'Registrazione avvenuta con successo!', type: 'success' });
          } else {
            let msg;
            if (res.status === 409) {
              msg = 'Username o Email già registrati!';
            } else {
              msg = 'Registrazione Fallita!';
            }
            triggerFeedback({ message: msg, type: 'error' });
          }
        })
        .catch(() => {
          triggerFeedback({ message: 'Errore di rete, riprova!', type: 'error' });
        });
    toggleSignUp()
  };

  return (
    <>
      {/* feedback toast */}
      {showFeedback && (
          <div className={`feedback ${feedback.type}`}>
            {feedback.message}
          </div>
      )}
      <header>
        <div className="navbar light-neutral-10">
          <div>
            <Link to="/">
              <img className = "logo-img" src="../imgs/apple-touch-icon.png" alt="Logo"></img>
            </Link>
          </div>

          <div className="hamburger-menu" onClick={toggleMenu}>
            <FontAwesomeIcon icon={isMenuOpen ? faTimes : faBars} size="lg" />
          </div>

          <nav className={`nav-links ${isMenuOpen ? 'open' : ''}`}>
            {paths.map((path, index) => (
              <Link key={index} to={path.path} className={path.path === location.pathname ? 'active fw-600' : 'fw-500'} onClick={() => setIsMenuOpen(false)}>
                {path.label}
              </Link>
            ))}
            <Link className='link-navbar font-body-l' to = {"/catalogue"}>
              {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CATALOGUE : CONSTANTS.EN.ALLPAGES.CATALOGUE}
            </Link>
            <Autocomplete language = {language}></Autocomplete>
            <select id="lang" className = "select-lang" onChange={handleLang}>
              <option value={CONSTANTS.lang_it}>ITALIANO</option>
              <option value={CONSTANTS.lang_en}>ENGLISH</option>
            </select>
            {!usernameGlobal && (<a id = "log-in-button" className="button-primary" onClick={toggleLogin} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LOGIN : CONSTANTS.EN.ALLPAGES.LOGIN}</a>)}
            {!usernameGlobal && (<a className="button-primary" onClick={toggleSignUp} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SIGNUP : CONSTANTS.EN.ALLPAGES.SIGNUP}</a>)}
            {usernameGlobal && (<Link className="button-primary" to={"/profile"}  data-discover="true">{usernameGlobal}</Link>)}
            {usernameGlobal && (<a className="button-primary" onClick={askLogout} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LOGOUT : CONSTANTS.EN.ALLPAGES.LOGOUT}</a>)}
          </nav>
        </div>
      </header>
      {isVisibleLogin && (
        <div id="login-box" className="login-box">
          <h2>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LOGIN : CONSTANTS.EN.ALLPAGES.LOGIN}</h2>
          <form>
            <div className="user-box">
            <input
                  id="username"
                  type="text"
                  value={usernameLogin}
                  onChange={(e) => setUsernameLogin(e.target.value)}
                  required
                />
                <label>Username</label>
              </div>
            <div className="user-box">
            <input
                  id="password"
                  type="password"
                  value={passwordLogin}
                  onChange={(e) => setPasswordLogin(e.target.value)}
                  required
                />
                <label>Password</label>
            </div>
            <div className='side-boxes-login'>
              <a className="button-primary" onClick={login} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.LOGIN : CONSTANTS.EN.ALLPAGES.LOGIN}</a>
              <a className="button-primary" onClick={toggleLogin} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CLOSE : CONSTANTS.EN.ALLPAGES.CLOSE}</a>
            </div>
          </form>
        </div>
      )}
      {isVisibleSignup && (
        <div id="signup-box" className="login-box">
          <h2>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SIGNUP : CONSTANTS.EN.ALLPAGES.SIGNUP}</h2>
          <form>
          <div className="side-boxes">
            <div className="singleBox">
              <div className="user-box">
                <input
                  id="name"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
                <label>Name</label>
              </div>
              <div className="user-box">
                <input
                  id="surname"
                  type="text"
                  value={surname}
                  onChange={(e) => setSurname(e.target.value)}
                  required
                />
                <label>Last Name</label>
              </div>
              <div className="user-box-date">
                <label>Birth Date</label>
                <input
                  id="birthDate"
                  type="date"
                  value={birthDate}
                  onChange={(e) => setBirthDate(e.target.value)}
                  required
                />
              </div>
              <div className="user-box-date">
                <label>Gender</label>
                <select
                  id="gender"
                  onChange={(e) => setGender(e.target.value)}
                  required
                >
                  <option value="M">Male</option>
                  <option value="F">Female</option>
                  <option value="N">Not Specified</option>
                </select>
              </div>
            </div>
            <div className="singleBox">
              <div className="user-box">
                <input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
                <label>Username</label>
              </div>
              <div className="user-box">
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
                <label>Email</label>
              </div>
              <div className="user-box">
                <input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <label>Password</label>
              </div>
            </div>
          </div>
            <div className='side-boxes-login'>
              <a className="button-primary" onClick={signup} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SIGNUP : CONSTANTS.EN.ALLPAGES.SIGNUP}</a>
              <a className="button-primary" onClick={toggleSignUp} data-discover="true">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CLOSE : CONSTANTS.EN.ALLPAGES.CLOSE}</a>
            </div>
          </form>
        </div>
      )}
      <ConfirmDialog
          isOpen={isConfirmOpen}
          message={confirmMessage}
          onConfirm={handleConfirm}
          onCancel={handleCancel}
      />
    </>
  );
}

export default Navbar;
