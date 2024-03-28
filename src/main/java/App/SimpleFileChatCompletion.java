package App;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static Utilities.Misc.getConfigProperties;

public class SimpleFileChatCompletion {

    public static void main(String[] args) throws IOException {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM

        ChatMessage userMessage;
        String DEFAULT_CONFIG = "./src/main/resources/genai.properties";
        String DEFAULT_DATAFILE = "./src/main/resources/mydata.txt";
        String INSTRUCTION = "You are a extremely good grammar-school instructor and will respond as one.";

        Properties prop = getConfigProperties(DEFAULT_CONFIG);
        if (prop == (Properties) null) {
            System.err.println("Cannot find properties file. Your path to the properties is probably incorrect.");
            System.exit(1);
        }
        String token = prop.getProperty("chatgpt.apikey");


        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));

        // Create system msg
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), INSTRUCTION);
        messages.add(systemMessage);

        // Create user msg from file
        String fstring = new String(Files.readAllBytes(Paths.get(DEFAULT_DATAFILE)));
        userMessage = new ChatMessage(ChatMessageRole.USER.value(), fstring);
        messages.add(userMessage);

        // Create prompt message
        userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Summarize in a succinct paragraph to a non computer student.");
        messages.add(userMessage);

        // Ask the LLM for a completion
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")        // chat endpoint
                .messages(messages)            // your chat “messages”
                .n(1)                       // num of completions
                .maxTokens(128)                // max size of each completion
                .build();
        List<ChatCompletionChoice> completions = service.createChatCompletion(chatCompletionRequest).getChoices();

        // output the results
        for (ChatCompletionChoice s : completions) {
            System.out.println(s.getMessage().getContent().trim());
        }
    }
}
