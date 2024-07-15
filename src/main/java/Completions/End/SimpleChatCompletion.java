package Completions.End;

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

        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                .apiKey(token)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(1024)
                .build();

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("You are a extremely funny comedian and will respond as one.");
        messages.add(sysmsg);

        UserMessage usermsg = new UserMessage("How should I learn Java?");
        messages.add(usermsg);

        //sysmsg = new SystemMessage("Please respond in Italian");
        //messages.add(sysmsg);

        Response<AiMessage> answer = cmodel.generate(messages);

        System.out.println(answer.content().text());        // text() eliminates the 'noise' results
    }
}