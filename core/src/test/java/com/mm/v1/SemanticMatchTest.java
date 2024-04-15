package com.mm.v1;

import java.util.ArrayList;
import java.util.List;

import com.mm.v1.semanticmatch.SemanticMatch;

public class SemanticMatchTest {


    public static void main(String[] args) {

        List<String[]> expected_matches = getPositiveTests();
        List<String[]> expected_failures = getNegativeTests();

        boolean print_enabled = false;

        evaluate(expected_matches, expected_failures, print_enabled);

    }

    private static List<String[]> getPositiveTests()    {

        List<String[]> expected_matches = new ArrayList<String[]>();

        /** EXPECTED MATCHES */
            
        // 1
        String[] test1 = new String[] {"Come Together (Remastered) The Beatles", "Come Together The Beatles"};
        expected_matches.add(test1);
        // 2
        String[] test2 = new String[] {"Purple Rain Prince", "Purple Rain Prince and the Revolution"};
        expected_matches.add(test2);
        // 3
        String[] test3 = new String[] {"Mack Game Totally Insane", "mack GAME ToTaLLy insane"};
        expected_matches.add(test3);
        // 4
        String[] test4 = new String[] {"Esate Joa Gilberto", "Estate Joao Gilberto"};
        expected_matches.add(test4);
        // 5
        String[] test5 = new String[] {"Mo Bambo Sheck Wis", "Mo Bomba Sheck Wes"};
        expected_matches.add(test5);
        // 6
        String[] test8 = new String[] {"Ellie's Love Theme (Remix) Isaac Hayes", "Ellie's Love Theme Isaac Hayes"};
        expected_matches.add(test8);
        // 7
        String[] test11 = new String[] {"Octopus's Garden - Remastered 2009 The Beatles", "Octopus's Garden The Beatles"};
        expected_matches.add(test11);
        // 8
        String[] test12 = new String[] {"Death On Two Legs (Dedicated To...) Queen", "Deth On to Legs (Dedicated To) Queen"};
        expected_matches.add(test12);
        // 9
        String[] test15 = new String[] {"Lazing On A Sunday Afternoon Queen", "Lazing on a Sunday Afternoon (Remastered) Queen"};
        expected_matches.add(test15);
        // 10
        String[] test16 = new String[] {"I Cant Be Faded Totally Insane", "I Can't Be Faded Totally Insane"};
        expected_matches.add(test16);
        // 11
        String[] test17 = new String[] {"Lets go crazy prince", "Let's Go Crazy! Prince & The Revolution"};
        expected_matches.add(test17);
        // 12
        String[] test18 = new String[] {"MODERN JAM (feat. Teezo Touchdown) Travis Scott", "MODERN JAM Travis Scott"};
        expected_matches.add(test18);
        // 13
        String[] test19 = new String[] {"Eye Know De La Soul, Otis Redding", "Eye Know De La Soul"};
        expected_matches.add(test19);
        // 14
        String[] test20 = new String[] {"Blue Sway Paul Mccartney", "Blue Sway - Remastered 2011 Paul McCartney, Richard Miles"};
        expected_matches.add(test20);
        // 15
        String[] test21 = new String[] {"My World Hallow Tip", "My World Hollow Tip"};
        expected_matches.add(test21);

        return expected_matches;

    }

