import React, { useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.scss';
import Navbar from './components/Navbar/Navbar';
import Home from './Pages/Home';
import AboutUs from './Pages/AboutUs';
import Footer from './components/Footer/Footer';
import { CONSTANTS } from './constants';
import { LanguageContext, LanguageProvider } from './Contexts/LanguageProvider';
import { UserProvider } from './Contexts/UserProvider'; 
import Profile from './Pages/Profile';
import GamePage from './Pages/GamePage';
import Catalogue from './Pages/Catalogue';

function App() {

  return (
    <LanguageProvider>
      <UserProvider>
        <BrowserRouter>
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<AboutUs />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/game/:id" element={<GamePage />} />
            <Route path="/catalogue" element={<Catalogue />} />
          </Routes>
          <Footer />
        </BrowserRouter>
      </UserProvider>
    </LanguageProvider>
  );
}

export default App;
