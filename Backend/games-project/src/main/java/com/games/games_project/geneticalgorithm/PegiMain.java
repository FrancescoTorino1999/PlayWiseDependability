package com.games.games_project.geneticalgorithm;

//@ non_null_by_default
public class PegiMain {

    //@ requires args != null;
    //@ ensures true;
    public static void main(String[] args) {
        //@ assume args != null;

        PegiFeatureExtractor extractor = new PegiFeatureExtractor();
        GeneticEngine engine = new GeneticEngine();

        System.out.println("=== Test 1: estrazione feature ===");
        PegiFeatures f1 = extractor.extract("This family puzzle game is colorful and fun.");
        //@ assert f1 != null;
        System.out.println("Ratio = " + f1.ratio());

        System.out.println("\n=== Test 2: testo violento ===");
        PegiFeatures f2 = extractor.extract("Blood and weapons in a brutal fight with gore!");
        //@ assert f2 != null;
        System.out.println("Ratio = " + f2.ratio());

        System.out.println("\n=== Test 3: errore intenzionale ===");
        try {
            extractor.extract(null); // viola requires text != null
            //@ unreachable;
        } catch (Throwable e) {
            //@ assert e != null;
            System.err.println("Violazione JML catturata (estrazione): " + e);
        }

        System.out.println("\n=== Test 4: GeneticEngine - evoluzione ===");
        try {
            //@ assume engine != null;
            double[] best = engine.evolve();
            //@ assert best != null && best.length == 7;

            System.out.println("Soluzione evoluta:");
            for (int i = 0; i < best.length; i++) {
                System.out.printf("w[%d] = %.4f%n", i, best[i]);
            }

        } catch (Throwable e) {
            System.err.println("Violazione JML catturata (evolve): " + e);
        }
    }
}
