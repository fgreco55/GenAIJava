package Embeddings.Begin;

import Utilities.Misc;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Embeddings{

    public static void main(String[] args) throws IOException {

        Scanner userinput;

        /*
         Get the key and create the embedding model

        String token = Misc.getAPIkey();
        EmbeddingModel model = OpenAiEmbeddingModel.withApiKey(...);
        */

        while (true) {
            System.out.print("String to Embed> ");
            userinput = new java.util.Scanner(System.in);

            if (userinput.hasNextLine()) {
                String cmd = userinput.nextLine();
                if (!cmd.isEmpty()) {
                    /*
                     Get the embedding vector for the user's input

                    List<Float> embeddings = getEmbeddingVec(..., ...);
                    */

                    /*
                    Show the embeddings and it's length

                    System.out.println(embeddings);
                    System.out.println("Embedding vector has " + embeddings.size() + " elements.");
                    */
                }
            }
        }
    }

    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }
}