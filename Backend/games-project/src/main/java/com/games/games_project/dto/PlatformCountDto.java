package com.games.games_project.dto;

public class PlatformCountDto {
    private String platform;
    private long count;

    public PlatformCountDto() {}

    public PlatformCountDto(String platform, long count) {
        this.platform = platform;
        this.count = count;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}