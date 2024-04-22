package Chatbot.Begin;

import Chatbot.End.Chatbot;
import Utilities.Misc;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import static Utilities.Misc.getConfigProperties;

public class ChatApp {
    public static void main(String[] args) throws IOException {
            Scanner userinput;                                  // user inputted line as a Scanner
            String cmdline;
            String token = Misc.getAPIkey();

            /*
             Create an instance of ChatApp

            //Chatbot cb = ...
            */

            while (true) {

                userinput = new Scanner(System.in);
                System.out.print("prompt> ");
                cmdline = userinput.nextLine();

                if (cmdline.isEmpty())
                    continue;
                /*
                 Display the completion
                   System.out.println(..getCompletion(cmdline));
                 */

            }
        }
}
