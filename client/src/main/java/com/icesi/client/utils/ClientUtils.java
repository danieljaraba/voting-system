package com.icesi.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for processing client-related tasks, such as reading and mapping data.
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
        // Read all lines from the file and return them as a list of strings
        return Files.readAllLines(Paths.get(filePath));
    }

}
