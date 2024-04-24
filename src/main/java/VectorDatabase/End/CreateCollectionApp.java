package VectorDatabase.End;

import Utilities.Misc;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;

public class CreateCollectionApp {
    public static void main(String[] args) throws IOException {
        CreateCollection cc = new CreateCollection();

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        String token = Misc.getAPIkey();                                // Connect to OpenAI
        cc.setService(new OpenAiService(token));

        int ver = 5;
        cc.createCollection("frank" + ver, "This is test " + ver + " of my create-collection example");
        cc.insert_file("frank" + ver, "./src/main/resources/mydata.txt");
    }
}
