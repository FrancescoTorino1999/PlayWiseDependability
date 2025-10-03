import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./TableSteamGames.scss";
import { CONSTANTS } from "../../constants";
import { ENV } from "../../env";

function TableSteamGames() {
    const [games, setGames] = useState<any[]>([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    useEffect(() => {
        fetch(`${ ENV.ENVIRONMENT }/games?page=${page}&size=16`)
            .then(response => response.json())
            .then(data => {
                setGames(data.content || []); // Evita undefined
                setTotalPages(data.totalPages || 1);
            })
            .catch(error => console.error('Error fetching data:', error));
    }, [page]); 

    return (
        <div className="table-container">
            <table className="table_games">
                <thead>
                    <tr className = "table-row">
                        <th>Nome</th>
                        <th>Score</th>
                        <th>User Score</th>
                        <th>Indirizzo</th>
                        <th>Dettagli</th>
                    </tr>
                </thead>
                <tbody>
                    {games.map((game, index) => (
                        <tr className = "table-row" key={index}>
                            <td className="table-data">{game["title"]}</td>
                            <td className="table-data">{game["metaScore"]}</td>
                            <td className="table-data">{game["userScore"]}</td>
                            <td className="table-data"><img alt="" src={game["cover"]} /></td>
                            <td className="table-data">
                                <Link className="button-primary" to={`/user/${index}`}>Dettagli</Link>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <div className="pagination">
                <button disabled={page === 1} onClick={() => setPage(page - 1)}>&lt;</button>
                <span>Page {page} of {totalPages}</span>
                <button disabled={page === totalPages} onClick={() => setPage(page + 1)}>&gt;</button>
            </div>
        </div>
    );
}

export default TableSteamGames;
