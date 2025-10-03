package com.games.games_project.dto;

import java.util.Set;

public class FilterValuesDto {
    private Set<String> ratings;
    private Set<String> genres;
    private Set<String> developers;
    private Set<String> publishers;
    private Set<String> themes;
    private Set<String> platforms;
    private String minReleaseDate;
    private String maxReleaseDate;
    private Double minMetaScore;
    private Double maxMetaScore;
    private Double minUserScore;
    private Double maxUserScore;

    public FilterValuesDto() {
    }

    public Set<String> getRatings() {
        return ratings;
    }

    public void setRatings(Set<String> ratings) {
        this.ratings = ratings;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public Set<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<String> developers) {
        this.developers = developers;
    }

    public Set<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(Set<String> publishers) {
        this.publishers = publishers;
    }

    public Set<String> getThemes() {
        return themes;
    }

    public void setThemes(Set<String> themes) {
        this.themes = themes;
    }

    public Set<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<String> platforms) {
        this.platforms = platforms;
    }

    public String getMinReleaseDate() {
        return minReleaseDate;
    }

    public void setMinReleaseDate(String minReleaseDate) {
        this.minReleaseDate = minReleaseDate;
    }

    public String getMaxReleaseDate() {
        return maxReleaseDate;
    }

    public void setMaxReleaseDate(String maxReleaseDate) {
        this.maxReleaseDate = maxReleaseDate;
    }

    public Double getMinMetaScore() {
        return minMetaScore;
    }

    public void setMinMetaScore(Double minMetaScore) {
        this.minMetaScore = minMetaScore;
    }

    public Double getMaxMetaScore() {
        return maxMetaScore;
    }

    public void setMaxMetaScore(Double maxMetaScore) {
        this.maxMetaScore = maxMetaScore;
    }

    public Double getMinUserScore() {
        return minUserScore;
    }

    public void setMinUserScore(Double minUserScore) {
        this.minUserScore = minUserScore;
    }

    public Double getMaxUserScore() {
        return maxUserScore;
    }

    public void setMaxUserScore(Double maxUserScore) {
        this.maxUserScore = maxUserScore;
    }
}
