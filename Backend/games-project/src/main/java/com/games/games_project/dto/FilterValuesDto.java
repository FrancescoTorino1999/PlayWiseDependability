package com.games.games_project.dto;

import java.util.Set;
import java.util.TreeSet;

public class FilterValuesDto {
    private Set<String> ratings = new TreeSet<>();
    private Set<String> genres = new TreeSet<>();
    private Set<String> developers = new TreeSet<>();
    private Set<String> publishers = new TreeSet<>();
    private Set<String> themes = new TreeSet<>();
    private Set<String> platforms = new TreeSet<>();
    private String minReleaseDate;
    private String maxReleaseDate;
    private Double minMetaScore;
    private Double maxMetaScore;
    private Double minUserScore;
    private Double maxUserScore;

    public Set<String> getRatings() {
        return ratings;
    }

    public void setRatings(Set<String> ratings) {
        this.ratings = ratings != null ? ratings : new TreeSet<>();
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres != null ? genres : new TreeSet<>();
    }

    public Set<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<String> developers) {
        this.developers = developers != null ? developers : new TreeSet<>();
    }

    public Set<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(Set<String> publishers) {
        this.publishers = publishers != null ? publishers : new TreeSet<>();
    }

    public Set<String> getThemes() {
        return themes;
    }

    public void setThemes(Set<String> themes) {
        this.themes = themes != null ? themes : new TreeSet<>();
    }

    public Set<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<String> platforms) {
        this.platforms = platforms != null ? platforms : new TreeSet<>();
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
