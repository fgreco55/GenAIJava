package VectorDatabase.Begin;

import Utilities.Misc;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateCollection {
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

    private ChatLanguageModel cmodel;
    private EmbeddingModel emodel;

    CreateCollection(String apikey) {
        cmodel = OpenAiChatModel.builder()
                .apiKey(apikey)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(512)
                .build();
        emodel = OpenAiEmbeddingModel.withApiKey(apikey);
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
     * createCollection() - convenience method for creating collections of width OPENAI_VECSIZE
     *
     * @param coll
     * @param colldesc
     */
    public void createCollection(String coll, String colldesc) {
        create_collection(coll, colldesc, OPENAI_VECSIZE);
    }

    /**
     * *********************************************************************
     * create_collection()
     *
     * @param coll     Name of collection
     * @param colldesc Description of the collection
     * @param vecsize  Size of the embedding vector
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

    @NotNull
    public List<Float> getEmbeddingVec(String input) {
        Response<dev.langchain4j.data.embedding.Embedding> response = emodel.embed(input);
        return response.content().vectorAsList();
    }

    /**
     * **************************************************************
     * insert_file() - insert a textfile into the VDB
     *
     * @param collname
     * @param fname
     */
    public void insert_file(String collname, String fname) {
        List<String> sents = Misc.fileToListStrings(fname);        // list of strings from a file
        //List<String> sents = Misc.fileToListParagraphs(fname);       // list of paragraphs from a file

        List<Long> ilist = new ArrayList<>();               // sentence ids - just a running count
        List<String> filesentences = new ArrayList<>();     // sentences to be inserted
        List<List<Float>> veclist = new ArrayList<>();      // embedding vectors for each sentence

        for (int i = 0; i < sents.size(); i++) {
            String mysentence = sents.get(i);

            if (mysentence.isBlank() || mysentence == (String) null || mysentence.isEmpty())
                continue;

            List<Float> embeddings = getEmbeddingVec(mysentence);   // Get the embedding vector for a string
            veclist.add(embeddings);
            ilist.add(Long.parseLong(rowcount + i + ""));
            filesentences.add(mysentence);
            System.out.println("STRING: " + mysentence);
            //System.out.println("VECTOR: " + embeddings);
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
}