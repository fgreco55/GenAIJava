package Completions.Begin;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SimpleChatCompletion {
    public static void main(String[] args) throws IOException {

        String token = System.getenv("OPENAI_API_KEY");

        /*
         Create a list of ChatMessages

         Create a SystemMessage to instruct the LLM on how to act
         Create a UserMessage with your prompt

         Add these messages to the list
        /*
        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage(...);
        UserMessage usermsg = new UserMessage(...);

        messages.add(...);
        messages.add(...);
         */

        /*
        Create a ChatLanguageModel with the apikey and some additional parameters

        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                .apiKey(...)
                .modelName(...)
                .temperature(...)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(...)
                .build();
         */

        /*
         Send the prompt and get the completion

        Response<AiMessage> answer = cmodel.generate(...);
        System.out.println(answer.content().text());
         */


    }
}