package Embeddings.Begin;

import Utilities.Misc;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.List;

import static Utilities.Misc.*;


public class CompareEmbeddings {

    public static void main(String[] args) throws IOException {

        /*
         Get the api key and create an embedding model.

        String token = Misc.getAPIkey();
        EmbeddingModel model = OpenAiEmbeddingModel.withApiKey(...);
        */

        /*
         Get the embedding vectors for two strings

        List<Float> one = getEmbeddingVec(..., "...");
        List<Float> two = getEmbeddingVec(..., "...");
        */

        /*
          Calculate the cosine similarity and display it along with the cosine distance.
          Use the FloatList2doubleArray() method in the Utilities.Misc class

        double similarity = cosineSimilarity(FloatList2doubleArray(...), FloatList2doubleArray(...));
        System.out.println("Cosine Similarity: " + similarity);
        System.out.println("Cosine Distance: " + (1 - similarity));
        */
    }

    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }
}