package Completions.End;

import Utilities.Misc;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SimpleFileChatCompletion {

    public static void main(String[] args) throws IOException {
        List<String> resultsFromLLM = new ArrayList<>();  // results coming back from LLM

        ChatMessage userMessage;
        String DEFAULT_DATAFILE = "./src/main/resources/mydata.txt";
        String INSTRUCTION = "You are a extremely good grammar-school instructor and will respond as one.";

        String token = Misc.getAPIkey();

        // Create system msg
        final List<ChatMessage> messages = new ArrayList<>();
        SystemMessage sysmsg = new SystemMessage(INSTRUCTION);
        messages.add(sysmsg);

        // Create user msg from file
        String fstring = new String(Files.readAllBytes(Paths.get(DEFAULT_DATAFILE)));
        UserMessage usermsg = new UserMessage(fstring);
        messages.add(usermsg);

        // Create prompt message
        usermsg = new UserMessage("Summarize in a succinct paragraph to a non computer student.");
        messages.add(usermsg);

        // Ask the LLM for a completion
        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                        .apiKey(token)
                        .modelName(OpenAiChatModelName.GPT_4_O)
                        .temperature(0.3)
                        .timeout(Duration.ofSeconds(30))
                        .maxTokens(1024)
                        .build();

        Response<AiMessage> answer = cmodel.generate(messages);

        // output the results
        System.out.println(answer.content().text());        // text() eliminates the 'noise' results
    }
}