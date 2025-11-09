package com.games.games_project.service.impl;

import com.games.games_project.dto.PegiResponseDto;
import com.games.games_project.geneticalgorithm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PegiClassifierServiceImplTest {

    private PegiClassifierServiceImpl service;
    private GeneticEngine mockGeneticEngine;
    private PegiFeatureExtractor mockExtractor;
    private PegiRiskCalculator mockRisk;

    @BeforeEach
    void setUp() {
        mockGeneticEngine = Mockito.mock(GeneticEngine.class);
        mockExtractor = Mockito.mock(PegiFeatureExtractor.class);
        mockRisk = Mockito.mock(PegiRiskCalculator.class);

        service = new PegiClassifierServiceImpl() {
            // dependency injection manuale
            {
                try {
                    var geField = PegiClassifierServiceImpl.class.getDeclaredField("geneticEngine");
                    geField.setAccessible(true);
                    geField.set(this, mockGeneticEngine);

                    var exField = PegiClassifierServiceImpl.class.getDeclaredField("featureExtractor");
                    exField.setAccessible(true);
                    exField.set(this, mockExtractor);

                    var rcField = PegiClassifierServiceImpl.class.getDeclaredField("riskCalculator");
                    rcField.setAccessible(true);
                    rcField.set(this, mockRisk);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testEstimatePegiDetailed_AllPaths() {
        PegiFeatures features = new PegiFeatures(0.02, 0.01, 0.01, 0.01, 0.01, 0.05, 1.0);
        Mockito.when(mockExtractor.extract("test")).thenReturn(features);
        Mockito.when(mockGeneticEngine.evolve()).thenReturn(new double[]{1,1,1,1,1,1,1});
        Mockito.when(mockRisk.compute(Mockito.eq(features), Mockito.any())).thenReturn(0.5);

        PegiResponseDto dto = service.estimatePegiDetailed("test");
        assertNotNull(dto);
        assertTrue(dto.getPegiLevel() >= 3);
        assertNotNull(dto.getFeatures());
        assertNotNull(dto.getReasoning());
        assertTrue(dto.getReasoning().contains("Contains violence"));
        assertEquals(7, dto.getFeatures().size());
    }

    @Test
    void testToPegi_AllLevels() throws Exception {
        var method = PegiClassifierServiceImpl.class.getDeclaredMethod("toPegi", double.class, String.class);
        method.setAccessible(true);
        var service = new PegiClassifierServiceImpl();

        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_3"),
                method.invoke(service, 0.5, "family friendly"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_7"),
                method.invoke(service, 1.0, "adventure fun"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_12"),
                method.invoke(service, 2.0, "some challenge"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_16"),
                method.invoke(service, 2.6, "mild violence"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_18"),
                method.invoke(service, 3.0, "extreme content"));

        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_16"),
                method.invoke(service, 0.3, "this has fuck language"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_16"),
                method.invoke(service, 0.3, "this has shit language"));
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_16"),
                method.invoke(service, 0.3, "this bastard hero says bad things"));
        // Boundary checks for toPegi score thresholds
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_7"),
                method.invoke(service, 0.7, "neutral text"));  // boundary for PEGI_3/7
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_12"),
                method.invoke(service, 1.3, "neutral text"));  // boundary for PEGI_7/12
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_16"),
                method.invoke(service, 2.4, "neutral text"));  // boundary for PEGI_12/16
        assertEquals(Enum.valueOf((Class<Enum>)method.getReturnType(), "PEGI_18"),
                method.invoke(service, 2.8, "neutral text"));  // boundary for PEGI_16/18

    }


    @Test
    void testExplain_AllBranches() throws Exception {
        var method = PegiClassifierServiceImpl.class.getDeclaredMethod("explain", PegiFeatures.class, PegiLevel.class);
        method.setAccessible(true);

        // Tutti i casi positivi
        PegiFeatures f = new PegiFeatures(0.02, 0.01, 0.01, 0.01, 0.01, 0.05, 1.0);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) method.invoke(service, f, PegiLevel.PEGI_18);
        assertTrue(result.size() > 1);
        assertTrue(result.stream().anyMatch(s -> s.contains("violence")));

        // Caso con nessun contenuto negativo (solo audience generale)
        PegiFeatures clean = new PegiFeatures(0, 0, 0, 0, 0, 0, 0);
        @SuppressWarnings("unchecked")
        List<String> neutral = (List<String>) method.invoke(service, clean, PegiLevel.PEGI_3);
        assertEquals(List.of("General audience"), neutral);
    }

    @Test
    void testIntegration_EndToEnd_ValuesConsistency() {
        // test realistico senza mock
        PegiClassifierServiceImpl realService = new PegiClassifierServiceImpl();
        PegiResponseDto dto = realService.estimatePegiDetailed("fun family friendly colorful");
        Map<String, Double> features = dto.getFeatures();

        assertNotNull(dto);
        assertTrue(dto.getPegiLevel() >= 3);
        assertEquals(7, features.size());
        assertTrue(features.containsKey("violence"));
        assertTrue(dto.getReasoning().size() >= 1);
    }

    @Test
    void testExplain_Boundaries() throws Exception {
        var service = new PegiClassifierServiceImpl();

        var ctor = Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures")
                .getDeclaredConstructor(double.class, double.class, double.class, double.class, double.class, double.class, double.class);
        ctor.setAccessible(true);

        Object features = ctor.newInstance(0.01, 0.005, 0.003, 0.005, 0.002, 0.02, 1.0);
        List<String> result = (List<String>)
                service.getClass().getDeclaredMethod("explain",
                                Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures"),
                                PegiLevel.class)
                        .invoke(service, features, PegiLevel.PEGI_12);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testExplain_EqualToBoundaries() throws Exception {
        // 1️⃣ Ottieni il costruttore della classe PegiFeatures
        Constructor<?> ctor = Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures")
                .getDeclaredConstructor(double.class, double.class, double.class, double.class, double.class, double.class, double.class);
        ctor.setAccessible(true);

        // 2️⃣ Crea un'istanza di PegiFeatures con i valori al limite
        Object features = ctor.newInstance(0.01, 0.005, 0.003, 0.005, 0.002, 0.02, 1.0);

        // 3️⃣ Ottieni il metodo "explain" da PegiClassifierServiceImpl
        Method m = PegiClassifierServiceImpl.class.getDeclaredMethod(
                "explain",
                Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures"),
                Class.forName("com.games.games_project.geneticalgorithm.PegiLevel")
        );
        m.setAccessible(true);

        // 4️⃣ Invoca il metodo "explain"
        @SuppressWarnings("unchecked")
        List<String> reasons = (List<String>) m.invoke(service, features,
                Enum.valueOf((Class<Enum>) Class.forName("com.games.games_project.geneticalgorithm.PegiLevel"), "PEGI_7"));

        // 5️⃣ Asserzioni
        assertNotNull(reasons);
        assertFalse(reasons.isEmpty());
    }

    @Test
    void testExplain_BoundaryCrossings() throws Exception {
        Constructor<?> ctor = Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures")
                .getDeclaredConstructor(double.class, double.class, double.class, double.class, double.class, double.class, double.class);
        ctor.setAccessible(true);

        Method m = PegiClassifierServiceImpl.class.getDeclaredMethod(
                "explain",
                Class.forName("com.games.games_project.geneticalgorithm.PegiFeatures"),
                Class.forName("com.games.games_project.geneticalgorithm.PegiLevel")
        );
        m.setAccessible(true);

        // PEGI_7 arbitrario
        Object pegi7 = Enum.valueOf(
                (Class<Enum>) Class.forName("com.games.games_project.geneticalgorithm.PegiLevel"),
                "PEGI_7"
        );

        // 1️⃣ appena sotto le soglie → lista vuota o quasi
        Object below = ctor.newInstance(0.0099, 0.0049, 0.0029, 0.0049, 0.0019, 0.0199, 1.0);
        @SuppressWarnings("unchecked")
        List<String> reasonsBelow = (List<String>) m.invoke(service, below, pegi7);
        assertTrue(reasonsBelow.contains("General audience"));

        // 2️⃣ appena sopra le soglie → tutte le frasi attivate
        Object above = ctor.newInstance(0.0101, 0.0051, 0.0031, 0.0051, 0.0021, 0.0201, 1.0);
        @SuppressWarnings("unchecked")
        List<String> reasonsAbove = (List<String>) m.invoke(service, above, pegi7);
        assertTrue(reasonsAbove.contains("Contains violence"));
        assertTrue(reasonsAbove.contains("Includes fear or horror elements"));
        assertTrue(reasonsAbove.contains("Sexual or adult content"));
        assertTrue(reasonsAbove.contains("References to addiction or drugs"));
        assertTrue(reasonsAbove.contains("Offensive language detected"));
        assertTrue(reasonsAbove.contains("Positive and family-friendly tone"));
    }


}
