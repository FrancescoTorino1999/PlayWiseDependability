import { CONSTANTS } from "../../constants";
import "./LeaveReview.scss"
import { Link } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { LanguageContext } from "../../Contexts/LanguageProvider";
import { useUser } from '../../Contexts/UserProvider';
import { ENV } from "../../env";

function LeaveReview({gameId}) {
    const { language, setLanguage } = useContext(LanguageContext);
    const { usernameGlobal, updateUsername } = useUser();
    const [value, setValue] = useState(50);
    const [text, setText] = useState("");

    const leaveReview = () => {

        const review = {
          author: usernameGlobal,
          score: value,
          date: new Date(),
          text: text,
          gameId: gameId
        };

        console.log(review)
    
        fetch(`${ ENV.ENVIRONMENT }/reviews/addReview`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(review),
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
    return (

        <>
        <div className="review-card">
                <div className="side-th">
                    <div className = "font-body-desktop-xl fw-600 font-heading-m side-th" >
                        <svg className="svg-new"
                        version="1.1" 
                        id="Layer_1" 
                        x="0px" 
                        y="0px" 
                        viewBox="0 0 200 200" 
                        enableBackground="new 0 0 200 200" 
                        xmlSpace="preserve"
                        >
                        <path
                            d="M135.832,140.848h-70.9c-2.9,0-5.6-1.6-7.4-4.5c-1.4-2.3-1.4-5.7,0-8.6l4-8.2c2.8-5.6,9.7-9.1,14.9-9.5  c1.7-0.1,5.1-0.8,8.5-1.6c2.5-0.6,3.9-1,4.7-1.3c-0.2-0.7-0.6-1.5-1.1-2.2c-6-4.7-9.6-12.6-9.6-21.1c0-14,9.6-25.3,21.5-25.3  c11.9,0,21.5,11.4,21.5,25.3c0,8.5-3.6,16.4-9.6,21.1c-0.5,0.7-0.9,1.4-1.1,2.1c0.8,0.3,2.2,0.7,4.6,1.3c3,0.7,6.6,1.3,8.4,1.5  c5.3,0.5,12.1,3.8,14.9,9.4l3.9,7.9c1.5,3,1.5,6.8,0,9.1C141.432,139.148,138.632,140.848,135.832,140.848z M100.432,62.648  c-9.7,0-17.5,9.6-17.5,21.3c0,7.4,3.1,14.1,8.2,18.1c0.1,0.1,0.3,0.2,0.4,0.4c1.4,1.8,2.2,3.8,2.2,5.9c0,0.6-0.2,1.2-0.7,1.6  c-0.4,0.3-1.4,1.2-7.2,2.6c-2.7,0.6-6.8,1.4-9.1,1.6c-4.1,0.4-9.6,3.2-11.6,7.3l-3.9,8.2c-0.8,1.7-0.9,3.7-0.2,4.8  c0.8,1.3,2.3,2.6,4,2.6h70.9c1.7,0,3.2-1.3,4-2.6c0.6-1,0.7-3.4-0.2-5.2l-3.9-7.9c-2-4-7.5-6.8-11.6-7.2c-2-0.2-5.8-0.8-9-1.6  c-5.8-1.4-6.8-2.3-7.2-2.5c-0.4-0.4-0.7-1-0.7-1.6c0-2.1,0.8-4.1,2.2-5.9c0.1-0.1,0.2-0.3,0.4-0.4c5.1-3.9,8.2-10.7,8.2-18  C117.932,72.248,110.132,62.648,100.432,62.648z"
                        />
                        </svg>
                        {usernameGlobal}
                    </div>
                    <div className={`tag-genre tag-genre-${Math.round(value / 10) * 10} font-heading-m fw-600`}>{value}</div>
                </div>    
                <div className="slider-container">
                    <input
                        type="range"
                        min="0"
                        max="100"
                        step="1"
                        value={value}
                        onChange={(e) => setValue(e.target.value)}
                        className="slider"
                    />
                    <div className="ticks">
                        {[...Array(11)].map((_, i) => (
                        <span key={i} className="tick" style={{ left: `${i * 10}%` }}>
                            {i * 10}
                        </span>
                        ))}
                    </div>
                </div>            
                <div className="font-heading-s fw-300">
                    <textarea value={text} onChange={(e) => setText(e.target.value)} className="text-area-review"></textarea>
                </div>
                <a className="button-primary btn-small" onClick={leaveReview}>
                    {language == CONSTANTS.lang_it ? CONSTANTS.IT.ALLPAGES.CONTINUE : CONSTANTS.EN.ALLPAGES.CONTINUE}
                </a>
                <div className="card-date font-heading-s fw-300">
                    {
                        new Date().toLocaleDateString("it-IT") 
                    }
                </div>
                
            </div>

        </>

    )

}

export default LeaveReview;