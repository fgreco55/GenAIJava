package VectorDatabase.Begin;

import Utilities.Misc;
import VectorDatabase.End.CreateCollection;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;

public class CreateCollectionApp {
    public static void main(String[] args) throws IOException {
        VectorDatabase.End.CreateCollection cc = new CreateCollection();

        /*
         Step 1 - Connect to Milvus

        cc.setMc(cc.connectToMilvus(...);      // Connect to the VDB
        */

        /*
         Step 2 - Initialize OpenAiService()

        String token = Misc.getAPIkey();                                // Connect to OpenAI
        cc.setService(new OpenAiService(...));
        */

        /*
         Step 3 - Create a collection, give it a useful description, and load a file into the collection

        int ver = 5;
        cc.createCollection("frank" + ver, "This is test " + ver + " of my create-collection example");
        cc.insert_file(...);
        */

        /*
         Step 4 (optional) - Add additional text files to the vector database
         cc.insert_file(...);
         cc.insert_file(...);
         cc.insert_file(...);
         ...
         */
    }
}
