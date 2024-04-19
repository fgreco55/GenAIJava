package Embeddings.Begin;

import Utilities.Misc;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Embeddings {

    public static void main(String[] args) throws IOException {

        Scanner userinput;

        String token = Misc.getAPIkey();
        OpenAiService service = new OpenAiService(token);

        while (true) {
            System.out.print("String to Embed> ");
            userinput = new Scanner(System.in);

            if (userinput.hasNextLine()) {
                String cmd = userinput.nextLine();
                if (!cmd.isEmpty()) {

                    /*
                      Get Embedding vector from embeddings service

                    List<Embedding> embeddings = ...
                    */

                    /* Display the embedding vector

                    println...
                    */

                    System.out.println("Embedding vector has " + embeddings.get(0).getEmbedding().size() + " elements.");
                }
            }
        }
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

