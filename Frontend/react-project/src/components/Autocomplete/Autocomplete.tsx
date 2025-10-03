import { useState } from "react";
import { Link } from "react-router-dom";
import { CONSTANTS } from "../../constants";
import "./Autocomplete.scss";
import { ENV } from "../../env";

function Autocomplete({language}) {
    const [query, setQuery] = useState("");
    const [suggestions, setSuggestions] = useState([]);

    const fetchSuggestions = async (value) => {
        if (value.length < 2) {
            setSuggestions([]);
            return;
        }

        try {
            const response = await fetch(`${ ENV.ENVIRONMENT }/games/games/autocomplete/${value}`);
            const data = await response.json();
            setSuggestions(data);
        } catch (error) {
            console.error("Errore nel recupero suggerimenti:", error);
        }
    };

    return (
        <div className="search">
            <input
                className="input-search"
                type="text"
                value={query}
                onChange={(e) => {
                    setQuery(e.target.value);
                    fetchSuggestions(e.target.value);
                }}
                placeholder={language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SEARCH : CONSTANTS.EN.ALLPAGES.SEARCH}
            />
            <ul className="list-results">
                {suggestions.map((item, index) => (
                    <li key={index}><a href={`/game/${item.id}`}>{item.title}</a></li>
                ))}
            </ul>
        </div>
    );
}

export default Autocomplete;