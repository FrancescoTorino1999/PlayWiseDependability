// src/components/ProfileReview/ProfileReview.tsx
import React from 'react';
import { Link } from 'react-router-dom';
import Review from '../Review/Review';
import type { ReviewFromApi } from '../../types'; // assicurati di avere il tipo corretto
import './ProfileReview.scss';

interface ProfileReviewProps {
    review: ReviewFromApi;
    language: string;
}

export default function ProfileReview({
                                          review,
                                          language,
                                      }: ProfileReviewProps) {
    return (
        <div className="profile-review-card">

            {/* HEADER: titolo + cover */}
            <div className="profile-review-header">
                <Link
                    to={`/game/${review.gameId}`}
                    className="profile-review-game-link"
                >
                    <h3 className="profile-review-game-title">
                        {review.gameTitle}
                    </h3>
                    {review.gameCover && (
                        <img
                            className="profile-review-game-cover"
                            src={
                                review.gameCover.startsWith('//')
                                    ? `https:${review.gameCover}`
                                    : review.gameCover
                            }
                            alt={review.gameTitle}
                        />
                    )}
                </Link>
            </div>

            {/* BODY: layout identico al Review “standard” */}
            <div className="profile-review-body">
                <Review review={review} language={language} />
            </div>
        </div>
    );
}
