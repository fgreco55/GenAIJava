package Utilities;

import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
            System.err.println("Cannot find properties file.  Your path to the properties is probably incorrect.");
            System.exit(1);
        }
        return prop.getProperty(propname);
    }

    public static String getAPIkey() throws IOException {
        //return getProperty(DEFAULT_CONFIG, "chatgpt.apikey");
        return System.getenv("OPENAI_API_KEY");
    }

    public static String getUser() {
        return System.getenv("USER");
    }

    public static double[] Double2double(Double[] indouble) {
        double[] result = new double[indouble.length];
        for (int i = 0; i < indouble.length; i++) {
            result[i] = indouble[i].doubleValue();
        }
        return result;
    }
    public static double[] FloatList2doubleArray(List<Float> floatList) {
        double[] result = new double[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            result[i] = floatList.get(i);
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

    // Function to calculate cosine similarity between two embedding vectors
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

    public static List<Float> Double2Float(List<Double> d) {
        List<Float> flist = new ArrayList<>();
        for (int i = 0; i < d.size(); i++) {
            flist.add(d.get(i).floatValue());
        }
        return flist;
    }

    public static List<Double> Float2Double(List<Float> fl) {
        List<Double> flist = new ArrayList<>();
        for (int i = 0; i < fl.size(); i++) {
            flist.add(fl.get(i).doubleValue());
        }
        return flist;
    }

    /**
     * fileToListStrings() - read a text file into a List of Strings
     * @param fname
     * @return
     */
    public static List<String> fileToListStrings(String fname) {
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

    /**
     * fileToListSentences() - Convert a text file into a List of Sentence Strings
     * @param fname
     * @return
     */
    public static List<String> fileToListSentences(String fname) {
        List<String> sentList = new ArrayList<>();

        List<String> fs = fileToListStrings(fname);
        for (String s : fs) {
            for (String sn : stringToSentences(s)) {
                if (sn.isBlank() || sn.isEmpty())
                    continue;
                else
                    sentList.add(sn);
            }
        }
        return sentList;
    }

    /**
     * stringToParagraph() - parse a string into a List of paragraphs (strings)
     * @param text
     * @return
     */
    public static List<String> stringToParagraphs(String text) {
        List<String> paragraphs = new ArrayList<>();
        if (text == null)
            return paragraphs;

        String[] parsedString = text.split("\\n\\s*\\n");
        for (String s : parsedString) {
            paragraphs.add(s.trim());
        }
        return paragraphs;
    }

    public static List<String> fileToListParagraphs(String fname) {
        List<String> chunkList = new ArrayList<>();

        List<String> fs = fileToListStrings(fname);

        for(String s : fs) {
               List<String> ls = stringToParagraphs(s);
               for(String ts : ls) {
                   chunkList.add(ts);
               }
        }
        return chunkList;
    }

    /**
     * stringToSentences() - parse a string into a List of Sentences (strings)
     * @param text
     * @return
     */
    static List<String> stringToSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null)
            return sentences;

        // Create a BreakIterator for sentence tokenization
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.US);
        sentenceIterator.setText(text);

        // Iterate through sentences and output them one at a time
        int start = sentenceIterator.first();

        for (int end = sentenceIterator.next(); end != BreakIterator.DONE; start = end, end = sentenceIterator.next()) {
            String s = text.substring(start, end).trim();
            //System.out.println("* " + s);
            sentences.add(s);
        }
        return sentences;
    }
}
