package VectorDatabase.End;

import Utilities.Misc;

import java.io.IOException;

public class CreateCollectionApp {
    public static void main(String[] args) throws IOException {
        CreateCollection cc = new CreateCollection(Misc.getAPIkey());

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        String collprefix = "frank";
        int ver = 5;
        String coll = collprefix + ver;     // Just a naming convenience... collection names can be any string

        cc.createCollection(coll, "This is test " + ver + " of my create-collection example");
        System.out.println("**********Inserting mydata.txt");
        cc.insert_file(coll, "./src/main/resources/mydata.txt");
        System.out.println("**********Inserting java22.txt");
        cc.insert_file(coll, "./src/main/resources/java22.txt");
    }
}
