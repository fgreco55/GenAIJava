package VectorDatabase.End;

import Utilities.Misc;

import java.io.IOException;
import java.util.List;

public class SearchCollectionApp {
    public static void main(String[] args) throws IOException {
        SearchCollection cc = new SearchCollection(Misc.getAPIkey());

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        String collection = "frank5";
        cc.loadCollection(collection);

        List<String> matches;
        matches = cc.searchDB(collection, "Java", 10);      // Find top N-most similar entries in the VDB
        System.out.println("MATCHES ------------------------");
        for (String m : matches) {
            System.out.println("MATCH: [" + m + "]");
        }
    }
}
