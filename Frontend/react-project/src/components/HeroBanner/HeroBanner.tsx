import React from 'react';
import './HeroBanner.scss'; // Importa il file CSS per la stilizzazione
import { Link } from 'react-router-dom';

function HeroBanner({ title, subtitle, buttonText, buttonLink, backgroundImage }) {
    return (
        <div className="hero-banner" style={{ backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.6)), url(${backgroundImage})` }}>
            <div className="hero-content">
                <h1 className="hero-title font-heading-xxl">{title}</h1>
                <p className="hero-subtitle">{subtitle}</p>
            </div>
        </div>
    );
}

export default HeroBanner;
