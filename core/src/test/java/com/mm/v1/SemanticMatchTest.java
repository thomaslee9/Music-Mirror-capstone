package com.mm.v1;

import com.mm.v1.semanticmatch.SemanticMatch;

public class SemanticMatchTest {

    public static void main(String[] args) {

        String input, output;

        input = "Come Together (Remastered) The Beatles Abbey Road";
        output = "Come Together The Beatles Abbey Road (20th Anniversary Edition)";
        testMatch(input, output);

        input = "Band on the Run Wings & Paul McCartney Band on the Run";
        output = "Band on the Run Wings Band on the Run";
        testMatch(input, output);

        input = "Purple Rain Prince Best of Prince";
        output = "Purple Rain Prince Purple Rain";
        testMatch(input, output);

        input = "Yesterday J Dilla";
        output = "Yesterday The Beatles";
        testMatch(input, output);

        input = "Live and Let Die Wings Red Rose Speedway";
        output = "Live and Let Die Wings Best of Wings";
        testMatch(input, output);

        input = "Mack Game Totally Insane Direct From The Backstreet";
        output = "mack GAME ToTaLLy insane DIRECT From The backstreet";
        testMatch(input, output);

        input = "Light mr Fire The Dors Te Doors";
        output = "Light My Fire The Doors The Doors";
        testMatch(input, output);

        /** WARNING: need to improve, this should be more similar */
        input = "Esate Joa Gilberto Amorosoo";
        output = "Estate Joao Gilberto Amoroso";
        testMatch(input, output);

    }

    private static void testMatch(String input, String output)   {

        double similarity = SemanticMatch.computeCosineSimilarity(input, output);

        System.out.println("## TESTING ##");
        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
        System.out.println("*** Similarity: " + similarity + " ***");

    }
    
}
