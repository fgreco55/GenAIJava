package Embeddings.End;

import Utilities.Misc;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Utilities.Misc.*;

public class MostSimilar {
    private final static String DEFAULT_DATA = "./src/main/resources/simple.txt";

    public static void main(String[] args) throws IOException {

        // Get a string from the user and retrieve its embedding vector
        String token = Misc.getAPIkey();
        EmbeddingModel model = OpenAiEmbeddingModel.withApiKey(token);

        List<Float> one = getEmbeddingVec(model, "I am interested in Java 22, reliability, and structured concurrency.");

        // Read file of strings into a list
        List<String> fstrings = fileToListStrings(DEFAULT_DATA);
        List<Float> similarities = new ArrayList<>();

        // Iterate thru List and calculate the cosine similarity for each line in the file compared to the user's input
        for (String fs : fstrings) {
            if (fs.length() == 0)           // skip empty lines
                continue;

            List<Float> fsembedding = getEmbeddingVec(model, fs);
            double similarity = cosineSimilarity(FloatList2doubleArray(one), FloatList2doubleArray(fsembedding));
            similarities.add(1 - (float) similarity);     // Save the cosine similarities for sorting later
        }

        Collections.sort(similarities);     // remember sort() sorts in ascending order
        System.out.println(similarities);

        showtop(similarities, 3);
    }

    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }

    public static void showtop(List<Float> sim, int max) {
        System.out.println("Top " + max + " =====================");
        for (int i = 0; i < max; i++) {
            System.out.println(sim.get(i));
        }
    }
}