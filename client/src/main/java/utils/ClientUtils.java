package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for processing client-related tasks, such as reading and
 * mapping data.
 */
public class ClientUtils {

    /**
     * Reads a text file where each line represents an ID and returns a list of IDs.
     *
     * @param filePath The path to the text file.
     * @return A list of IDs read from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public static List<String> readIdsFromFile(String filePath) throws IOException {
        return java.nio.file.Files.readAllLines(Paths.get(filePath));
    }

    /**
     * Writes a consolidated log file from a list of input strings.
     *
     * @param inputs     The list of input strings, where each string contains data
     *                   separated by ',' or ';'.
     * @param outputFile The name of the output log file.
     * @throws IOException If an error occurs during file creation or writing.
     */
    public static void generateConsolidatedLog(String[] inputs, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            // Process each input string and write it to the file
            for (String input : inputs) {
                // Write the line to the log file
                writer.write(input);
            }
        }
    }
}