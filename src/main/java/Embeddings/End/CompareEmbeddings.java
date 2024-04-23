package Embeddings.End;

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

        List<Embedding> one = getEmbeddingVec(service, "I like the Java programming language.");
        List<Double> emb1 = one.get(0).getEmbedding();
        Double[] emb1d = emb1.toArray(new Double[0]);   // convert from List to array of Doubles for cosineSimilarity()

        List<Embedding> two = getEmbeddingVec(service, "Java is a programming language.");
        List<Double> emb2 = two.get(0).getEmbedding();
        Double[] emb2d = emb2.toArray(new Double[0]);

        double similarity = cosineSimilarity(Double2double(emb1d), Double2double(emb2d));
        System.out.println("Cosine Similarity: " + similarity);
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

