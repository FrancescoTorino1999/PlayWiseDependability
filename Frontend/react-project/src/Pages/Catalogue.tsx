import { useContext, useEffect, useState } from "react";
import { CONSTANTS } from "../constants";
import Card from "../components/Card/Card";
import HeroBanner from "../components/HeroBanner/HeroBanner";
import { LanguageContext } from "../Contexts/LanguageProvider";
import { ENV } from "../env";

function Catalogue() {

    const { language } = useContext(LanguageContext);
    const [games, setGames] = useState<any[]>([]);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(12);
    const [sort, setSort] = useState("desc");
    const [totalPages, setTotalPages] = useState(1);
    const [filters, setFilters] = useState<Filters[]>([]);
    const [selectedFilter, setelSectedFilter] = useState<Filters[]>([]);
    const [isOpen, setIsOpen] = useState(false);
    const [selectedFilters, setSelectedFilters] = useState({
        fromReleaseDate: "",
        toReleaseDate: "",
        genres: [] as string[],
        ratings: [] as string[],
        developers: [] as string[],
        publishers: [] as string[],
        themes: [] as string[],
        platforms: [] as string[],
        fromMetaScore: "",
        toMetaScore: "",
        fromUserScore: "",
        toUserScore: ""
    });

    interface Filters {
        releaseDate?: string;  
        rating?: string;
        genre?: string; 
        developers?: string[]; 
        publishers?: string[];
        themes?: string[];
        platforms?: string[]; 
        metaScore?: number;
        userScore?: number;
    }


    useEffect(() => {
        fetch(`${ ENV.ENVIRONMENT }/games/getFilters`)
            .then(response => response.json())
            .then(data => {
                setFilters(data || []);
            })
            .catch(error => console.error('Error fetching data:', error));
    }, []);

    useEffect(() => {
        console.log(selectedFilters)
        fetch(`${ ENV.ENVIRONMENT }/games/findFilteredGames?page=${page}&size=${size}&sort=${sort}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(selectedFilters),
        })
        .then(response => response.json())
                .then(data => {
                    setGames(data.content || []);
                    setTotalPages(data.totalPages || 1);
                    window.scrollTo(0, 0);
                })
    }, [page]);



    const filter = () => {
        if(page == 1) {
            fetch(`${ ENV.ENVIRONMENT }/games/findFilteredGames?page=${page}&size=${size}&sort=${sort}`, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify(selectedFilters),
            })
            .then(response => response.json())
                    .then(data => {
                        setGames(data.content || []);
                        setTotalPages(data.totalPages || 1);
                        window.scrollTo(0, 0);
                    })
        } else {
            setPage(1);
        }
        
    }

    const clear = () => {
        window.location.reload();
    }




    const handleFilterChange = (key: string, value: string | string[]) => {
        setSelectedFilters((prevFilters) => ({
            ...prevFilters,
            [key]: value
        }));
    };

    const handleMultiSelectChange = (key: string, event: React.ChangeEvent<HTMLSelectElement>) => {
        const values = Array.from(event.target.selectedOptions, (option) => option.value);
        handleFilterChange(key, values);
    };

    const applyFilters = () => {
        console.log("Filtri applicati:", selectedFilters);
        setIsOpen(false);
    };

    return (
        <>
            <HeroBanner
                title={language === CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CATALOGUE : CONSTANTS.EN.ALLPAGES.CATALOGUE}
                subtitle={""}
                buttonText={language === CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SCOPRI_DI_PIU_LABEL : CONSTANTS.EN.ALLPAGES.SCOPRI_DI_PIU_LABEL}
                buttonLink="/about"
                backgroundImage="https://media.gq-magazine.co.uk/photos/645b5c3c8223a5c3801b8b26/16:9/w_2560%2Cc_limit/100-best-games-hp-b.jpg"
            />
            
            <div className="page">
                <button className="accordion-header font-heading-xl fw-700 mb-30 align-left" onClick={() => setIsOpen(!isOpen)}>
                    {language === CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.FILTER : CONSTANTS.EN.ALLPAGES.FILTER}
                    <span>{isOpen ? "▲" : "▼"}</span>
                </button>
                
                {isOpen && 
                    <div className="filters-container">
                        <div className="filters mb-40">
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.DATE_FROM : CONSTANTS.EN.ALLPAGES.DATE_FROM }</label>
                                <input
                                    type="date"
                                    value={selectedFilters.fromReleaseDate}
                                    onChange={(e) => handleFilterChange("fromReleaseDate", e.target.value)}
                                />
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.DATE_TO : CONSTANTS.EN.ALLPAGES.DATE_TO }</label>
                                <input
                                    type="date"
                                    value={selectedFilters.toReleaseDate}
                                    onChange={(e) => handleFilterChange("toReleaseDate", e.target.value)}
                                />
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.GENERE : CONSTANTS.EN.ALLPAGES.GENERE }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("genres", e)}>
                                    {filters.genres?.map((filter: string, index: number) => (
                                        <option key={index} value={filter}>
                                            {filter}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.RATING : CONSTANTS.EN.ALLPAGES.RATING }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("ratings", e)}>
                                    {filters.ratings?.map((rating: string, index: number) => (
                                        <option key={index} value={rating}>
                                            {rating}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.DEVELOPERS : CONSTANTS.EN.ALLPAGES.DEVELOPERS }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("developers", e)}>
                                    {filters.developers?.map((developer: string, index: number) => (
                                        <option key={index} value={developer}>
                                            {developer}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.PUBLISHERS : CONSTANTS.EN.ALLPAGES.PUBLISHERS }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("publishers", e)}>
                                    {filters.publishers?.map((publisher: string, index: number) => (
                                        <option key={index} value={publisher}>
                                            {publisher}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.THEMES : CONSTANTS.EN.ALLPAGES.THEMES }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("themes", e)}>
                                    {filters.themes?.map((theme: string, index: number) => (
                                        <option key={index} value={theme}>
                                            {theme}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.PLATFORMS : CONSTANTS.EN.ALLPAGES.PLATFORMS }</label>
                                <select multiple onChange={(e) => handleMultiSelectChange("platforms", e)}>
                                    {filters.platforms?.map((platform: string, index: number) => (
                                        <option key={index} value={platform}>
                                            {platform}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.METASCORE_FROM : CONSTANTS.EN.ALLPAGES.METASCORE_FROM }</label>
                                <select onChange={(e) => handleFilterChange("fromMetaScore", e.target.value)}>
                                <option value="" disabled selected>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SELECT_VALUE : CONSTANTS.EN.ALLPAGES.SELECT_VALUE }</option> 
                                {Array.from({ length: 100 }, (_, i) => i + 1).map((value) => (
                                        <option key={value} value={value}>
                                            {value}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.METASCORE_TO : CONSTANTS.EN.ALLPAGES.METASCORE_TO }</label>
                                <select onChange={(e) => handleFilterChange("toMetaScore", e.target.value)}>
                                <option value="" disabled selected>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SELECT_VALUE : CONSTANTS.EN.ALLPAGES.SELECT_VALUE }</option> 
                                {Array.from({ length: 100 }, (_, i) => i + 1).map((value) => (
                                        <option key={value} value={value}>
                                            {value}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERSCORE_FROM : CONSTANTS.EN.ALLPAGES.USERSCORE_FROM }</label>
                                <select onChange={(e) => handleFilterChange("fromUserScore", e.target.value)}>
                                    <option value="" disabled selected>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SELECT_VALUE : CONSTANTS.EN.ALLPAGES.SELECT_VALUE }</option> 
                                    {Array.from({ length: 100 }, (_, i) => i + 1).map((value) => (
                                        <option key={value} value={value}>
                                            {value}
                                        </option>
                                    ))}
                                </select>
                            </div>
            
                            <div className="single-filter">
                                <label className="font-heading-m fw-600 mb-20">{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.USERSCORE_TO : CONSTANTS.EN.ALLPAGES.USERSCORE_TO }</label>
                                <select onChange={(e) => handleFilterChange("toUserScore", e.target.value)}>
                                <option value="" disabled selected>{language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.SELECT_VALUE : CONSTANTS.EN.ALLPAGES.SELECT_VALUE }</option> 
                                {Array.from({ length: 100 }, (_, i) => i + 1).map((value) => (
                                        <option key={value} value={value}>
                                            {value}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
        
                    <div className="central-filter-button">
                        <button className="button-primary font-heading-l" onClick={clear}>
                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CLEAN : CONSTANTS.EN.ALLPAGES.CLEAN }
                        </button>
                        <button className="button-primary font-heading-l" onClick={filter}>
                            {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.FILTER : CONSTANTS.EN.ALLPAGES.FILTER }
                        </button>
                    </div>
                </div>
                }
                
                <div className='main-container'>

                    <div className='custom-grid'>
                        {games.map((game, index) => (
                            <div key={index}>
                                <Card game={game} />
                            </div>
                        ))}
                    </div>

                    <div className="pagination">
                        <button  className="font-heading-s"
                            onClick={() => setPage(prev => Math.max(prev - 1, 1))} 
                            disabled={page === 1}
                        >
                            &lt;
                        </button>
                        <div className="font-heading-s">Pagina {page} di {totalPages}</div>
                        <button  className="font-heading-s"
                            onClick={() => setPage(prev => Math.min(prev + 1, totalPages))} 
                            disabled={page === totalPages}
                        >
                            &gt;
                        </button>
                    </div>

                </div>
            </div>
        </>
    )
}

export default Catalogue;