//@ non_null_by_default
package com.games.games_project.service.impl;

import com.games.games_project.dto.PegiResponseDto;
import com.games.games_project.geneticalgorithm.GeneticEngine;
import com.games.games_project.geneticalgorithm.PegiFeatureExtractor;
import com.games.games_project.geneticalgorithm.PegiFeatures;
import com.games.games_project.geneticalgorithm.PegiLevel;
import com.games.games_project.geneticalgorithm.PegiRiskCalculator;
import com.games.games_project.geneticalgorithm.PegiExplanationGenerator;
import com.games.games_project.service.PegiClassifierService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service that estimates PEGI level for a given text.
 */
@Service
public class PegiClassifierServiceImpl implements PegiClassifierService {

    // Composition over inheritance: delegate responsibilities to dedicated components
    private final GeneticEngine geneticEngine = new GeneticEngine();
    private final PegiFeatureExtractor featureExtractor = new PegiFeatureExtractor();
    private final PegiRiskCalculator riskCalculator = new PegiRiskCalculator();
    private final PegiExplanationGenerator explanationGenerator = new PegiExplanationGenerator();

    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    @Override
    public PegiResponseDto estimatePegiDetailed(String text) {
        PegiFeatures f = featureExtractor.extract(text);
        double[] weights = geneticEngine.evolve();
        double risk = riskCalculator.compute(f, weights);
        PegiLevel level = toPegi(risk, text);

        List<String> reasons = explain(f, level);

        Map<String, Double> map = Map.of(
                "violence", f.violence(),
                "fear", f.fear(),
                "sexual", f.sexual(),
                "addiction", f.addiction(),
                "language", f.language(),
                "positivity", f.positivity(),
                "ratio", f.ratio()
        );

        PegiResponseDto dto = new PegiResponseDto();
        dto.setPegiLevel(level.getValue());
        dto.setReasoning(reasons);
        dto.setFeatures(map);
        return dto;
    }

    // Package-private so the test in the same package can invoke it
    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    PegiLevel toPegi(double score, String text) {
        String lower = text.toLowerCase(Locale.ROOT);

        // Language short-circuit (kept aligned with existing tests)
        if (lower.contains("fuck") || lower.contains("shit") || lower.contains("bastard")) {
            return PegiLevel.PEGI_16;
        }

        if (score < 0.7)  return PegiLevel.PEGI_3;
        if (score < 1.3)  return PegiLevel.PEGI_7;
        if (score < 2.4)  return PegiLevel.PEGI_12;
        if (score < 2.8)  return PegiLevel.PEGI_16;
        return PegiLevel.PEGI_18;
    }

    // Package-private so il test può esercitare direttamente le soglie
    //@ requires f != null && lvl != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    List<String> explain(PegiFeatures f, PegiLevel lvl) {
        // Delega alla classe dedicata per tenere il service snello e facilitare i test unitari
        return explanationGenerator.generate(f, lvl);
    }
}
