package com.games.games_project.service.impl;

import com.games.games_project.dto.*;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private GameRepository gameRepository;
    @InjectMocks private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private String randomUsername() {
        return "user_" + UUID.randomUUID();
    }

    private String randomPassword() {
        return "pwd_" + UUID.randomUUID();
    }

    /* ===================== LOGIN ===================== */

    @Test
    @DisplayName("login - utente valido con password cifrata")
    void testLogin_Success() {
        String username = randomUsername();
        String rawPassword = randomPassword();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginResponseDto result = userService.login(new LoginRequestDto(username, rawPassword));

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("USER", result.getRole());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    @DisplayName("login - credenziali errate")
    void testLogin_WrongPassword() {
        String username = randomUsername();
        String correctPassword = randomPassword();
        String wrongPassword = randomPassword();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(correctPassword));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertNull(userService.login(new LoginRequestDto(username, wrongPassword)));
    }

    @Test
    @DisplayName("login - utente non trovato")
    void testLogin_UserNotFound() {
        String username = randomUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertNull(userService.login(new LoginRequestDto(username, randomPassword())));
    }

    @Test
    @DisplayName("login - request nulla o campi null")
    void testLogin_NullCases() {
        assertNull(userService.login(null));
        assertNull(userService.login(new LoginRequestDto(null, randomPassword())));
        assertNull(userService.login(new LoginRequestDto(randomUsername(), null)));
    }

    /* ===================== REGISTRAZIONE ===================== */

    @Test
    @DisplayName("register - nuovo utente valido")
    void testRegister_Success() {
        String username = randomUsername();
        String password = randomPassword();

        RegistrationRequestDto reg = new RegistrationRequestDto();
        reg.setUsername(username);
        reg.setPassword(password);
        reg.setEmail(username + "@mail.com");
        reg.setName("Marco");
        reg.setSurname("Verdi");
        reg.setGender("M");
        reg.setBirthDate(new Date());

        when(userRepository.findByEmailAndUsername(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Boolean result = userService.register(reg);

        assertTrue(result);

        verify(userRepository).save(argThat(u ->
                u.getUsername().equals(username) &&
                        u.getEmail().equals(username + "@mail.com") &&
                        u.getRole().equals("USER") &&
                        u.getName().equals("Marco") &&
                        u.getSurname().equals("Verdi") &&
                        u.getGender().equals("M") &&
                        u.getBirthDate().equals(reg.getBirthDate()) &&
                        passwordEncoder.matches(password, u.getPassword())
        ));
    }


    @Test
    @DisplayName("register - utente già esistente")
    void testRegister_AlreadyExists() {
        String username = randomUsername();
        RegistrationRequestDto reg = new RegistrationRequestDto();
        reg.setUsername(username);
        reg.setPassword(randomPassword());
        reg.setEmail(username + "@gmail.com");

        when(userRepository.findByEmailAndUsername(reg.getEmail(), username))
                .thenReturn(Optional.of(new User()));

        assertFalse(userService.register(reg));
    }

    @Test
    @DisplayName("register - request nulla o campi null")
    void testRegister_NullCases() {
        assertFalse(userService.register(null));

        RegistrationRequestDto r1 = new RegistrationRequestDto();
        r1.setPassword(randomPassword());
        r1.setEmail("e");
        assertFalse(userService.register(r1));

        RegistrationRequestDto r2 = new RegistrationRequestDto();
        r2.setUsername("u");
        r2.setEmail("e");
        assertFalse(userService.register(r2));

        RegistrationRequestDto r3 = new RegistrationRequestDto();
        r3.setUsername("u");
        r3.setPassword(randomPassword());
        assertFalse(userService.register(r3));
    }

    /* ===================== USER INFO ===================== */

    @Test
    @DisplayName("getUserInfo - utente esistente")
    void testGetUserInfo_Existing() {
        String username = randomUsername();

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@gmail.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserInfo(username);

        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(username, result.getUsername());
    }

    @Test
    @DisplayName("getUserInfo - username vuoto")
    void testGetUserInfo_Empty() {
        User result = userService.getUserInfo("");
        assertNotNull(result);
        assertNull(result.getUsername());
    }

    /* ===================== UPDATE ===================== */

    @Test
    @DisplayName("updateUser - aggiorna dati utente")
    void testUpdateUser_Success() {
        String username = randomUsername();
        User existing = new User();
        existing.setUsername(username);
        existing.setEmail("old@gmail.com");

        User update = new User();
        update.setUsername(username);
        update.setEmail("new@gmail.com");
        update.setName("Allegra");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("new@gmail.com") &&
                        u.getName().equals("Allegra")));
    }

    @Test
    @DisplayName("updateUser - utente non trovato")
    void testUpdateUser_NotFound() {
        String username = randomUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        User user = new User();
        user.setUsername(username);

        assertFalse(userService.updateUser(user));
    }

    /* ===================== DELETE ===================== */

    @Test
    @DisplayName("deleteUser - aggiorna giochi e cancella recensioni")
    void testDeleteUser_Success() {
        String username = randomUsername();
        User user = new User();
        user.setUsername(username);

        Review r1 = new Review();
        r1.setId("rev1");
        r1.setAuthor(username);
        r1.setScore(8);
        r1.setGameId(new ObjectId("6807a1905d04121deaab7da0"));

        Review r2 = new Review();
        r2.setId("rev2");
        r2.setAuthor(username);
        r2.setScore(7);
        r2.setGameId(new ObjectId("6807a1905d04121deaab7da6"));

        Page<Review> reviewPage = new PageImpl<>(List.of(r1, r2));

        Game g1 = new Game();
        g1.setId("6807a1905d04121deaab7da0");
        g1.setUserScore(80.0);
        g1.setReviewCount(10);

        Game g2 = new Game();
        g2.setId("6807a1905d04121deaab7da6");
        g2.setUserScore(70.0);
        g2.setReviewCount(5);

        when(reviewRepository.findByAuthor(eq(username), any(Pageable.class))).thenReturn(reviewPage);
        when(gameRepository.findById(g1.getId())).thenReturn(Optional.of(g1));
        when(gameRepository.findById(g2.getId())).thenReturn(Optional.of(g2));

        assertTrue(userService.deleteUser(user));

        verify(reviewRepository, times(2)).deleteById(anyString());
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(userRepository).deleteByUsername(username);
    }

    @Test
    @DisplayName("deleteUser - gioco con una sola recensione")
    void testDeleteUser_SingleReview() {
        String username = randomUsername();
        User user = new User();
        user.setUsername(username);

        Review review = new Review();
        review.setId("rev1");
        review.setAuthor(username);
        review.setScore(9);
        review.setGameId(new ObjectId("6807a1905d04121deaab7da1"));

        Game singleGame = new Game();
        singleGame.setId("6807a1905d04121deaab7da1");
        singleGame.setUserScore(9.0);
        singleGame.setReviewCount(1);

        when(reviewRepository.findByAuthor(eq(username), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));
        when(gameRepository.findById(singleGame.getId())).thenReturn(Optional.of(singleGame));

        assertTrue(userService.deleteUser(user));
        assertEquals(0.0, singleGame.getUserScore());
        assertEquals(0, singleGame.getReviewCount());
    }

    /* ===================== STATISTICHE ===================== */

    @Test
    @DisplayName("getUserCountByGender - conteggio corretto")
    void testGetUserCountByGender() {
        GenderCountDto male = new GenderCountDto("M", 1L);
        GenderCountDto female = new GenderCountDto("F", 5L);
        when(userRepository.countUsersByGender()).thenReturn(List.of(male, female));

        List<GenderCountDto> result = userService.getUserCountByGender();

        assertEquals(2, result.size());
        assertEquals("M", result.get(0).getGender());
        assertEquals("F", result.get(1).getGender());
    }

    /* ===================== UPDATE EDGE CASES ===================== */

    @Test
    @DisplayName("updateUser - tutti i campi valorizzati → aggiorna tutto")
    void testUpdateUser_AllFieldsFilled() {
        String username = randomUsername();
        String newPassword = randomPassword();

        User existing = new User();
        existing.setUsername(username);
        existing.setEmail("old@mail.com");
        existing.setName("Old");
        existing.setSurname("User");
        existing.setPassword(passwordEncoder.encode(randomPassword()));

        User update = new User();
        update.setUsername(username);
        update.setEmail("new@mail.com");
        update.setName("NewName");
        update.setSurname("NewSurname");
        update.setPassword(newPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals(username) &&
                        saved.getEmail().equals("new@mail.com") &&
                        saved.getName().equals("NewName") &&
                        saved.getSurname().equals("NewSurname") &&
                        passwordEncoder.matches(newPassword, saved.getPassword())));
    }

    @Test
    @DisplayName("updateUser - tutti i campi vuoti → nessun aggiornamento")
    void testUpdateUser_AllFieldsEmpty() {
        String username = randomUsername();
        String oldPassword = randomPassword();

        User existing = new User();
        existing.setUsername(username);
        existing.setEmail("old@mail.com");
        existing.setName("Old");
        existing.setSurname("User");
        existing.setPassword(passwordEncoder.encode(oldPassword));

        User update = new User();
        update.setUsername(username);
        update.setEmail("");
        update.setName("");
        update.setSurname("");
        update.setPassword("");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals(username) &&
                        saved.getEmail().equals("old@mail.com") &&
                        saved.getName().equals("Old") &&
                        saved.getSurname().equals("User") &&
                        passwordEncoder.matches(oldPassword, saved.getPassword())));
    }


    @Test
    @DisplayName("updateUser - tutti i campi null → nessun aggiornamento")
    void testUpdateUser_AllFieldsNull() {
        String username = randomUsername();
        String oldPassword = randomPassword();

        User existing = new User();
        existing.setUsername(username);
        existing.setEmail("old@mail.com");
        existing.setName("Old");
        existing.setSurname("User");
        existing.setPassword(passwordEncoder.encode(oldPassword));

        User update = new User();
        update.setUsername(username);
        update.setEmail(null);
        update.setName(null);
        update.setSurname(null);
        update.setPassword(null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals(username) &&
                        saved.getEmail().equals("old@mail.com") &&
                        saved.getName().equals("Old") &&
                        saved.getSurname().equals("User") &&
                        passwordEncoder.matches(oldPassword, saved.getPassword())));
    }


    @Test
    @DisplayName("updateUser - combinazioni miste di aggiornamento")
    void testUpdateUser_MixedConditions() {
        String username = randomUsername();
        String newPassword = randomPassword();

        User existing = new User();
        existing.setUsername(username);
        existing.setEmail("old@mail.com");
        existing.setName("OldName");
        existing.setSurname("OldSurname");
        existing.setPassword(passwordEncoder.encode(randomPassword()));

        User update = new User();
        update.setUsername(username);
        update.setEmail(" ");
        update.setName("NewName");
        update.setSurname(null);
        update.setPassword(newPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals(username) &&
                        saved.getEmail().equals("old@mail.com") &&
                        saved.getName().equals("NewName") &&
                        saved.getSurname().equals("OldSurname") &&
                        passwordEncoder.matches(newPassword, saved.getPassword())));
    }

    @Test
    @DisplayName("deleteUser - aggiorna correttamente media con più recensioni")
    void testDeleteUser_UpdateAverageProperly() {
        String username = randomUsername();
        User user = new User();
        user.setUsername(username);

        Review review = new Review();
        review.setId("revZ");
        review.setAuthor(username);
        review.setScore(9);
        review.setGameId(new ObjectId("6807a1905d04121deaab7da9"));

        Game game = new Game();
        game.setId("6807a1905d04121deaab7da9");
        game.setUserScore(7.5);
        game.setReviewCount(3);

        when(reviewRepository.findByAuthor(eq(username), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        assertTrue(userService.deleteUser(user));

        double expectedNewAvg = Math.round(((7.5 * 3) - 9) / (3 - 1));
        assertEquals(expectedNewAvg, game.getUserScore());
        assertEquals(2, game.getReviewCount());
    }

    @Test
    @DisplayName("updateUser - aggiorna anche lo username")
    void testUpdateUser_UpdateUsername() {
        String oldUsername = randomUsername();
        String newUsername = randomUsername();

        User existing = new User();
        existing.setUsername(oldUsername);

        User update = new User();
        update.setUsername(newUsername);

        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.of(existing));

        assertTrue(userService.updateUser(update));

        verify(userRepository).save(argThat(u -> u.getUsername().equals(newUsername)));
    }
}
