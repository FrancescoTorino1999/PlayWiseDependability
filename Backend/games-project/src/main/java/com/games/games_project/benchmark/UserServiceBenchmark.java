package com.games.games_project.benchmark;

import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.model.User;
import com.games.games_project.repositories.UserRepository;
import com.games.games_project.service.impl.UserServiceImpl;
import org.openjdk.jmh.annotations.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.All)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(8)
public class UserServiceBenchmark {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    private User sampleUser;
    private RegistrationRequestDto registrationRequest;
    private LoginRequestDto loginRequest;
    private String rawPassword;
    private String encodedPassword;

    @Setup(Level.Trial)
    public void setup() throws Exception {

        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl();

        // Inject mocked repository
        var f1 = UserServiceImpl.class.getDeclaredField("userRepository");
        f1.setAccessible(true);
        f1.set(userService, userRepository);

        encoder = new BCryptPasswordEncoder(4);

        rawPassword = "pwd_" + UUID.randomUUID().toString().substring(0, 6);
        encodedPassword = encoder.encode(rawPassword);

        sampleUser = new User();
        sampleUser.setId("u1");
        sampleUser.setUsername("user_" + UUID.randomUUID().toString().substring(0, 6));
        sampleUser.setPassword(encodedPassword);
        sampleUser.setEmail("sample@example.com");
        sampleUser.setRole("USER");

        registrationRequest = new RegistrationRequestDto();
        registrationRequest.setUsername(sampleUser.getUsername());
        registrationRequest.setPassword(rawPassword);
        registrationRequest.setEmail("sample@example.com");
        registrationRequest.setName("Mario");
        registrationRequest.setSurname("Rossi");
        registrationRequest.setGender("M");

        loginRequest = new LoginRequestDto();
        loginRequest.setUsername(sampleUser.getUsername());
        loginRequest.setPassword(rawPassword);
    }

    @Setup(Level.Iteration)
    public void resetStubs() {

        reset(userRepository);

        doReturn(Optional.of(sampleUser))
                .when(userRepository)
                .findByUsername(loginRequest.getUsername());

        doReturn(Optional.empty())
                .when(userRepository)
                .findByEmailAndUsername(anyString(), anyString());

        doReturn(sampleUser)
                .when(userRepository)
                .save(any(User.class));

        doReturn(Optional.of(sampleUser))
                .when(userRepository)
                .findByUsername(sampleUser.getUsername());
    }

    @Benchmark
    public LoginResponseDto benchmarkLogin() {
        return userService.login(loginRequest);
    }

    @Benchmark
    public Boolean benchmarkRegister() {
        return userService.register(registrationRequest);
    }

    @Benchmark
    public Boolean benchmarkUpdateUser() {
        User updated = new User();
        updated.setUsername(sampleUser.getUsername());
        updated.setEmail("newmail@example.com");
        updated.setName("Giovanni");
        updated.setSurname("Bianchi");
        return userService.updateUser(updated);
    }

    @Benchmark
    public boolean benchmarkPasswordMatch() {
        return encoder.matches(rawPassword, encodedPassword);
    }

    @Benchmark
    public String benchmarkPasswordEncode() {
        return encoder.encode(rawPassword);
    }
}
