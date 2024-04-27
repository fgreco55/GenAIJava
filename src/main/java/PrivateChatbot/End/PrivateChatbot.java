package PrivateChatbot.End;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;

import java.time.Duration;
import java.util.*;

import static Utilities.Misc.Double2Float;

public class PrivateChatbot {
    private final List<String> asstHistory;      // history of all responses from LLM
    private final List<String> userHistory;    // history of all prompts sent to LLM
    private String instruction = "You are a extremely helpful Java expert and will respond as one.";        // additional behavior (system)
    private String collection;
    private int numMatches = 5;
    private float radius = .5f;                 // "average" similarity is the minimum returned
    private String completion_format = "";      // style or language of output
    private List<String> context = new ArrayList<>();
    private OpenAiService service;
    private MilvusServiceClient mc;
    static final String FIELD1 = "sentence_id";          // chunk identifier - we're using sentences.  Could be other chunk types
    static final String FIELD2 = "sentence_text";       // actual sentence - retrieved later to be sent to the LLM
    static final String FIELD3 = "sentence_vector";     // the embedding vector

    PrivateChatbot(String apikey) {
        service = new OpenAiService(apikey, Duration.ofSeconds(30));
        mc = connectToMilvus("localhost", 19530);
        asstHistory = new ArrayList<>();
        userHistory = new ArrayList<>();
    }

    PrivateChatbot(String apikey, String collection, int numMatches) {
        this(apikey);
        setCollection(collection);
        setNumMatches(numMatches);
    }

    PrivateChatbot(String apikey, String collection, float radius, int numMatches, String instruction) {
        this(apikey);
        setCollection(collection);
        setRadius(radius);
        setNumMatches(numMatches);
        setInstruction(instruction);
    }

    PrivateChatbot(String apikey, String collection, int numMatches, String instruction) {
        this(apikey);
        setCollection(collection);
        setNumMatches(numMatches);
        setInstruction(instruction);
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getNumMatches() {
        return numMatches;
    }

    public void setNumMatches(int numMatches) {
        this.numMatches = numMatches;
    }

    public MilvusServiceClient getMc() {
        return mc;
    }

    public void setMc(MilvusServiceClient mc) {
        this.mc = mc;
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
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
        final ChatMessage format = new ChatMessage(ChatMessageRole.USER.value(), completion_format);
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
     * ************************************************************************************
     * addContext - add the histories to the current Context
     *
     * @param msg
     */
    public void addContext(String prompt, List<ChatMessage> msg) {
        addRAGDataToMsg(msg, searchDB(getCollection(), prompt, getNumMatches()));
        addUserHistory(msg);       // add the user prompt history
        addAsstHistory(msg);       // add the LLM (assistant) history
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
     * *************************************************************
     * connectToMilvus() - connect to a running Milvus instance
     *
     * @param host
     * @param port
     * @return
     */
    public MilvusServiceClient connectToMilvus(String host, int port) {
        mc = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .build()
        );
        mc.setLogLevel(LogLevel.Debug);
        return mc;
    }

    /**
     * loadCollection() - collection must be loaded into memory to work
     *
     * @param coll
     */
    public void loadCollection(String coll) {
        R<RpcStatus> loadc = mc.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName(coll)
                        .build()
        );
        if (loadc.getStatus() != R.Status.Success.getCode()) {
            System.out.print("***ERROR:  " + loadc.getMessage());
        }
    }

    /**
     * searchDB_using_targetvectors() - Using a List of vectors, search a collection for 'max' matches
     *
     * @param coll
     * @param vec
     * @param max
     * @return
     */
    public List<String> searchDB_using_targetvectors(String coll, List<List<Float>> vec, int max) {
        loadCollection(coll);

        SearchParam param = SearchParam.newBuilder()
                .withCollectionName(coll)
                .withMetricType(MetricType.L2)
                .withTopK(max)                      // confirm this is the number of MAX MATCHES in the VDB
                .withVectors(vec)                   // Search closest neighbors to these
                .withVectorFieldName(FIELD3)        // ... using this field (my embedding array)
                .withConsistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                .addOutField(FIELD2)                // IMPORTANT FIELD TO ADD - you want the strings returned
                .withParams("{\"radius\": " + getRadius() + "}")    // filter results to slightly better than average
                .build();

        R<SearchResults> resp = mc.search(param);
        SearchResultsWrapper wrapper;

        if (resp.getStatus() != R.Status.Success.getCode()) {
            System.err.println("***ERROR: Cannot Search. " + resp.getMessage());
            return new ArrayList<>();
        } else {
            wrapper = new SearchResultsWrapper(resp.getData().getResults());
            if (wrapper.getRowRecords().size() == 0) {
                return new ArrayList<>();
            }
            return getSearchData(resp, vec.size());     // get the actual data
        }
    }

    /**
     * ***********************************************************************
     * getSearchData() - extract the highest semantic scores
     *
     * @param resp
     * @param size
     * @return
     */
    private List<String> getSearchData(R<SearchResults> resp, int size) {
        SearchResultsWrapper wrapper = new SearchResultsWrapper(resp.getData().getResults());
        List<String> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            for (SearchResultsWrapper.IDScore score : scores) {
                System.out.println("[" + score.getScore() + "]" + "[" + score.get(FIELD2) + "]");
                results.add((String) score.get(FIELD2));    // the sentence
            }
        }
        return results;
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

    /**
     * searchDB() - convenience method - search for "max" matches on a string in collection "coll"
     *
     * @param coll   Name of the collection
     * @param target Find matches based on this string
     * @param max    At most return this many matches
     * @return
     */
    public List<String> searchDB(String coll, String target, int max) {
        List<List<Float>> smallvec = new ArrayList<>();
        smallvec.add(sendEmbeddingRequest(target));

        return searchDB_using_targetvectors(coll, smallvec, max);   // Give me at most max best matches
    }
}
