package VectorDatabase.End;

import Utilities.Misc;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.List;

public class SearchCollectionApp {
    public static void main(String[] args) throws IOException {
        SearchCollection cc = new SearchCollection();

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        String token = Misc.getAPIkey();                                // Connect to OpenAI
        cc.setService(new OpenAiService(token));

        String collection = "frank5";
        cc.loadCollection(collection);

        List<String> matches;
        matches = cc.searchDB(collection, "jdk 22", 5);
        System.out.println("MATCHES ------------------------");
        for (String m : matches) {
            System.out.println("MATCH: [" + m + "]");
        }
    }
}
