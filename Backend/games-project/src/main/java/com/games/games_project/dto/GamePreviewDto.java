package com.games.games_project.dto;

public class GamePreviewDto {
    private String id;
    private String title;
    private String cover;
    private Double metaScore;
    private Double userScore;

    public GamePreviewDto() {}

    public GamePreviewDto(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Double getMetaScore() {
        return metaScore;
    }

    public void setMetaScore(Double metaScore) {
        this.metaScore = metaScore;
    }

    public Double getUserScore() {
        return userScore;
    }

    public void setUserScore(Double userScore) {
        this.userScore = userScore;
    }
}
