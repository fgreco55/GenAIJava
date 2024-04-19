package Completions.End;

import Utilities.Misc;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static Utilities.Misc.getConfigProperties;

public class SimpleChatCompletion {
    public static void main(String[] args) throws IOException {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM

        String token = Misc.getAPIkey();
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "You are a extremely funny comedian and will respond as one.");
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
                "How should I learn Java?");

        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")        // chat endpoint
                .messages(messages)            // your chat “messages”
                .n(3)                       // num of completions
                .maxTokens(64)                // max size of each completion
                .build();
        List<ChatCompletionChoice> completions = service.createChatCompletion(chatCompletionRequest).getChoices();

        for (ChatCompletionChoice s : completions) {
            System.out.println(s.getMessage().getContent().trim());
        }
    }
}
