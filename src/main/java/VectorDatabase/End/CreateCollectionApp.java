package VectorDatabase.End;

import Utilities.Misc;

import java.io.IOException;

public class CreateCollectionApp {
    public static void main(String[] args) throws IOException {
        CreateCollection cc = new CreateCollection(Misc.getAPIkey());

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        int ver = 5;
        cc.createCollection("frank" + ver, "This is test " + ver + " of my create-collection example");
        System.out.println("**********Inserting mydata.txt");
        cc.insert_file("frank" + ver, "./src/main/resources/mydata.txt");
        System.out.println("**********Inserting java22.txt");
        cc.insert_file("frank" + ver, "./src/main/resources/java22.txt");
        System.out.println("**********Inserting Mike info on Java22");
        //cc.insert_file("frank" + ver, "./src/main/resources/java22-mike.txt");
    }
}
