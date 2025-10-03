package com.games.games_project.controller;

import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.model.User;
import com.games.games_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = userService.login(loginRequest);

        if(response!=null){
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 Unauthorized
        }

    }

    @PostMapping("/deleteUser")
    public ResponseEntity<Boolean> deleteUser(@RequestBody User user) {

        Boolean response = userService.deleteUser(user);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/updateUser")
    public ResponseEntity<Boolean> updateUser(@RequestBody User user) {

        Boolean response = userService.updateUser(user);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody RegistrationRequestDto registrationRequest) {
        Boolean response = userService.register(registrationRequest);

        if(response){
            return ResponseEntity.ok(true); //200 OK
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false); //409 Conflict
        }
    }

    @PostMapping("/getUserInfo")
    public ResponseEntity<User> getUserInfo(@RequestBody User user) {

        User response = userService.getUserInfo(user.getUsername());

        return  ResponseEntity.ok(response);

    }
}