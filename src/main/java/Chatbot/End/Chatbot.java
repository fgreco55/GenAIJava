package Chatbot.End;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.*;

public class Chatbot {
    private final List<String> asstHistory;      // history of all responses from LLM
    private final List<String> promptHistory;    // history of all prompts sent to LLM
    private String instruction = "You are a extremely helpful Java expert and will respond as one.";        // additional behavior (system)
    private String completion_format = "";      // style or language of output
    private List<String> context = new ArrayList<>();
    private OpenAiService service;

    Chatbot(String apikey) {
        service = new OpenAiService(apikey, Duration.ofSeconds(30));
        asstHistory = new ArrayList<>();
        promptHistory = new ArrayList<>();
    }

    public List<String> getCompletions(String prompt) {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM
        List<ChatMessage> messages = new ArrayList<>();

        // step 1 - how to behave
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), instruction);
        messages.add(systemMessage);

        // step 2 - prepend context (user and assistant msgs)
        addContext(messages);

        // step 3 - add the user's actual prompt
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(userMessage);

        // step 4 - specify the output format
        final ChatMessage format = new ChatMessage(ChatMessageRole.USER.value(), completion_format);
        messages.add(format);

        showMessages(messages);     // Just to show the whole prompt sent to the LLM

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(128)
                .build();

        List<ChatCompletionChoice> completions = service.createChatCompletion(chatCompletionRequest).getChoices();

        for (ChatCompletionChoice s : completions) {
            resultsFromLLM.add(s.getMessage().getContent().trim());
        }

        appendAsstHistory(resultsFromLLM.get(0));   // Add the Assistant (LLM) response to the Asst history
        appendPromptHistory(prompt);                // Add the User's prompt to the Prompt history

        return resultsFromLLM;
    }

    public String getCompletion(String prompt) {
        return getCompletions(prompt).get(0).toString();       // just the first one for now
    }

    public void addContext(List<ChatMessage> msg) {
        addPromptHistory(msg);     // add the user prompt history
        addAsstHistory(msg);       // add the LLM (assistant) history
    }

    public Boolean appendAsstHistory(String asst) {
        return this.asstHistory.add(asst);
    }

    public Boolean appendPromptHistory(String prompt) {
        return this.promptHistory.add(prompt);
    }

    public void addAsstHistory(List<ChatMessage> msg) {
        for (int i = 0; i < asstHistory.size(); i++) {
            ChatMessage p = new ChatMessage(ChatMessageRole.ASSISTANT.value(), asstHistory.get(i));
            msg.add(p);
        }
    }

    public void addPromptHistory(List<ChatMessage> msg) {
        for (int i = 0; i < promptHistory.size(); i++) {
            ChatMessage p = new ChatMessage(ChatMessageRole.USER.value(), promptHistory.get(i));
            msg.add(p);
        }
    }

    public static void showMessages(List<ChatMessage> mlist) {
        System.out.println("+START-----------------------------------------------------+ [" + mlist.size() + "]");
        for (ChatMessage cm : mlist) {
            switch (cm.getRole()) {
                case "system":
                    System.out.println("SYSTEM: " + cm.getContent().toString());
                    break;
                case "user":
                    System.out.println("  USER: " + cm.getContent().toString());
                    break;
                case "assistant":
                    System.out.println("  ASST: " + cm.getContent().toString());
                    break;
                default:
                    System.out.println("UNDEFINED ROLE!!!!");
                    break;
            }
        }
        // mlist.forEach(cm -> System.out.println("MSG: " + cm.getContent().toString()));
        System.out.println("+END-------------------------------------------------------+");
    }
}

