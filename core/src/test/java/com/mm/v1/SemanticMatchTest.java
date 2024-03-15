package com.mm.v1;

import java.util.ArrayList;
import java.util.List;

import com.mm.v1.semanticmatch.SemanticMatch;

public class SemanticMatchTest {


    public static void main(String[] args) {

        List<String[]> tests = getTests();

        for (String[] test : tests) {

            String input = test[0];
            String output = test[1];

            testMatch(input, output);

        }

    }

    private static List<String[]> getTests()    {

        List<String[]> tests = new ArrayList<String[]>();

        String[] test1 = new String[] {"Come Together (Remastered) The Beatles", "Come Together The Beatles"};
        tests.add(test1);

        String[] test2 = new String[] {"Purple Rain Prince", "Purple Rain Prince and the Revolution"};
        tests.add(test2);

        String[] test3 = new String[] {"Mack Game Totally Insane", "mack GAME ToTaLLy insane"};
        tests.add(test3);

        String[] test4 = new String[] {"Esate Joa Gilberto", "Estate Joao Gilberto"};
        tests.add(test4);

        String[] test5 = new String[] {"Mo Bambo Sheck Wis", "Mo Bomba Sheck Wes"};
        tests.add(test5);

        String[] test6 = new String[] {"Yesterday J Dilla", "Yesterday The Beatles"};
        tests.add(test6);

        String[] test7 = new String[] {"Let it Be The Beatles", "Let it Be The Replacements"};
        tests.add(test7);

        String[] test8 = new String[] {"Ellie's Love Theme (Remix) Isaac Hayes", "Ellie's Love Theme Isaac Hayes"};
        tests.add(test8);

        String[] test9 = new String[] {"1984 David Bowie", "1984 Van Halen"};
        tests.add(test9);

        String[] test10 = new String[] {"A Thousand Years Toto", "A Thousand Years Sting"};
        tests.add(test10);

        return tests;

    }

    private static void testMatch(String input, String output)   {

        double similarity = SemanticMatch.computeCosineSimilarity(input, output);

        System.out.println("## TESTING ##");
        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
        System.out.println("*** Similarity: " + similarity + " ***");

    }
    
}
