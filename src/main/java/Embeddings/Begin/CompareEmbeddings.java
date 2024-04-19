package Embeddings.Begin;

import Utilities.Misc;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static Utilities.Misc.Double2double;
import static Utilities.Misc.cosineSimilarity;


public class CompareEmbeddings {

    public static void main(String[] args) throws IOException {

        String token = Misc.getAPIkey();
        OpenAiService service = new OpenAiService(token);

        /*
         Get the embedding vector for the first string

        List<Embedding> one = getEmbeddingVec(...);
        List<Double> emb1 = one.get(0).getEmbedding();  // Get the first element
        Double[] emb1d = emb1.toArray(new Double[0]);   // convert from List to array of Doubles
        */

        /*
         Get the embedding vector for the second string
        List<Embedding> two = getEmbeddingVec(...);
        List<Double> emb2 = two.get(0).getEmbedding();
        Double[] emb2d = emb2.toArray(new Double[0]);
         */

        /*
         Calculate the cosine similarity and display it

        double similarity = cosineSimilarity(...);
        System.out.println("Cosine Similarity: " + similarity);
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

