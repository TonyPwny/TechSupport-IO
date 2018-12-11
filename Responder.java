import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Anthony Tiongson
 * @version 2018.12.10
 * @originalAuthor David J. Barnes and Michael Kölling.
 * @originalVersion 2016.02.29
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private static final String FILE_OF_MAPPED_RESPONSES = "responseMap.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_MAPPED_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            
            String buffer = reader.readLine();
            String nextLine = buffer;
            String trigger = nextLine;
            String response = null;
            
            while(nextLine != null) {
                
                if(nextLine.equals("\n") || nextLine.equals("")) {
                    
                    responseMap.put(trigger, response);
                    buffer = reader.readLine();
                    nextLine = buffer;
                    trigger = nextLine;
                    response = null;
                }
                else if(response == null && !nextLine.equals("\n") && !nextLine.equals("")) {
                    
                    response = nextLine;
                }
                else {
                    
                    response = response + "\n" + nextLine;
                }
                
                buffer = reader.readLine();
                
                if(buffer == null && response == null) {
                    
                    responseMap.put(trigger, "Wow, not sure about that...");
                }
                else if(buffer == null && response != null) {
                    
                    responseMap.put(trigger, response);
                }
                
                nextLine = buffer;
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_MAPPED_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_MAPPED_RESPONSES);
        }
        // Make sure we have at least one canned response.
        if(responseMap.size() == 0) {
            responseMap.put("bluej", 
                        "Ahhh, BlueJ, yes. We tried to buy out those guys long ago, but\n" +
                        "they simply won't sell... Stubborn people they are. Nothing we can\n" +
                        "do about it, I'm afraid.");
        }
        
        // Have a canned response for empty statements
        responseMap.put("", "Are you still there??");
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            
            String buffer = reader.readLine();
            String nextLine = buffer;
            String response = null;
            
            while(nextLine != null) {
                
                if(response != null && (nextLine.equals("\n") || nextLine.equals(""))) {
                    
                    defaultResponses.add(response);
                    response = null;
                }
                else if(response == null && !nextLine.equals("\n") && !nextLine.equals("")) {
                    
                    response = nextLine;
                }
                else if(response != null && !nextLine.equals("\n") && !nextLine.equals("")) {
                    
                    response = response + "\n" + nextLine;
                }
                
                buffer = reader.readLine();
                
                if(response != null && buffer == null) {
                    
                    defaultResponses.add(response);
                }
                nextLine = buffer;
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
