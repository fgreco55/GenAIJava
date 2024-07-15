package Embeddings.Begin;

import Utilities.Misc;

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

       /*
         Create an embedding model using your OpenAI API key

        String token = Misc.getAPIkey();
        EmbeddingModel model = OpenAiEmbeddingModel.withApiKey(...);
        */

        /*
         Get the embedding vector for a particular target string

        List<Float> one = getEmbeddingVec(..., "...");
        */

        // Read file of strings into a list
        List<String> fstrings = fileToListStrings(DEFAULT_DATA);
        List<Float> similarities = new ArrayList<>();

        // Iterate thru List and calculate the cosine similarity for each line in the file compared to the user's input
        for (String fs : fstrings) {
            if (fs.length() == 0)           // skip empty lines
                continue;
            /*
             Get the embedding vector for each string

            List<Float> fsembedding = getEmbeddingVec(..., ...);
            */

            /*
             Calculate the similarity between the target string and the line from the file

            double similarity = cosineSimilarity(FloatList2doubleArray(...), FloatList2doubleArray(...));
            similarities.add(1 - (float) similarity);     // Save the cosine similarities for sorting later
            */
        }

        /*
          Sort the array and show the top N

        Collections.sort(...);     // remember sort() sorts in ascending order
        System.out.println(similarities);

        showtop(..., ...);
        */
    }

    /*
     Get the embedding vector from a given Embedding Model
     */
    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }

    /*
     Show the top "max" entries from a sorted list
     */
    public static void showtop(List<Float> sim, int max) {
        System.out.println("Top " + max + " =====================");
        for (int i = 0; i < max; i++) {
            System.out.println(sim.get(i));
        }
    }
}