package Completions.Begin;

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

        ChatMessage userMessage;
        String DEFAULT_DATAFILE = "./src/main/resources/mydata.txt";

        /*
             Add a useful system message

             String INSTRUCTION = "You are a ...";
             SystemMessage sysmsg = new SystemMessage(INSTRUCTION);

         */

        /* Create List of messages
        final List<ChatMessage> messages = ...
        */

        /* Create user msg from file
            String fstring = new String(Files.readAllBytes(Paths.get(DEFAULT_DATAFILE)));

            UserMessage usermsg = new UserMessage(...);
         */

        /*
          Create user msg with the actual prompt
        usermsg = new UserMessage("...");
         */

        /*
          Add all the messages to the list
          messages.add(...);
          messages.add(...);
          messages.add(...);
         */

        /*
                Create a ChatLanguageModel with the apikey and some additional parameters

                String token = Misc.getAPIkey();
                ChatLanguageModel cmodel = OpenAiChatModel.builder()
                        .apiKey(...)
                        .modelName(...)
                        .temperature(...)
                        .timeout(Duration.ofSeconds(30))
                        .maxTokens(...)
                        .build();
         */

        /*
                 Send the messages (one long prompt) and get the completion

                Response<AiMessage> answer = cmodel.generate(...);
                System.out.println(answer.content().text());
         */
    }
}