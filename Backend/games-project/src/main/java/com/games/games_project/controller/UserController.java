package com.games.games_project.controller;

import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.dto.UserRequestDto;
import com.games.games_project.model.User;
import com.games.games_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.games.games_project.utils.ConverterDTO.convertToEntity;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = userService.login(loginRequest);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
    }

    @PostMapping("/deleteUser")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteUser(@RequestBody UserRequestDto user) {
        User user1 = convertToEntity(user);
        Boolean response = userService.deleteUser(user1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateUser")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> updateUser(@RequestBody UserRequestDto user) {
        User user1 = convertToEntity(user);
        Boolean response = userService.updateUser(user1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> register(@RequestBody RegistrationRequestDto registrationRequest) {
        Boolean response = userService.register(registrationRequest);
        if (response) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }
    }

    @PostMapping("/getUserInfo")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<User> getUserInfo(@RequestBody UserRequestDto user) {
        User response = userService.getUserInfo(user.getUsername());
        return ResponseEntity.ok(response);
    }
}
