package VectorDatabase.Begin;

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
      static final String COLLECTION_TO_DROP = "temp";

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
          ListCollections lc = new ListCollections();

          /*
           Step 1 - Connect to Milvus

          lc.setMc(lc.connectToMilvus(...));
          */

          /*
           Step 2 - Show all the collections in the database

          System.out.println("COLLECTIONS ------ Before");
          System.out.println();
          */

          /*
           Step 3 - Drop a collection

          System.out.println("Dropping collection: " + COLLECTION_TO_DROP);
          lc.drop_collection(...);
          */

          /*
           Step 4 - Show all the collections again to verify the drop

          System.out.println("COLLECTIONS ------ After");
          System.out.println();
          */
      }
}
