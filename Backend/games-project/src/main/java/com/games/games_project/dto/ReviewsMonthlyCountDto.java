package com.games.games_project.dto;

public class ReviewsMonthlyCountDto {
    private int year;
    private int month;
    private long count;

    public ReviewsMonthlyCountDto() {
    }

    public ReviewsMonthlyCountDto(int year, long count, int month) {
        this.year = year;
        this.count = count;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
