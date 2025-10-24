package com.games.games_project.controller.impl;

import com.games.games_project.controller.UserController;
import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.model.User;
import com.games.games_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        Mockito.reset(userService);
    }

    @Test
    @DisplayName("POST /users/login → autenticazione riuscita")
    void testLogin_Success() throws Exception {
        LoginResponseDto response = new LoginResponseDto(
                "6807a1995d04121deaab8a8d",
                "NuttyMan",
                "USER"
        );

        when(userService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "NuttyMan",
                              "password": "password123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NuttyMan"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /users/login → autenticazione fallita (401)")
    void testLogin_Fail() throws Exception {
        when(userService.login(any(LoginRequestDto.class))).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "FakeUser",
                              "password": "wrongpass"
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /users/register → registrazione riuscita")
    void testRegister_Success() throws Exception {
        when(userService.register(any(RegistrationRequestDto.class))).thenReturn(true);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "NewUser",
                              "password": "abc123",
                              "email": "newuser@gmail.com"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /users/register → registrazione fallita (409 Conflict)")
    void testRegister_Fail() throws Exception {
        when(userService.register(any(RegistrationRequestDto.class))).thenReturn(false);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "ExistingUser",
                              "password": "abc123",
                              "email": "existing@gmail.com"
                            }
                            """))
                .andExpect(status().isConflict())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("POST /users/deleteUser → eliminazione utente riuscita")
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/deleteUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"NuttyMan\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /users/updateUser → aggiornamento utente riuscito")
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"NuttyMan\",\"email\":\"updated@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /users/getUserInfo → restituisce informazioni utente")
    void testGetUserInfo() throws Exception {
        User user = new User();
        user.setUsername("NuttyMan");
        user.setEmail("nuttyman@gmail.com");
        user.setRole("USER");

        when(userService.getUserInfo("NuttyMan")).thenReturn(user);

        mockMvc.perform(post("/users/getUserInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"NuttyMan\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NuttyMan"))
                .andExpect(jsonPath("$.email").value("nuttyman@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
