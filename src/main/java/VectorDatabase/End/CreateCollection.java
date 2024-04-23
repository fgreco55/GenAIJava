package VectorDatabase.End;

import Utilities.Misc;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.MutationResult;
import io.milvus.param.*;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateCollection {
    private OpenAiService service;
    private MilvusServiceClient mc;
    private long rowcount = 0;

    static final String FIELD1 = "sentence_id";          // chunk identifier - we're using sentences.  Could be other chunk types
    static final String FIELD2 = "sentence_text";       // actual sentence - retrieved later to be sent to the LLM
    static final String FIELD3 = "sentence_vector";     // the embedding vector
    static final int MAX_SENTENCE_LENGTH = 5120;
    static final int OPENAI_VECSIZE = 1536;
    static final String PARTITION_NAME = "sentence_partition";

    public MilvusServiceClient getMc() {
        return mc;
    }

    public void setMc(MilvusServiceClient mc) {
        this.mc = mc;
    }

    /** **************************************************************
     * connectToMilvus() - connect to a running Milvus instance
     * @param host
     * @param port
     * @return
     */
    private MilvusServiceClient connectToMilvus(String host, int port) {
        mc = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .build()
        );
        mc.setLogLevel(LogLevel.Debug);
        return mc;
    }

    /** **********************************************************************
     * create_collection()
     * @param coll      Name of collection
     * @param colldesc  Description of the collection
     * @param vecsize   Size of the embedding vector
     */
    public void create_collection(String coll, String colldesc, int vecsize) {
        R<RpcStatus> response = null;

        FieldType fieldType1 = FieldType.newBuilder()        // schema for the id of the entry
                .withName(FIELD1)
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();
        FieldType fieldType2 = FieldType.newBuilder()       // schema for chars in sentence
                .withName(FIELD2)
                .withDataType(DataType.VarChar)
                .withMaxLength(MAX_SENTENCE_LENGTH)         // VarChar requires "withMaxLength()
                .build();
        FieldType fieldType3 = FieldType.newBuilder()       // schema for the actual vector
                .withName(FIELD3)
                .withDataType(DataType.FloatVector)
                .withDimension(vecsize)
                .build();

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()      // Create collection
                .withCollectionName(coll)
                .withDescription(colldesc)
                .withShardsNum(2)
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .withEnableDynamicField(true)
                .build();

        response = mc.createCollection(createCollectionReq);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println("***FAILURE: " + response.getMessage());
        }
        mc.createPartition(
                CreatePartitionParam.newBuilder()
                        .withCollectionName(coll)
                        .withPartitionName(PARTITION_NAME)
                        .build()
        );
        createIndex(coll);
    }

    /*****************************************************************
     * createIndex() - create index on vector field  - for performance
     * @param coll
     ************************************************************/
    public void createIndex(String coll) {
        CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                .withCollectionName(coll)
                .withFieldName(FIELD3)
                .withIndexType(IndexType.FLAT)      // Brute force search... slow
                .withMetricType(MetricType.L2)
                .withSyncMode(Boolean.TRUE)
                .build();
        R<RpcStatus> createIndexR = mc.createIndex(indexParam);

        if (createIndexR.getStatus() != R.Status.Success.getCode()) {
            System.out.print("***ERROR:  " + createIndexR.getMessage());
        }
    }

    /** *****************************************************************************
     * getEmbeddingVec() - Get an embedding vector from the OpenAI embedding service
     * @param service
     * @param input
     * @return
     */
    public static List<Embedding> getEmbeddingVec(@NotNull OpenAiService service, String input) {

        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-3-small")
                .input(Collections.singletonList(input))
                .build();

        List<Embedding> embeddings = service.createEmbeddings(embeddingRequest).getData();

        return embeddings;
    }

    /** ***************************************************************************************************
     * getEmbeddingVecAsFloat() - Convenience method since Milvus expects Floats but OpenAI returns Doubles
     * @param service
     * @param input
     * @return
     */
    @NotNull
    public static List<Float> getEmbeddingVecAsFloat(OpenAiService service, String input) {
        List<Float> results = new ArrayList<>();

        List<Embedding> embedding = getEmbeddingVec(service, input);
        List<Double> emb = embedding.get(0).getEmbedding();     // OpenAI returns Doubles... Milvus wants Floats...
        List<Float> newb = Misc.Double2Float(emb);
        int size = embedding.get(0).getEmbedding().size();
        for (int i = 0; i < size; i++) {
            results.add(newb.get(i));
        }
        return results;
    }

    /** ***************************************************************
     * insert_file() - insert a textfile into the VDB
     * @param collname
     * @param fname
     */
    public void insert_file(String collname, String fname) {
        List<String> sents = Misc.fileToList(fname);        // list of strings from a file

        List<Long> ilist = new ArrayList<>();               // sentence ids - just a running count
        List<String> filesentences = new ArrayList<>();     // sentences to be inserted
        List<List<Float>> veclist = new ArrayList<>();      // embedding vectors for each sentence

        for (int i = 0; i < sents.size(); i++) {
            String mysentence = sents.get(i);

            if (mysentence.isBlank() || mysentence == (String)null || mysentence.isEmpty())
                continue;

            List<Float> embeddings = getEmbeddingVecAsFloat(service, mysentence);   // Get the embedding vector for a string
            veclist.add(embeddings);
            ilist.add(Long.parseLong(rowcount + i + ""));
            filesentences.add(mysentence);

            System.out.println("STRING: " + mysentence);
           // System.out.println("VECTOR: " + embeddings);
        }

        List<InsertParam.Field> fieldsInsert = new ArrayList<>();
        fieldsInsert.add(new InsertParam.Field(FIELD1, ilist));
        fieldsInsert.add(new InsertParam.Field(FIELD2, filesentences));
        fieldsInsert.add(new InsertParam.Field(FIELD3, veclist));

        InsertParam param = InsertParam.newBuilder()
                .withCollectionName(collname)
                .withFields(fieldsInsert)
                .build();

        R<MutationResult> resp = mc.insert(param);
        if (resp.getStatus() != R.Status.Success.getCode()) {
            System.err.println("***ERROR:  Cannot insert into collection.");
        }
    }


    /******************************************************************
     *  main method
     ******************************************************************/
    public static void main(String[] args) throws IOException {
        CreateCollection cc = new CreateCollection();

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        String token = Misc.getAPIkey();                                // Connect to OpenAI
        cc.service = new OpenAiService(token);

        int ver = 3;
        cc.create_collection("frank"+ver, "This is test " + ver + " of my create-collection example", OPENAI_VECSIZE);
        cc.insert_file("frank"+ver, "./src/main/resources/mydata.txt");
    }
}
