package Embeddings.Begin;

import Utilities.Misc;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Utilities.Misc.*;

public class MostSimilar {
    private final static String DEFAULT_DATA = "./src/main/resources/simple.txt";

    public static void main(String[] args) throws IOException {

        OpenAiService service = new OpenAiService(Misc.getAPIkey());

        // Get a string from the user and retrieve its embedding vector
        List<Embedding> one = getEmbeddingVec(service, "I am interested in Java 22, reliability, and structured concurrency.");
        List<Double> emb1 = one.get(0).getEmbedding();
        Double[] emb1d = emb1.toArray(new Double[0]);

        // Read file of strings into a list
        List<String> fstrings = fileToListStrings(DEFAULT_DATA);
        List<Double> similarities = new ArrayList<>();

        // Iterate thru List and calculate the cosine similarity for each line in the file compared to the user's input
        for (String fs : fstrings) {
            if (fs.length() == 0)
                continue;
           /*
             Step 1 - Get embedding vector from OpenAI for each string in the file

            List<Embedding> fsembedding = getEmbeddingVec(...);
            List<Double> fsvec = fsembedding.get(0).getEmbedding();
            Double[] fd = fsvec.toArray(new Double[0]);
            */

            /*
             Step 2 - get the cosine similarity between this string and the user's prompt

            double similarity = cosineSimilarity(...);
            similarities.add(similarity);
            */
        }

        /*
          Step 4 - sort the similarities and display the sorted list

        Collections.sort(...);
        System.out.println(similarities);
        */
    }

    public static List<Embedding> getEmbeddingVec(OpenAiService service, String input) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-3-small")
                .input(Collections.singletonList(input))
                .build();

        List<Embedding> embeddings = service.createEmbeddings(embeddingRequest).getData();
        return embeddings;
    }
}
