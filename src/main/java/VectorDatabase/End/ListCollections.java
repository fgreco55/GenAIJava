package VectorDatabase.End;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.ShowCollectionsResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.LogLevel;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.ShowCollectionsParam;

public class ListCollections {
    private MilvusServiceClient mc;

    public MilvusServiceClient getMc() {
        return mc;
    }

    public void setMc(MilvusServiceClient mc) {
        this.mc = mc;
    }

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

    /******************************************************************
     *  main
     ******************************************************************/
    public static void main(String[] args) {
        ListCollections lc = new ListCollections();
        lc.setMc(lc.connectToMilvus("localhost", 19530));

        System.out.println("COLLECTIONS ------");
        System.out.println(lc.showCollections());
    }
}
