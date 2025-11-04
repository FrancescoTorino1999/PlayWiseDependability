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
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("login - utente valido con password cifrata")
    void testLogin_Success() {
        User nutty = new User();
        nutty.setId("6807a1995d04121deaab8a8d");
        nutty.setUsername("NuttyMan");
        nutty.setPassword(passwordEncoder.encode("pass123"));
        nutty.setRole("USER");

        LoginRequestDto req = new LoginRequestDto("NuttyMan", "pass123");

        when(userRepository.findByUsername("NuttyMan")).thenReturn(Optional.of(nutty));

        LoginResponseDto result = userService.login(req);

        assertNotNull(result);
        assertEquals("NuttyMan", result.getUsername());
        assertEquals("USER", result.getRole());
        assertEquals("6807a1995d04121deaab8a8d", result.getUserId());
    }

    @Test
    @DisplayName("login - credenziali errate")
    void testLogin_WrongPassword() {
        User user = new User();
        user.setUsername("NuttyMan");
        user.setPassword(passwordEncoder.encode("rightPass"));

        LoginRequestDto req = new LoginRequestDto("NuttyMan", "wrongPass");

        when(userRepository.findByUsername("NuttyMan")).thenReturn(Optional.of(user));

        assertNull(userService.login(req));
    }

    @Test
    @DisplayName("login - utente non trovato")
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        LoginRequestDto req = new LoginRequestDto("ghost", "1234");
        assertNull(userService.login(req));
    }

    @Test
    @DisplayName("login - request nulla o campi null")
    void testLogin_NullCases() {
        assertNull(userService.login(null));
        LoginRequestDto req1 = new LoginRequestDto(null, "1234");
        assertNull(userService.login(req1));
        LoginRequestDto req2 = new LoginRequestDto("user", null);
        assertNull(userService.login(req2));
    }

    @Test
    @DisplayName("register - nuovo utente valido")
    void testRegister_Success() {
        RegistrationRequestDto reg = new RegistrationRequestDto();
        reg.setUsername("NewUser");
        reg.setPassword("1234");
        reg.setEmail("newuser@gmail.com");
        reg.setName("Marco");
        reg.setSurname("Verdi");
        reg.setGender("M");

        when(userRepository.findByEmailAndUsername(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Boolean result = userService.register(reg);

        assertTrue(result);
        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("NewUser") &&
                        u.getEmail().equals("newuser@gmail.com") &&
                        u.getRole().equals("USER")));
    }

    @Test
    @DisplayName("register - utente già esistente")
    void testRegister_AlreadyExists() {
        RegistrationRequestDto reg = new RegistrationRequestDto();
        reg.setUsername("NuttyMan");
        reg.setPassword("1234");
        reg.setEmail("NuttyMan@gmail.com");
        reg.setBirthDate(new Date());

        when(userRepository.findByEmailAndUsername("NuttyMan@gmail.com", "NuttyMan"))
                .thenReturn(Optional.of(new User()));

        assertFalse(userService.register(reg));
    }

    @Test
    @DisplayName("register - request nulla o campi null")
    void testRegister_NullCases() {
        assertFalse(userService.register(null));
        RegistrationRequestDto r1 = new RegistrationRequestDto();
        r1.setPassword("123");
        r1.setEmail("e");
        assertFalse(userService.register(r1));
        RegistrationRequestDto r2 = new RegistrationRequestDto();
        r2.setUsername("u");
        r2.setEmail("e");
        assertFalse(userService.register(r2));
        RegistrationRequestDto r3 = new RegistrationRequestDto();
        r3.setUsername("u");
        r3.setPassword("p");
        assertFalse(userService.register(r3));
    }

    @Test
    @DisplayName("getUserInfo - utente esistente")
    void testGetUserInfo_Existing() {
        User user = new User();
        user.setUsername("8tonystark8");
        user.setEmail("8tonystark8@gmail.com");

        when(userRepository.findByUsername("8tonystark8")).thenReturn(Optional.of(user));

        User result = userService.getUserInfo("8tonystark8");

        assertEquals("8tonystark8@gmail.com", result.getEmail());
        assertEquals("8tonystark8", result.getUsername());
    }

    @Test
    @DisplayName("getUserInfo - username vuoto")
    void testGetUserInfo_Empty() {
        User result = userService.getUserInfo("");
        assertNotNull(result);
        assertNull(result.getUsername());
    }

    @Test
    @DisplayName("updateUser - aggiorna dati utente")
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setUsername("chix");
        existing.setEmail("old@gmail.com");

        User update = new User();
        update.setUsername("chix");
        update.setEmail("new@gmail.com");
        update.setName("Allegra");

        when(userRepository.findByUsername("chix")).thenReturn(Optional.of(existing));

        Boolean result = userService.updateUser(update);

        assertTrue(result);
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("new@gmail.com") && u.getName().equals("Allegra")));
    }

    @Test
    @DisplayName("updateUser - utente non trovato")
    void testUpdateUser_NotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        User user = new User();
        user.setUsername("ghost");
        assertFalse(userService.updateUser(user));
    }

    @Test
    @DisplayName("deleteUser - aggiorna giochi e cancella recensioni")
    void testDeleteUser_Success() {
        User nutty = new User();
        nutty.setUsername("NuttyMan");

        Review r1 = new Review();
        r1.setId("rev1");
        r1.setAuthor("NuttyMan");
        r1.setScore(8);
        r1.setGameId(new ObjectId("6807a1905d04121deaab7da0"));

        Review r2 = new Review();
        r2.setId("rev2");
        r2.setAuthor("NuttyMan");
        r2.setScore(7);
        r2.setGameId(new ObjectId("6807a1905d04121deaab7da6"));

        Page<Review> reviewPage = new PageImpl<>(List.of(r1, r2));

        Game gta = new Game();
        gta.setId("6807a1905d04121deaab7da0");
        gta.setTitle("Grand Theft Auto V");
        gta.setUserScore(77.0);
        gta.setReviewCount(1046);

        Game bioshock = new Game();
        bioshock.setId("6807a1905d04121deaab7da6");
        bioshock.setTitle("BioShock");
        bioshock.setUserScore(87.0);
        bioshock.setReviewCount(293);

        when(reviewRepository.findByAuthor(eq("NuttyMan"), any(Pageable.class))).thenReturn(reviewPage);
        when(gameRepository.findById("6807a1905d04121deaab7da0")).thenReturn(Optional.of(gta));
        when(gameRepository.findById("6807a1905d04121deaab7da6")).thenReturn(Optional.of(bioshock));

        Boolean result = userService.deleteUser(nutty);

        assertTrue(result);
        verify(reviewRepository, times(2)).deleteById(anyString());
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(userRepository).deleteByUsername("NuttyMan");
    }

    @Test
    @DisplayName("deleteUser - gioco con una sola recensione")
    void testDeleteUser_SingleReview() {
        User user = new User();
        user.setUsername("SoloUser");

        Review review = new Review();
        review.setId("rev1");
        review.setAuthor("SoloUser");
        review.setScore(9);
        review.setGameId(new ObjectId("6807a1905d04121deaab7da1"));

        Game singleGame = new Game();
        singleGame.setId("6807a1905d04121deaab7da1");
        singleGame.setUserScore(9.0);
        singleGame.setReviewCount(1);

        Page<Review> reviewPage = new PageImpl<>(List.of(review));

        when(reviewRepository.findByAuthor(eq("SoloUser"), any(Pageable.class))).thenReturn(reviewPage);
        when(gameRepository.findById("6807a1905d04121deaab7da1")).thenReturn(Optional.of(singleGame));

        Boolean result = userService.deleteUser(user);

        assertTrue(result);
        assertEquals(0.0, singleGame.getUserScore());
        assertEquals(0, singleGame.getReviewCount());
        verify(reviewRepository).deleteById("rev1");
    }

    @Test
    @DisplayName("getUserCountByGender - conteggio corretto")
    void testGetUserCountByGender() {
        GenderCountDto male = new GenderCountDto("M", 1L);
        GenderCountDto female = new GenderCountDto("F", 5L);
        when(userRepository.countUsersByGender()).thenReturn(List.of(male, female));

        List<GenderCountDto> result = userService.getUserCountByGender();

        assertEquals(2, result.size());
        assertEquals("M", result.get(0).getGender());
        assertEquals(1L, result.get(0).getCount());
        assertEquals("F", result.get(1).getGender());
        assertEquals(5L, result.get(1).getCount());
    }

    @Test
    @DisplayName("updateUser - tutti i campi valorizzati → aggiorna tutto")
    void testUpdateUser_AllFieldsFilled() {
        User existing = new User();
        existing.setUsername("oldUser");
        existing.setEmail("old@mail.com");
        existing.setName("Old");
        existing.setSurname("User");
        existing.setPassword("oldPass");

        User update = new User();
        update.setUsername("oldUser");
        update.setEmail("new@mail.com");
        update.setName("NewName");
        update.setSurname("NewSurname");
        update.setPassword("newPass");

        when(userRepository.findByUsername("oldUser")).thenReturn(Optional.of(existing));

        Boolean result = userService.updateUser(update);

        assertTrue(result);
        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals("oldUser") &&
                        saved.getEmail().equals("new@mail.com") &&
                        saved.getName().equals("NewName") &&
                        saved.getSurname().equals("NewSurname") &&
                        passwordEncoder.matches("newPass", saved.getPassword())
        ));
    }

    @Test
    @DisplayName("updateUser - tutti i campi null o vuoti → nessun aggiornamento")
    void testUpdateUser_AllFieldsNullOrEmpty() {
        User existing = new User();
        existing.setUsername("oldUser");
        existing.setEmail("old@mail.com");
        existing.setName("Old");
        existing.setSurname("User");
        existing.setPassword("pass123");

        User update = new User();
        update.setUsername("oldUser");
        update.setEmail("");
        update.setName(" ");
        update.setSurname(null);
        update.setPassword(null);

        when(userRepository.findByUsername("oldUser")).thenReturn(Optional.of(existing));

        Boolean result = userService.updateUser(update);

        assertTrue(result);
        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals("oldUser") &&
                        saved.getEmail().equals("old@mail.com") &&
                        saved.getName().equals("Old") &&
                        saved.getSurname().equals("User") &&
                        saved.getPassword().equals("pass123")
        ));
    }

    @Test
    @DisplayName("updateUser - ogni campo testato con combinazioni miste true/false")
    void testUpdateUser_MixedConditions() {
        User existing = new User();
        existing.setUsername("baseUser");
        existing.setEmail("old@mail.com");
        existing.setName("OldName");
        existing.setSurname("OldSurname");
        existing.setPassword("oldPass");

        User update = new User();
        update.setUsername("baseUser");
        update.setEmail(" ");
        update.setName("NewName");
        update.setSurname(null);
        update.setPassword("newPass");

        when(userRepository.findByUsername("baseUser")).thenReturn(Optional.of(existing));

        Boolean result = userService.updateUser(update);

        assertTrue(result);
        verify(userRepository).save(argThat(saved ->
                saved.getUsername().equals("baseUser") &&
                        saved.getEmail().equals("old@mail.com") &&
                        saved.getName().equals("NewName") &&
                        saved.getSurname().equals("OldSurname") &&
                        passwordEncoder.matches("newPass", saved.getPassword())
        ));
    }
}
