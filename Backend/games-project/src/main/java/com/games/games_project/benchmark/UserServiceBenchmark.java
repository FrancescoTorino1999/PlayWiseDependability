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

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
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

        // Inietta mock repository
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

    // 1️⃣ login reale (incluso BCrypt.matches)
    @Benchmark
    public LoginResponseDto benchmarkLogin() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(sampleUser));
        return userService.login(loginRequest);
    }

    // 2️⃣ register reale (incluso BCrypt.encode + save)
    @Benchmark
    public Boolean benchmarkRegister() {
        when(userRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        return userService.register(registrationRequest);
    }

    // 3️⃣ updateUser simulato (senza password encoder, serve come baseline)
    @Benchmark
    public Boolean benchmarkUpdateUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        User updated = new User();
        updated.setUsername(sampleUser.getUsername());
        updated.setEmail("newmail@example.com");
        updated.setName("Giovanni");
        updated.setSurname("Bianchi");
        return userService.updateUser(updated);
    }

    // 4️⃣ misura diretta del costo BCryptPasswordEncoder.matches()
    @Benchmark
    public boolean benchmarkPasswordMatch() {
        return encoder.matches(rawPassword, encodedPassword);
    }

    // 5️⃣ misura diretta del costo BCryptPasswordEncoder.encode()
    @Benchmark
    public String benchmarkPasswordEncode() {
        return encoder.encode(rawPassword);
    }
}
