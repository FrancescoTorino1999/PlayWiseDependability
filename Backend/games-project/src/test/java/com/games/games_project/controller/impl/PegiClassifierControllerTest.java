package com.games.games_project.controller;

import com.games.games_project.dto.PegiResponseDto;
import com.games.games_project.service.PegiClassifierService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PegiClassifierControllerTest {

    @Mock
    private PegiClassifierService pegiClassifierService;

    @InjectMocks
    private PegiClassifierController controller;

    PegiClassifierControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("classify - restituisce DTO corretto con livello PEGI valido")
    void testClassifyReturnsValidDto() {
        PegiResponseDto mockDto = new PegiResponseDto();
        mockDto.setPegiLevel(16);
        mockDto.setReasoning(java.util.List.of("Contains violence"));
        mockDto.setFeatures(java.util.Map.of("violence", 0.2));

        when(pegiClassifierService.estimatePegiDetailed(anyString())).thenReturn(mockDto);

        String input = "Some violent adventure";
        PegiResponseDto result = controller.classify(input);

        assertNotNull(result);
        assertEquals(16, result.getPegiLevel());
        assertTrue(result.getReasoning().contains("Contains violence"));
        assertEquals(0.2, result.getFeatures().get("violence"));
        verify(pegiClassifierService).estimatePegiDetailed(input);
    }
}
