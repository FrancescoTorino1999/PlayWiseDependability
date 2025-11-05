package com.games.games_project.benchmark;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.model.User;
import com.games.games_project.repositories.UserRepository;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class UserRepositoryBenchmark {

    private UserRepository userRepository;
    private User sampleUser;
    private List<GenderCountDto> genderCounts;

    @Setup(Level.Trial)
    public void setup() {
        userRepository = mock(UserRepository.class);

        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID().toString());
        sampleUser.setUsername(randomUsername());
        sampleUser.setEmail(randomEmail());
        sampleUser.setPassword("encodedPassword");
        sampleUser.setGender("M");
        sampleUser.setRole("USER");

        genderCounts = List.of(new GenderCountDto("M", 10L), new GenderCountDto("F", 8L));
    }

    private String randomUsername() {
        return "user_" + UUID.randomUUID();
    }

    private String randomEmail() {
        return "email_" + UUID.randomUUID() + "@example.com";
    }

    @Benchmark
    public Optional<User> benchmarkFindByEmailAndUsername() {
        when(userRepository.findByEmailAndUsername(sampleUser.getEmail(), sampleUser.getUsername()))
                .thenReturn(Optional.of(sampleUser));
        return userRepository.findByEmailAndUsername(sampleUser.getEmail(), sampleUser.getUsername());
    }

    @Benchmark
    public Optional<User> benchmarkFindByUsername() {
        when(userRepository.findByUsername(sampleUser.getUsername()))
                .thenReturn(Optional.of(sampleUser));
        return userRepository.findByUsername(sampleUser.getUsername());
    }

    @Benchmark
    public Optional<User> benchmarkDeleteByUsername() {
        when(userRepository.deleteByUsername(sampleUser.getUsername()))
                .thenReturn(Optional.of(sampleUser));
        return userRepository.deleteByUsername(sampleUser.getUsername());
    }

    @Benchmark
    public List<GenderCountDto> benchmarkCountUsersByGender() {
        when(userRepository.countUsersByGender()).thenReturn(genderCounts);
        return userRepository.countUsersByGender();
    }
}
