package PrivateChatbot.Begin;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
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
    private String completion_format = "Please respond in english";      // style or language of output
    private List<String> context = new ArrayList<>();
    private MilvusServiceClient mc;
    static final String FIELD1 = "sentence_id";          // chunk identifier - we're using sentences.  Could be other chunk types
    static final String FIELD2 = "sentence_text";       // actual sentence - retrieved later to be sent to the LLM
    static final String FIELD3 = "sentence_vector";     // the embedding vector
    private ChatLanguageModel cmodel;
    private EmbeddingModel emodel;

    PrivateChatbot(String apikey) {
        cmodel = OpenAiChatModel.builder()
                .apiKey(apikey)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(512)
                .build();
        emodel = OpenAiEmbeddingModel.withApiKey(apikey);

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

        /* step 1 - Create a system message to tell the LLM how to behave.
        SystemMessage sysmsg = new SystemMessage(...);
        messages.add(sysmsg);
         */

        /* step 2 - Add the user's prompt to the context (user and assistant msgs)
        addContext(..., ...);
         */

        /* step 3 - add the user's actual prompt to the list of messages
        UserMessage usermsg = new UserMessage(prompt);
        messages.add(usermsg);
        */

        /* step 4 - specify the output format as a system message and add it to the context
        SystemMessage format = new SystemMessage(...);
        messages.add(...);
          */

        //showMessages(messages);     // Just to show the whole prompt sent to the LLM

        /*
         step 5 - Ask the LLM to complete the prompt based on a list of messages and display the result

        Response<AiMessage> answer = cmodel.generate(...);
        System.out.println(answer.content().text());        // text() eliminates the 'noise' results
        */

        /*
          step 6 - Add the LLM's response to a Results list
          Btw... OpenAI's GPT can return multiple completions... others cannot... so this may be unnecessary in the future)
          Update the LLM's response to its history and update the user's history
           
        resultsFromLLM.add(answer.content().text());

        appendAsstHistory(answer.content().text());   // Add the Assistant (LLM) response to the Asst history
        appendUserHistory(prompt);                  // Add the User's prompt to the Prompt history
        */

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
    public void addContext(String prompt, List<ChatMessage> msg) {
        addRAGDataToMsg(msg, searchDB(getCollection(), prompt, getNumMatches()));
        addUserHistory(msg);       // add the user prompt history
        addAsstHistory(msg);       // add the LLM (assistant) history
    }

    public void addRAGDataToMsg(List<ChatMessage> msg, List<String> strmatches) {
        for (String s : strmatches) {
            msg.add(new UserMessage(s));
        }
    }

    public void addAsstHistory(List<ChatMessage> msg) {
        for (int i = 0; i < asstHistory.size(); i++) {
            ChatMessage p = new AiMessage(asstHistory.get(i));
            msg.add(p);
        }
    }

    public void addUserHistory(List<ChatMessage> msg) {
        for (int i = 0; i < userHistory.size(); i++) {
            ChatMessage p = new UserMessage(userHistory.get(i));
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
                case SYSTEM -> System.out.println("SYSTEM: " + cm.text());
                case USER -> System.out.println("  USER: " + cm.text());
                case AI -> System.out.println("  ASST: " + cm.text());
                default -> System.out.println("UNDEFINED ROLE!!!!");
            }
        }
        //mlist.forEach(cm -> System.out.println("MSG: " + cm.text()));
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

    /**
     * sendEmbeddingRequest() - get an embedding vector and convert to List<Float> for Milvus
     *
     * @param msg
     * @return
     */
    public List<Float> sendEmbeddingRequest(String msg) {

        Response<Embedding> response = emodel.embed(msg);
        return response.content().vectorAsList();

        /*List<Float> results = new ArrayList<>();
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
        */

    }
}