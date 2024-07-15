package VectorDatabase.Begin;

import Utilities.Misc;
import VectorDatabase.End.SearchCollection;

import java.io.IOException;
import java.util.List;

public class SearchCollectionApp {
    public static void main(String[] args) throws IOException {
        /*
         Step 1 - Create a SearchCollection object and connect to Milvus

        SearchCollection cc = new SearchCollection(...);
        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB
        */

        /*
         Step 2 - load a specific collection into memory

        String collection = "frank5";
        cc.loadCollection(...);
        */

        /*
         Step 3 - search the VDB for semantic matches

        List<String> matches;
        matches = cc.searchDB(collection, ...);
        */

        /*
         Step 4 - display the matches

        System.out.println("MATCHES ------------------------");
        for (String m : matches) {
            System.out.println(...);
        }
        */
    }
}
