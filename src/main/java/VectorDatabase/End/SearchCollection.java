package VectorDatabase.End;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Utilities.Misc.Double2Float;

public class SearchCollection {
    private MilvusServiceClient mc;
    static final String FIELD1 = "sentence_id";          // chunk identifier - we're using sentences.  Could be other chunk types
    static final String FIELD2 = "sentence_text";       // actual sentence - retrieved later to be sent to the LLM
    static final String FIELD3 = "sentence_vector";     // the embedding vector
    private EmbeddingModel emodel;


    SearchCollection(String apikey) {
        emodel = OpenAiEmbeddingModel.withApiKey(apikey);
    }

    public MilvusServiceClient getMc() {
        return mc;
    }

    public void setMc(MilvusServiceClient mc) {
        this.mc = mc;
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
                .addOutField(FIELD2)                // IMPORTANT FIELD TO ADD!
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
                //System.out.println("[" + score.getScore() + "]" + "[" + score.get(FIELD2) + "]");
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
        Response<Embedding> response = emodel.embed(msg);
        return response.content().vectorAsList();
    }

    /**
     * searchDB() - search for "max" matches on a string in collection "coll"
     *
     * @param coll
     * @param target
     * @param max
     * @return
     */
    public List<String> searchDB(String coll, String target, int max) {
        List<List<Float>> smallvec = new ArrayList<>();
        smallvec.add(sendEmbeddingRequest(target));

        return searchDB_using_targetvectors(coll, smallvec, max);   // Give me at most max best matches
    }
}
