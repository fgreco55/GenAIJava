package Completions.Begin;

import Utilities.Misc;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static Utilities.Misc.getConfigProperties;

public class SimpleFileChatCompletion {

    public static void main(String[] args) throws IOException {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM

        ChatMessage userMessage;
        String DEFAULT_DATAFILE = "./src/main/resources/mydata.txt";
        /*
         Add a useful system message

        String INSTRUCTION = "You are a ...";
        */

        String token = Misc.getAPIkey();
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));

        // Create system msg
        final List<ChatMessage> messages = new ArrayList<>();

        /*
         Create a system message with a useful instruction for the LLM and add it to the List of messages

        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), ...;
        messages.add(...);
        */

        // Create user msg from file
        String fstring = new String(Files.readAllBytes(Paths.get(DEFAULT_DATAFILE)));

        /*
         Create a user message using the string representation of the file and add it to the List of messages

        userMessage = new ChatMessage(ChatMessageRole.USER.value(), ...);
        messages.add(...);
        */

        /*
         Create a useful "summarize this" prompt and add it to the List of messages

        userMessage = new ChatMessage(ChatMessageRole.USER.value(), ...);
        messages.add(...);
        */

        /*
         Create a chatCompletionRequest instance using its builder

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(...)               // chat endpoint
                .messages(...)            // your chat “messages”
                .n(1)                     // num of completions
                .maxTokens(...)           // max size of each completion
                .build();
         */

        /*
          Ask the LLM for a completion

        List<ChatCompletionChoice> completions = service.createChatCompletion(...).getChoices();
        */

        /*
         Display the completions

        for (ChatCompletionChoice s : ...) {
            System.out.println(s.getMessage()...);
        }
        */
    }
}
