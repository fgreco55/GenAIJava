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
        System.out.println("Inserting mydata.txt");
        cc.insert_file("frank" + ver, "./src/main/resources/mydata.txt");
        System.out.println("Inserting java22.txt");
        cc.insert_file("frank" + ver, "./src/main/resources/java22.txt");
    }
}
