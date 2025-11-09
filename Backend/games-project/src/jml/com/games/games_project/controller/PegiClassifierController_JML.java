package com.games.games_project.controller;

import com.games.games_project.dto.PegiResponseDto;
import com.games.games_project.service.PegiClassifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

public class PegiClassifierController {

    @Autowired
    private PegiClassifierService service;

    //@ requires text != null;
    //@ ensures \result.getPegiLevel() == 3 || \result.getPegiLevel() == 7 || \result.getPegiLevel() == 12 || \result.getPegiLevel() == 16 || \result.getPegiLevel() == 18;
    @PostMapping("/pegi")
    public PegiResponseDto classify(@RequestBody String text) {
        return service.estimatePegiDetailed(text);
    }
}
