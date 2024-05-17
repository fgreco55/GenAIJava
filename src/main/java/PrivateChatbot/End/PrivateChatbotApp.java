package PrivateChatbot.End;

import Utilities.Misc;
import java.io.IOException;
import java.util.Scanner;

public class PrivateChatbotApp {
    public static void main(String[] args) throws IOException {
           Scanner userinput;                                  // user inputted line as a Scanner
           String cmdline;

           String token = Misc.getAPIkey();                    // API key for the cloud GenAI REST service

           PrivateChatbot pcb = new PrivateChatbot(token,
                   "frank5",    // The specific collection associated with this PCB (has to exist)
                   5,                   // At most return this many results from the VDB
                   "You are a extremely helpful Java expert and will respond as one");

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
