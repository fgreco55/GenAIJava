package Chatbot.End;

import Utilities.Misc;
import java.io.IOException;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) throws IOException {
            Scanner userinput;                                  // user inputted line as a Scanner
            String cmdline;

            String token = Misc.getAPIkey();                    // API key for the cloud GenAI REST service

            Chatbot cb = new Chatbot(token);

            while (true) {

                userinput = new Scanner(System.in);
                System.out.print("prompt> ");
                cmdline = userinput.nextLine();

                if (cmdline.isEmpty())
                    continue;
                System.out.println(cb.getCompletion(cmdline));
            }
        }
}
