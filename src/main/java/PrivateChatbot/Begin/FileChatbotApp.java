package PrivateChatbot.Begin;

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

        /*
         Step 1 - create a list of pathnames of files that contain related information.

        String folder = "src/main/resources/";
        List<String> myfiles = ...
        String inst = "You are a extremely helpful Java expert and will respond as one";
        */

        /*
         Step 2 - instantiate a FileChatbot() with the list of files and an instruction (SYSTEM) message

        FileChatbot pcb = new FileChatbot(token, ...);
        */

        while (true) {
            userinput = new Scanner(System.in);
            System.out.print("prompt> ");
            cmdline = userinput.nextLine();

            if (cmdline.isEmpty())
                continue;
            /*
             Step 3 - Call the LLM with the prompt from the user
            System.out.println(pcb.getCompletion(cmdline));
            */
        }
    }
}
