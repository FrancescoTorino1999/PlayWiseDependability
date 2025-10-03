package com.games.games_project.dto;

public class GenderCountDto {
    private String gender;
    private long count;

    public GenderCountDto() {
    }

    public GenderCountDto(String gender, long count) {
        this.gender = gender;
        this.count = count;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
