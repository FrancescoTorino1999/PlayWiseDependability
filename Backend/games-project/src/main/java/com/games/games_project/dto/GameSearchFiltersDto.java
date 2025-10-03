package com.games.games_project.dto;

import java.util.Date;
import java.util.List;

public class GameSearchFiltersDto {
    private List<String> ratings;
    private List<String> genres;
    private List<String> developers;
    private List<String> publishers;
    private List<String> themes;
    private List<String> platforms;
    private Double fromMetaScore;
    private Double toMetaScore;
    private Double fromUserScore;
    private Double toUserScore;
    private Date fromReleaseDate;
    private Date toReleaseDate;

    public GameSearchFiltersDto() {
    }

    public List<String> getRatings() {
        return ratings;
    }

    public void setRatings(List<String> ratings) {
        this.ratings = ratings;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public Double getFromMetaScore() {
        return fromMetaScore;
    }

    public void setFromMetaScore(Double fromMetaScore) {
        this.fromMetaScore = fromMetaScore;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public Double getToMetaScore() {
        return toMetaScore;
    }

    public void setToMetaScore(Double toMetaScore) {
        this.toMetaScore = toMetaScore;
    }

    public Double getToUserScore() {
        return toUserScore;
    }

    public void setToUserScore(Double toUserScore) {
        this.toUserScore = toUserScore;
    }

    public Double getFromUserScore() {
        return fromUserScore;
    }

    public void setFromUserScore(Double fromUserScore) {
        this.fromUserScore = fromUserScore;
    }

    public Date getFromReleaseDate() {
        return fromReleaseDate;
    }

    public void setFromReleaseDate(Date fromReleaseDate) {
        this.fromReleaseDate = fromReleaseDate;
    }

    public Date getToReleaseDate() {
        return toReleaseDate;
    }

    public void setToReleaseDate(Date toReleaseDate) {
        this.toReleaseDate = toReleaseDate;
    }
}
