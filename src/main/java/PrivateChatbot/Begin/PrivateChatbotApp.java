package PrivateChatbot.Begin;

import Utilities.Misc;

import java.io.IOException;
import java.util.Scanner;

public class PrivateChatbotApp {
    public static void main(String[] args) throws IOException {
        Scanner userinput;                                  // user inputted line as a Scanner
        String cmdline;

        String token = Misc.getAPIkey();                    // API key for the cloud GenAI REST service

        /*
         Step 1 - Create an instance of PrivateChatbot

        PrivateChatbot pcb = new PrivateChatbot(token,
            ...
         */

        while (true) {
            userinput = new Scanner(System.in);
            System.out.print("prompt> ");
            cmdline = userinput.nextLine();

            if (cmdline.isEmpty())
                continue;
            /*
              Step 2  Call getCompletion() with the user's prompt

            System.out.println(...);
            */
        }
    }
}
