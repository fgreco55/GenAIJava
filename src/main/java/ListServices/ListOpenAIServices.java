package ListServices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * List all the available services from OpenAI in JSON format using pure Java
 */
public class ListOpenAIServices {
    public static void main(String[] args) throws IOException, InterruptedException {
        var apiKey = System.getenv("OPENAI_API_KEY");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/models"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
