package com.games.games_project.benchmark;

import com.games.games_project.dto.*;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.repositories.UserRepository;
import com.games.games_project.service.impl.UserServiceImpl;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class UserServiceBenchmark {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private GameRepository gameRepository;

    private User sampleUser;
    private Review sampleReview;
    private Game sampleGame;
    private PageImpl<Review> reviewPage;
    private RegistrationRequestDto registrationRequest;
    private LoginRequestDto loginRequest;
    private BCryptPasswordEncoder encoder;

    @Setup(Level.Trial)
    public void setup() {
        userRepository = mock(UserRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        gameRepository = mock(GameRepository.class);
        userService = new UserServiceImpl();
        userService.getClass();
        try {
            var f1 = UserServiceImpl.class.getDeclaredField("userRepository");
            var f2 = UserServiceImpl.class.getDeclaredField("reviewRepository");
            var f3 = UserServiceImpl.class.getDeclaredField("gameRepository");
            f1.setAccessible(true);
            f2.setAccessible(true);
            f3.setAccessible(true);
            f1.set(userService, userRepository);
            f2.set(userService, reviewRepository);
            f3.set(userService, gameRepository);
        } catch (Exception ignored) {}

        encoder = new BCryptPasswordEncoder();

        sampleUser = new User();
        sampleUser.setId("u1");
        sampleUser.setUsername("Francesco");
        sampleUser.setPassword(encoder.encode("password"));
        sampleUser.setEmail("francesco@example.com");
        sampleUser.setRole("USER");

        sampleGame = new Game();
        sampleGame.setId("g1");
        sampleGame.setTitle("Mock Game");
        sampleGame.setUserScore(8.0);
        sampleGame.setReviewCount(2);

        sampleReview = new Review();
        sampleReview.setId("r1");
        sampleReview.setAuthor("Francesco");
        sampleReview.setScore(7);
        sampleReview.setText("Mock review");
        sampleReview.setGameId(new ObjectId("6555abcd9876abcd1234abcd"));

        reviewPage = new PageImpl<>(List.of(sampleReview));

        registrationRequest = new RegistrationRequestDto();
        registrationRequest.setUsername("Francesco");
        registrationRequest.setPassword("password");
        registrationRequest.setEmail("francesco@example.com");
        registrationRequest.setName("Francesco");
        registrationRequest.setSurname("Torino");
        registrationRequest.setGender("M");

        loginRequest = new LoginRequestDto();
        loginRequest.setUsername("Francesco");
        loginRequest.setPassword("password");
    }

    @Benchmark
    public LoginResponseDto benchmarkLoginSuccess() {
        when(userRepository.findByUsername("Francesco")).thenReturn(Optional.of(sampleUser));
        return userService.login(loginRequest);
    }

    @Benchmark
    public LoginResponseDto benchmarkLoginFail() {
        when(userRepository.findByUsername("Francesco")).thenReturn(Optional.empty());
        return userService.login(loginRequest);
    }

    @Benchmark
    public Boolean benchmarkRegisterSuccess() {
        when(userRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.empty());
        return userService.register(registrationRequest);
    }

    @Benchmark
    public Boolean benchmarkRegisterDuplicate() {
        when(userRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.of(sampleUser));
        return userService.register(registrationRequest);
    }

    @Benchmark
    public User benchmarkGetUserInfo() {
        when(userRepository.findByUsername("Francesco")).thenReturn(Optional.of(sampleUser));
        return userService.getUserInfo("Francesco");
    }

    @Benchmark
    public Boolean benchmarkDeleteUser() {
        when(reviewRepository.findByAuthor(anyString(), any(Pageable.class))).thenReturn(reviewPage);
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(sampleGame));
        doNothing().when(userRepository).deleteByUsername(anyString());
        return userService.deleteUser(sampleUser);
    }

    @Benchmark
    public Boolean benchmarkUpdateUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(sampleUser));
        return userService.updateUser(sampleUser);
    }

    @Benchmark
    public List<GenderCountDto> benchmarkGetUserCountByGender() {
        when(userRepository.countUsersByGender()).thenReturn(List.of(new GenderCountDto("M", 10L)));
        return userService.getUserCountByGender();
    }
}
