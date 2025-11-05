package com.games.games_project.benchmark;

import com.games.games_project.dto.*;
import com.games.games_project.model.*;
import com.games.games_project.utils.ConverterDTO;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ConverterDTOBenchmark {

    private ReviewRequestDto reviewDto;
    private UserRequestDto userDto;
    private GameRequestDto gameDto;

    @Setup(Level.Trial)
    public void setup() {
        reviewDto = new ReviewRequestDto();
        reviewDto.setId("r1");
        reviewDto.setAuthor("user_" + UUID.randomUUID());
        reviewDto.setText("This is a test review");
        reviewDto.setScore(8);
        reviewDto.setDate(new Date());
        reviewDto.setGameId(new org.bson.types.ObjectId("6555abcd9876abcd1234abcd"));
        reviewDto.setUserId(new org.bson.types.ObjectId("6555abcd9876abcd1234abce"));
        reviewDto.setGameName("Mock Game");

        userDto = new UserRequestDto();
        userDto.setId("u1");
        userDto.setUsername("user_" + UUID.randomUUID());
        userDto.setEmail("user@example.com");
        userDto.setPassword("pwd_" + UUID.randomUUID());
        userDto.setName("Test");
        userDto.setSurname("User");
        userDto.setGender("M");
        userDto.setRole("USER");
        userDto.setBirthDate(new Date());

        gameDto = new GameRequestDto();
        gameDto.setId("g1");
        gameDto.setTitle("Benchmark Game");
        gameDto.setReleaseDate(new Date());
        gameDto.setRating("E");
        gameDto.setGenre("Action");
        gameDto.setDevelopers(List.of("Studio A"));
        gameDto.setPublishers(List.of("Publisher X"));
        gameDto.setThemes(List.of("Sci-Fi"));
        gameDto.setPlatforms(List.of("PC", "PS5"));
        gameDto.setMetaScore(85.0);
        gameDto.setMetaScoreCount(120.0);
        gameDto.setDescription("Sample description");
        gameDto.setStoryline("Sample storyline");
        gameDto.setSummary("Sample summary");
        gameDto.setCover("cover.png");
        gameDto.setVideo("video.mp4");
        gameDto.setUserScore(8.5);
        gameDto.setReviewCount(10);
        gameDto.setScreenshots(List.of("sc1.png", "sc2.png"));
    }

    @Benchmark
    public Review benchmarkConvertToReviewEntity() {
        return ConverterDTO.convertToEntity(reviewDto);
    }

    @Benchmark
    public User benchmarkConvertToUserEntity() {
        return ConverterDTO.convertToEntity(userDto);
    }

    @Benchmark
    public Game benchmarkConvertToGameEntity() {
        return ConverterDTO.convertToEntity(gameDto);
    }
}
