package FileChatbot.End;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import static Utilities.Misc.Double2Float;

public class FileChatbot {
    private final List<String> asstHistory;      // history of all responses from LLM
    private final List<String> userHistory;      // history of all prompts sent to LLM
    private List<String> files;                  // filenames of files that contain related information
    private String instruction = "You are a well-respected Java expert and will respond as one.";        // additional behavior (system)
    private String completion_format = "Respond in italiano";      // style or language of output
    private List<String> context = new ArrayList<>();
    private OpenAiService service;

    FileChatbot(String apikey) {
        service = new OpenAiService(apikey, Duration.ofSeconds(30));
        asstHistory = new ArrayList<>();
        userHistory = new ArrayList<>();
    }

    FileChatbot(String apikey, String instruction) {
        this(apikey);
        setInstruction(instruction);
    }

    FileChatbot(String apikey, List<String>files, String instruction) {
        this(apikey, instruction);
        this.files = files;
    }

    public OpenAiService getService() {
        return service;
    }
    public void setService(OpenAiService service) {
        this.service = service;
    }
    public String getInstruction() {
        return instruction;
    }
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * ****************************************************************************************
     * getCompletions() - create the prompt from the messages, get the result, adjust the histories
     *
     * @param prompt
     * @return
     */
    public List<String> getCompletions(String prompt) {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM
        List<ChatMessage> messages = new ArrayList<>();

        // step 1 - how to behave
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), instruction);
        messages.add(systemMessage);

        // step 2 - prepend context (user and assistant msgs)
        addContext(prompt, messages);

        // step 3 - add the user's actual prompt
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(userMessage);

        // step 4 - specify the output format
        final ChatMessage format = new ChatMessage(ChatMessageRole.SYSTEM.value(), completion_format);
        messages.add(format);

        showMessages(messages);     // Just to show the whole prompt sent to the LLM

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(256)
                .build();

        List<ChatCompletionChoice> completions = service.createChatCompletion(chatCompletionRequest).getChoices();

        for (ChatCompletionChoice s : completions) {
            resultsFromLLM.add(s.getMessage().getContent().trim());
        }

        appendAsstHistory(resultsFromLLM.get(0));   // Add the Assistant (LLM) response to the Asst history
        appendUserHistory(prompt);                // Add the User's prompt to the Prompt history

        return resultsFromLLM;
    }

    /**
     * getCompletion() - convenience method to retrieve only the first completion
     *
     * @param prompt
     * @return
     */
    public String getCompletion(String prompt) {
        return getCompletions(prompt).get(0).toString();       // just the first one for now
    }

    /**
     * addContext() - add user history and the LLM's completion history to the overall context
     * @param prompt
     * @param msg
     */
    public void addContext(String prompt, List<ChatMessage> msg) {
        addFiles(this.files, msg);  // add the optional list of files
        addUserHistory(msg);        // add the user prompt history
        addAsstHistory(msg);        // add the LLM (assistant) history
    }

    /**
     * addFiles() - add an optional list of files to the context (very primitive RAG system)
     * @param filenames
     * @param msg
     */
    public void addFiles(List<String>filenames, List<ChatMessage> msg) {
        for (String f : filenames) {
            String finfo = null;
            try {
                finfo = new String(Files.readAllBytes(Paths.get(f)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            msg.add(new ChatMessage(ChatMessageRole.USER.value(), finfo));
        }
    }

    public void addRAGDataToMsg(List<ChatMessage> msg, List<String> strmatches) {
        for (String s : strmatches) {
            msg.add(new ChatMessage(ChatMessageRole.USER.value(), s));
        }
    }

    public void addAsstHistory(List<ChatMessage> msg) {
        for (int i = 0; i < asstHistory.size(); i++) {
            ChatMessage p = new ChatMessage(ChatMessageRole.ASSISTANT.value(), asstHistory.get(i));
            msg.add(p);
        }
    }

    public void addUserHistory(List<ChatMessage> msg) {
        for (int i = 0; i < userHistory.size(); i++) {
            ChatMessage p = new ChatMessage(ChatMessageRole.USER.value(), userHistory.get(i));
            msg.add(p);
        }
    }

    /**
     * appendAsstHistory() - Add a string to the Assistant history
     *
     * @param asst
     * @return
     */
    public Boolean appendAsstHistory(String asst) {
        return this.asstHistory.add(asst);
    }

    /**
     * appendUserHistory() - Add a string to the User history
     *
     * @param prompt
     * @return
     */
    public Boolean appendUserHistory(String prompt) {
        return this.userHistory.add(prompt);
    }

    /**
     * showMesssages() - useful display of all ChatMessages in a list
     *
     * @param mlist
     */
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



    /**
     * sendEmbeddingRequest() - get an embedding vector and convert to List<Float> for Milvus
     *
     * @param msg
     * @return
     */
    public List<Float> sendEmbeddingRequest(String msg) {

        List<Float> results = new ArrayList<>();
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-3-small")
                .input(Collections.singletonList(msg))
                .build();

        List<Embedding> embedding = getService().createEmbeddings(embeddingRequest).getData();
        List<Double> emb = embedding.get(0).getEmbedding();     // OpenAI returns Doubles... Milvus wants Floats...
        List<Float> newb = Double2Float(emb);
        int size = embedding.get(0).getEmbedding().size();
        for (int i = 0; i < size; i++) {
            results.add(newb.get(i));
        }
        return results;
    }
}
