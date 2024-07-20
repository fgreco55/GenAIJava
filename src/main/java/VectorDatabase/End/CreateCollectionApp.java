package VectorDatabase.End;

import Utilities.Misc;

import java.io.IOException;
import java.util.Scanner;

public class CreateCollectionApp {
    public static void main(String[] args) throws IOException {
        CreateCollection cc = new CreateCollection(Misc.getAPIkey());
        Scanner userinput;
        String collname;
        String username = Misc.getUser();

        cc.setMc(cc.connectToMilvus("localhost", 19530));      // Connect to the VDB

        userinput = new Scanner(System.in);
        System.out.print("collection to create> ");
        collname = userinput.nextLine();

        cc.createCollection(collname, "Created by " + username + " from CreateCollectionApp.java");
        System.out.println("**********Inserting mydata.txt");
        cc.insert_file(collname, "./src/main/resources/mydata.txt");
        System.out.println("**********Inserting java22.txt");
        cc.insert_file(collname, "./src/main/resources/java22.txt");
    }
}
