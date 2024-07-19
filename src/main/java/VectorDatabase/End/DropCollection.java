package VectorDatabase.End;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.ShowCollectionsResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.LogLevel;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.ShowCollectionsParam;

import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class DropCollection {
    private MilvusServiceClient mc;
    static final String COLLECTION_TO_DROP = "frank5";

    public MilvusServiceClient getMc() {
        return mc;
    }

    public void setMc(MilvusServiceClient mc) {
        this.mc = mc;
    }

    /**
     * connectToMilvus - connect to milvus server
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

    /**
     * showCollections - display the current collections in the current milvus database
     * @return
     */
    public String showCollections() {
        String buffer = "";

        R<ShowCollectionsResponse> respShowCollections = mc.showCollections(
                ShowCollectionsParam.newBuilder().build()
        );
        for (int i = 0; i < respShowCollections.getData().getCollectionNamesCount(); i++) {
            buffer += respShowCollections.getData().getCollectionNames(i) + " ";
        }
        return buffer;
    }

    /**
     * drop_collection - remove the collection from the milvus database
     * @param coll
     */
    public void drop_collection(String coll) {
        DropCollectionParam dropParam = DropCollectionParam.newBuilder()
                .withCollectionName(coll)
                .build();
        R<RpcStatus> response = mc.dropCollection(dropParam);
    }

    /******************************************************************
     *  main
     ******************************************************************/
    public static void main(String[] args) {
        DropCollection dc = new DropCollection();
        String collname;
        Scanner userinput;

        dc.setMc(dc.connectToMilvus("localhost", 19530));

        userinput = new Scanner(System.in);
        System.out.print("collection to drop> ");
        collname = userinput.nextLine();

        if (collname.isEmpty())
            System.exit(1);

        System.out.println("COLLECTIONS ------ Before");
        System.out.println(dc.showCollections());

        System.out.println("Dropping collection: " + collname);
        dc.drop_collection(collname);

        System.out.println("COLLECTIONS ------ After");
        System.out.println(dc.showCollections());
    }
}
