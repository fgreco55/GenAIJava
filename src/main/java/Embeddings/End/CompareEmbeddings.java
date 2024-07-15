package Embeddings.End;

import Utilities.Misc;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.List;

import static Utilities.Misc.*;


public class CompareEmbeddings {

    public static void main(String[] args) throws IOException {

        String token = Misc.getAPIkey();
        EmbeddingModel model = OpenAiEmbeddingModel.withApiKey(token);

        List<Float> one = getEmbeddingVec(model, "I like the java programming language.");
        List<Float> two = getEmbeddingVec(model, "Pineapple does not belong on pizza.");

        double similarity = cosineSimilarity(FloatList2doubleArray(one), FloatList2doubleArray(two));
        System.out.println("Cosine Similarity: " + similarity);
        System.out.println("Cosine Distance: " + (1 - similarity));
    }

    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }
}