package com.games.games_project.service.impl;

import com.games.games_project.dto.*;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private GameRepository gameRepository;
    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("login - utente NuttyMan con credenziali corrette")
    void testLogin_Success() {
        User nutty = new User();
        nutty.setId("6807a1995d04121deaab8a8d");
        nutty.setUsername("NuttyMan");
        nutty.setPassword("pass123");
        nutty.setRole("USER");

        LoginRequestDto req = new LoginRequestDto("NuttyMan", "pass123");

        when(userRepository.findByUsername("NuttyMan")).thenReturn(Optional.of(nutty));

        LoginResponseDto result = userService.login(req);

        assertNotNull(result);
        assertEquals("NuttyMan", result.getUsername());
        assertEquals("USER", result.getRole());
    }


    @Test
    @DisplayName("login - credenziali errate restituisce null")
    void testLogin_WrongPassword() {
        User user = new User();
        user.setUsername("NuttyMan");
        user.setPassword("rightPass");

        LoginRequestDto req = new LoginRequestDto();
        req.setUsername("NuttyMan");
        req.setPassword("wrongPass");

        when(userRepository.findByUsername("NuttyMan")).thenReturn(Optional.of(user));

        assertNull(userService.login(req));
    }

    @Test
    @DisplayName("register - nuovo utente inserito correttamente")
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
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - utente già esistente restituisce false")
    void testRegister_AlreadyExists() {
        RegistrationRequestDto reg = new RegistrationRequestDto();
        reg.setUsername("NuttyMan");
        reg.setPassword("1234");
        reg.setEmail("NuttyMan@gmail.com");

        when(userRepository.findByEmailAndUsername("NuttyMan@gmail.com", "NuttyMan"))
                .thenReturn(Optional.of(new User()));

        assertFalse(userService.register(reg));
    }

    @Test
    @DisplayName("getUserInfo - utente esistente restituito")
    void testGetUserInfo_Existing() {
        User user = new User();
        user.setUsername("8tonystark8");
        user.setEmail("8tonystark8@gmail.com");

        when(userRepository.findByUsername("8tonystark8")).thenReturn(Optional.of(user));

        User result = userService.getUserInfo("8tonystark8");

        assertEquals("8tonystark8@gmail.com", result.getEmail());
    }

    @Test
    @DisplayName("updateUser - aggiorna dati utente esistente")
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setUsername("chix");
        existing.setEmail("chix@gmail.com");

        User update = new User();
        update.setUsername("chix");
        update.setEmail("newchix@gmail.com");
        update.setName("Allegra");

        when(userRepository.findByUsername("chix")).thenReturn(Optional.of(existing));

        Boolean result = userService.updateUser(update);

        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser - utente non esistente → false")
    void testUpdateUser_NotFound() {
        when(userRepository.findByUsername("fakeUser")).thenReturn(Optional.empty());
        User user = new User();
        user.setUsername("fakeUser");
        assertFalse(userService.updateUser(user));
    }

    @Test
    @DisplayName("deleteUser - cancella NuttyMan e aggiorna giochi")
    void testDeleteUser_Success() {
        User nutty = new User();
        nutty.setUsername("NuttyMan");

        Review r1 = new Review();
        r1.setId("rev1");
        r1.setAuthor("NuttyMan");
        r1.setScore(8);
        r1.setGameId(new ObjectId("6807a1905d04121deaab7da0")); // GTA V

        Review r2 = new Review();
        r2.setId("rev2");
        r2.setAuthor("NuttyMan");
        r2.setScore(7);
        r2.setGameId(new ObjectId("6807a1905d04121deaab7da6")); // BioShock

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
    @DisplayName("getUserCountByGender - ritorna conteggio corretto")
    void testGetUserCountByGender() {
        GenderCountDto male = new GenderCountDto("M", 1L);
        GenderCountDto female = new GenderCountDto("F", 5L);
        when(userRepository.countUsersByGender()).thenReturn(List.of(male, female));

        List<GenderCountDto> result = userService.getUserCountByGender();

        assertEquals(2, result.size());
        assertEquals("F", result.get(1).getGender());
        assertEquals(5L, result.get(1).getCount());
    }
}
