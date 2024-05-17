package FileChatbot.End;

import Utilities.Misc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FileChatbotApp {
    public static void main(String[] args) throws IOException {
        Scanner userinput;                                  // user inputted line as a Scanner
        String cmdline;

        String token = Misc.getAPIkey();                    // API key for the cloud GenAI REST service

        String folder = "src/main/resources/";
        List<String> myfiles = Arrays.asList(folder + "java22.txt", folder + "mydata.txt");
        String inst = "You are a extremely helpful Java expert and will respond as one";
        
        FileChatbot pcb = new FileChatbot(token, myfiles, inst);

        while (true) {
            userinput = new Scanner(System.in);
            System.out.print("prompt> ");
            cmdline = userinput.nextLine();

            if (cmdline.isEmpty())
                continue;
            System.out.println(pcb.getCompletion(cmdline));
        }
    }
}
