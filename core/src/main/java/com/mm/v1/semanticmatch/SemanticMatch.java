package com.mm.v1.semanticmatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.context.annotation.Bean;

public class SemanticMatch {

    public static TransformersEmbeddingClient embeddingClient = new TransformersEmbeddingClient();

    public static void main(String[] args) {

        String input = "Come Together (Remastered) The Beatles Abbey Road";
        String output = "Come Together The Beatles Abbey Road (20th Anniversary Edition)";

        String[] texts = new String[] {input, output};

        List<List<Double>> embeddings = tokenizeByTransformer(texts);

        Vector vector1 = new Vector(embeddings.get(0));
        Vector vector2 = new Vector(embeddings.get(1));

        double similarity = vector1.CosineSimilarity(vector2);
        
        System.out.println("Similarity: " + similarity);

    }

    public static double computeCosineSimilarity(String input, String output) {

        String[] texts = new String[] {input, output};

        List<List<Double>> embeddings = tokenizeByTransformer(texts);

        Vector vector1 = new Vector(embeddings.get(0));
        Vector vector2 = new Vector(embeddings.get(1));

        double similarity = vector1.CosineSimilarity(vector2);

        return similarity;

    }

    
    public static double computeCosineSimilarityOld(String input, String output) {

        int N = 1;

        Map<CharSequence, Integer> leftVector = stringToFrequencyMap(input, N);
        Map<CharSequence, Integer> rightVector = stringToFrequencyMap(output, N);

        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        return cosineSimilarity.cosineSimilarity(leftVector, rightVector);

    }

    private static List<List<Double>> tokenizeByTransformer(String[] texts)  {

        embeddingClient.setResourceCacheDirectory("core/bin/transformer");
        // (optional) Set the tokenizer padding if you see an errors like:
        // "ai.onnxruntime.OrtException: Supplied array is ragged, ..."
        embeddingClient.setTokenizerOptions(Map.of("padding", "true"));

        try {
            embeddingClient.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String input = strip_text(texts[0]);
        String output = strip_text(texts[1]);
        
        List<List<Double>> embeddings = embeddingClient.embed(List.of(input, output));

        return embeddings;

    }

    /**
     * attempt at model performance: strip away key repetitve words
     */
    private static String strip_text(String text)  {

        String result = text.toLowerCase();

        String[] words_to_remove = {
            " remastered",
            " remix",
            " remaster",
            " (remastered)",
            " (remaster)",
            " (remix)",
            " - remastered",
            " - remaster"
        };

        for (String word : words_to_remove) {
            result = result.replaceAll("\\b" + word + "\\b", "");
        }

        System.out.println("After stripping: " + result);

        return result;
        
    }

    /**
     * tokenize the input string as N-grams, ie. segments of N characters
     */
    private static String[] tokenizeByWord(String text, int N)    {

        text = text.toLowerCase(); // .replaceAll("[^a-zA-Z'\\s]", "")
        String[] result = text.split("\\s+"); // regex for one or more whitespaces
        return result;

    }

    /**
     * tokenize the input string as N-grams, ie. segments of N characters
     */
    private static String[] tokenizeByN(String text, int N)    {

        text = text.toLowerCase(); // .replaceAll("[^a-zA-Z'\\s]", "")
        text = text.replaceAll("\\s", "");
        /* use the regex (?<=\\G.{” + n + “})
        it is a positive lookback assertion assertion that matches a string 
        that has the last match (\G) followed by n characters */
        String[] result = text.split("(?<=\\G.{" + N + "})");
        return result;

    }

    private static Map<CharSequence, Integer> stringToFrequencyMap(String text, int N)    {

        Map<CharSequence, Integer> frequencyMap = new HashMap<>();

        // now tokenize the text into words 
        String[] tokens = tokenizeByN(text, N); // regex for one or more whitespaces

        // collect word frequencies
        for (String word : tokens) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        return frequencyMap;

    }
    
}
