package com.games.games_project.service;

import com.games.games_project.dto.PegiResponseDto;

public interface PegiClassifierService {

    /*@
      requires text != null && text.length() > 0;
      ensures \result != null;
      ensures \result.getPegiLevel() == 3
            || \result.getPegiLevel() == 7
            || \result.getPegiLevel() == 12
            || \result.getPegiLevel() == 16
            || \result.getPegiLevel() == 18;
      assignable \nothing;
    @*/
    PegiResponseDto estimatePegiDetailed(String text);
}
