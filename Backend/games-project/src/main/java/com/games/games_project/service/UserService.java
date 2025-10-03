package com.games.games_project.service;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.model.User;

import java.util.List;

public interface UserService {
    LoginResponseDto login(LoginRequestDto loginRequest);
    Boolean register(RegistrationRequestDto registrationRequest);
    User getUserInfo(String username);
    Boolean deleteUser(User user);
    Boolean updateUser(User user);
    List<GenderCountDto> getUserCountByGender();
}
