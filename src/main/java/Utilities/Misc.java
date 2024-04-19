package Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Misc {
    private final static String DEFAULT_CONFIG = "./src/main/resources/genai.properties";

    public static Properties getConfigProperties(String fname) throws IOException {
        Properties prop = new Properties();
        InputStream in;
        try {
            in = new FileInputStream(fname);
        } catch (IOException ix) {
            System.err.println("Properties file error: " + ix.getMessage());
            return (Properties) null;
        }

        prop.load(in);
        return prop;
    }

    public static String getProperty(String propfile, String propname) throws IOException {
        Properties prop = getConfigProperties(propfile);
        if (prop == (Properties) null) {
            System.err.println("Cannot find OpenAI API key.  Your path to the properties is probably incorrect.");
            System.exit(1);
        }
        return prop.getProperty(propname);
    }

    public static String getAPIkey() throws IOException {
        return getProperty(DEFAULT_CONFIG, "chatgpt.apikey");
    }

    public static double[] Double2double(Double[] indouble) {
        double[] result = new double[indouble.length];
        for (int i = 0; i < indouble.length; i++) {
            result[i] = indouble[i].doubleValue();
        }
        return result;
    }

    public static double dotProduct(double[] vec1, double[] vec2) {
        double dotProduct = 0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
        }
        return dotProduct;
    }

    // Function to calculate magnitude of a vector
    public static double magnitude(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    // Function to calculate cosine similarity
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = dotProduct(vec1, vec2);
        double magnitudeVec1 = magnitude(vec1);
        double magnitudeVec2 = magnitude(vec2);

        if (magnitudeVec1 == 0 || magnitudeVec2 == 0) {
            return 0; // To avoid division by zero
        } else {
            return dotProduct / (magnitudeVec1 * magnitudeVec2);
        }
    }

    public static List<String> fileToList(String fname) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
}
