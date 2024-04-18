package Chapter05.Begin;

import Chapter05.End.Chatbot;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import static Utilities.Misc.getConfigProperties;

public class ChatApp {
    public static void main(String[] args) throws IOException {
            Scanner userinput;                                  // user inputted line as a Scanner
            String cmdline;
            String DEFAULT_CONFIG = "./src/main/resources/genai.properties";
            Properties prop = getConfigProperties(DEFAULT_CONFIG);
            if (prop == (Properties) null) {
                System.err.println("Cannot find OpenAI API key.  Your path to the properties is probably incorrect.");
                System.exit(1);
            }
            String token = prop.getProperty("chatgpt.apikey");

            Chapter05.Begin.Chatbot cb = new Chapter05.Begin.Chatbot(token);

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
