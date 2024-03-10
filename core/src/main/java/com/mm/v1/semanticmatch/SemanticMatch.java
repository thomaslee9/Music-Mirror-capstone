package com.mm.v1.semanticmatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.similarity.CosineSimilarity;

public class SemanticMatch {

    public static void main(String[] args) {

        String input = "Come Together (Remastered) The Beatles Abbey Road";
        String output = "Come Together The Beatles Abbey Road (20th Anniversary Edition)";

        double similarity = computeCosineSimilarity(input, output);

        System.out.println("Similarity: " + similarity);

    }
    
    public static double computeCosineSimilarity(String input, String output) {

        Map<CharSequence, Integer> leftVector = stringToFrequencyMap(input);
        Map<CharSequence, Integer> rightVector = stringToFrequencyMap(output);

        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        return cosineSimilarity.cosineSimilarity(leftVector, rightVector);

    }

    private static Map<CharSequence, Integer> stringToFrequencyMap(String text)    {

        Map<CharSequence, Integer> frequencyMap = new HashMap<>();

        // convert to lower case
        text = text.toLowerCase(); // .replaceAll("[^a-zA-Z'\\s]", "")

        // now tokenize the text into words 
        String[] words = text.split("\\s+"); // regex for one or more whitespaces

        // collect word frequencies
        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        return frequencyMap;

    }
    
}