    private static List<String[]> getNegativeTests()    {

        List<String[]> expected_failures = new ArrayList<String[]>();

        /** EXPECTED FAILURES */

        // 1
        String[] test6 = new String[] {"Yesterday J Dilla", "Yesterday The Beatles"};
        expected_failures.add(test6);
        // 2
        String[] test7 = new String[] {"Let it Be The Beatles", "Let it Be The Replacements"};
        expected_failures.add(test7);
        // 3
        String[] test9 = new String[] {"1984 David Bowie", "1984 Van Halen"};
        expected_failures.add(test9);
        // 4
        String[] test10 = new String[] {"A Thousand Years Toto", "A Thousand Years Sting"};
        expected_failures.add(test10);
        // 5
        String[] test13 = new String[] {"Breakdown Tom Petty and the Heartbreakers", "Breakdown The Alan Parsons Project"};
        expected_failures.add(test13);
        // 6
        String[] test14 = new String[] {"Camera R.E.M.", "Camera Wilco"};
        expected_failures.add(test14);
        // 7
        String[] test18 = new String[] {"Come Together The Beatles", "Come Together Primal Scream"};
        expected_failures.add(test18);
        // 8
        String[] test19 = new String[] {"Creep Radiohead", "Creep Stone Temple Pilots"};
        expected_failures.add(test19);
        // 9
        String[] test20 = new String[] {"Do it Again Steely Dan", "Do it again The Beach Boys"};
        expected_failures.add(test20);
        // 10
        String[] test21 = new String[] {"Dreams The Allman Brothers Band", "Dreams Fleetwood Mac"};
        expected_failures.add(test21);
        // 11
        String[] test22 = new String[] {"Drive The Cars", "Drive R.E.M."};
        expected_failures.add(test22);
        // 12
        String[] test23 = new String[] {"End of the Line The Traveling Wilburys", "End of the Line The Allman Brothers Band"};
        expected_failures.add(test23);
        // 13
        String[] test24 = new String[] {"End of the Line The Traveling Wilburys", "End of the Line The Allman Brothers Band"};
        expected_failures.add(test24);
        // 14
        String[] test25 = new String[] {"Exciter Judas Priest", "Exciter Kiss"};
        expected_failures.add(test25);
        // 15
        String[] test26 = new String[] {"For Whom the Bell Tolls Metallica", "For Whom the Bell Bee Gees"};
        expected_failures.add(test26);

        return expected_failures;

    }

    private static void evaluate(List<String[]> expected_matches, List<String[]> expected_failures, boolean print_enabled) {

        // gather the test results

        List<Double> match_similarities = new ArrayList<Double>();
        double total_match_tests = expected_matches.size();
        double total_match_sims = 0;
        double max_match_similarity = 0;
        double min_match_similarity = 99999999;

        for (String[] test : expected_matches) {

            String input = test[0];
            String output = test[1];

            double sim = testMatch(input, output, print_enabled);
            match_similarities.add(sim);

            total_match_sims += sim;
            max_match_similarity = Math.max(max_match_similarity, sim);
            min_match_similarity = Math.min(min_match_similarity, sim);

        }

        // now print eval results
        double average_match_similarity = total_match_sims / total_match_tests;

        System.out.println("### TEST RESULTS : Expected Matches ###");
        System.out.println("Average Similarity: " + average_match_similarity);
        System.out.println("Maximum Similarity: " + max_match_similarity);
        System.out.println("Minimum Similarity: " + min_match_similarity);

        List<Double> failure_similarities = new ArrayList<Double>();
        double total_failure_tests = expected_failures.size();
        double total_failures_sims = 0;
        double max_failures_similarity = 0;
        double min_failures_similarity = 99999999;

        for (String[] test : expected_failures) {

            String input = test[0];
            String output = test[1];

            double sim = testMatch(input, output, print_enabled);
            failure_similarities.add(sim);

            total_failures_sims += sim;
            max_failures_similarity = Math.max(max_failures_similarity, sim);
            min_failures_similarity = Math.min(min_failures_similarity, sim);

        }

        // now print eval results
        double average_failure_similarity = total_failures_sims / total_failure_tests;

        System.out.println("### TEST RESULTS : Expected Failures ###");
        System.out.println("Average Similarity: " + average_failure_similarity);
        System.out.println("Maximum Similarity: " + max_failures_similarity);
        System.out.println("Minimum Similarity: " + min_failures_similarity);


    }

    private static double testMatch(String input, String output, boolean print_enabled)   {

        double similarity = SemanticMatch.computeCosineSimilarity(input, output);

        if (print_enabled)  {

            System.out.println("## TESTING ##");
            System.out.println("Input: " + input);
            System.out.println("Output: " + output);
            System.out.println("*** Similarity: " + similarity + " ***");

        }

        return similarity;

    }
    
}
