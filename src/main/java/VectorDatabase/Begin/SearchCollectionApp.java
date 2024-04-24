package VectorDatabase.Begin;

import Utilities.Misc;
import VectorDatabase.End.SearchCollection;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.List;

public class SearchCollectionApp {
    public static void main(String[] args) throws IOException {
        SearchCollection cc = new SearchCollection();

        /*
         Step 1 - connect to Milvus

        cc.setMc(cc.connectToMilvus(...);      // Connect to the VDB
        */

        /*
         Step 2 - initialize the OpenAiService() library

        String token = Misc.getAPIkey();                                // Connect to OpenAI
        cc.setService(...);
        */

        /*
         Step 3 - load a specific collection into memory

        String collection = "frank5";
        cc.loadCollection(...);
        */

        /*
         Step 4 - search the VDB for semantic matches

        List<String> matches;
        matches = cc.searchDB(collection, ...);
        */

        /*
         Step 5 - display the matches

        System.out.println("MATCHES ------------------------");
        for (String m : matches) {
            System.out.println(...);
        }
        */
    }
}
