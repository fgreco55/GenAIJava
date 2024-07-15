package Chatbot.End;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.*;

public class Chatbot {
    private final List<String> asstHistory;      // history of all responses from LLM
    private final List<String> userHistory;    // history of all user prompts sent to LLM
    private String instruction = "You are a extremely helpful Java expert and will respond as one.";        // additional behavior (system)
    private String completion_format = "Please respond in English";      // style or language of output     Tamil
    private List<String> context = new ArrayList<>();
    private ChatLanguageModel cmodel;

    Chatbot(String apikey) {
        cmodel = OpenAiChatModel.builder()
                .apiKey(apikey)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(512)
                .build();
        asstHistory = new ArrayList<>();
        userHistory = new ArrayList<>();
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
        SystemMessage sysmsg = new SystemMessage(instruction);
        messages.add(sysmsg);

        // step 2 - prepend context (user and assistant msgs)
        addContext(messages);

        // step 3 - add the user's actual prompt
        UserMessage usermsg = new UserMessage(prompt);
        messages.add(usermsg);

        // step 4 - specify the output format
        SystemMessage format = new SystemMessage(completion_format);
        messages.add(format);

        //showMessages(messages);     // Just to show the whole prompt sent to the LLM

        Response<AiMessage> answer = cmodel.generate(messages);
        System.out.println(answer.content().text());        // text() eliminates the 'noise' results

        resultsFromLLM.add(answer.content().text());

        appendAsstHistory(answer.content().text());   // Add the Assistant (LLM) response to the Asst history
        appendUserHistory(prompt);                  // Add the User's prompt to the Prompt history

        return resultsFromLLM;
    }

    /**
     * getCompletion() - convenience method to retrieve only the first completion
     *
     * @param prompt
     * @return
     */
    public String getCompletion(String prompt) {
        // Looks like Langchain4J does not give you access to OpenAI's multiple completion requests
        return getCompletions(prompt).get(0);
    }

    /**
     * ************************************************************************************
     * addContext - add the histories to the current Context
     *
     * @param msg
     */
    public void addContext(List<ChatMessage> msg) {
        addUserHistory(msg);       // add the user prompt history
        addAsstHistory(msg);       // add the LLM (assistant) history
    }

    public void addAsstHistory(List<ChatMessage> msg) {
        for (int i = 0; i < asstHistory.size(); i++) {
            UserMessage p = new UserMessage(asstHistory.get(i));
            msg.add(p);
        }
    }

    public void addUserHistory(List<ChatMessage> msg) {
        for (int i = 0; i < userHistory.size(); i++) {
            UserMessage p = new UserMessage(userHistory.get(i));
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
            switch (cm.type()) {
                case SYSTEM ->  System.out.println("SYSTEM: " + cm.text());
                case USER ->    System.out.println("  USER: " + cm.text());
                case AI ->      System.out.println("  ASST: " + cm.text());
                default ->      System.out.println("UNDEFINED ROLE!!!!");
            }
        }
        //mlist.forEach(cm -> System.out.println("MSG: " + cm.text()));
        System.out.println("+END-------------------------------------------------------+");
    }
}