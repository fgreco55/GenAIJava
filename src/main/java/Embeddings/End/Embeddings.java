package Embeddings.End;

import Utilities.Misc;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static Utilities.Misc.getProperty;

public class Embeddings {

    public static void main(String[] args) throws IOException {

        Scanner userinput;

        String token = Misc.getAPIkey();
        OpenAiService service = new OpenAiService(token);

        while (true) {
            System.out.print("String to Embed> ");
            userinput = new java.util.Scanner(System.in);

            if (userinput.hasNextLine()) {
                String cmd = userinput.nextLine();
                if (!cmd.isEmpty()) {

                    List<Embedding> embeddings = getEmbeddingVec(service, cmd);
                    embeddings.forEach(System.out::println);
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

