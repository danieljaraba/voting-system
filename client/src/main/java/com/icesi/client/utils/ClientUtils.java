package com.icesi.client.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
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
        return java.nio.file.Files.readAllLines(Paths.get(filePath));
    }

    /**
     * Writes a consolidated log file from a list of input strings.
     *
     * @param inputs     The list of input strings, where each string contains data separated by ',' or ';'.
     * @param outputFile The name of the output log file.
     * @throws IOException If an error occurs during file creation or writing.
     */
    public static void generateConsolidatedLog(String[] inputs, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write the header of the CSV file
            writer.write("CC, Mesa, Puesto, Municipio, Departamento, FactoresPrimosEsPrimo, TiempoRespuesta\n");

            // Process each input string and write it to the file
            for (String input : inputs) {
                // Replace ";" with "," for consistency and split into fields
                String[] fields = input.replace(";", ",").split(",");

                // Extract information from fields
                String cc = fields[0].trim(); // CC
                String mesa = fields[1].trim(); // Mesa
                String puesto = fields[2].trim(); // Puesto
                String municipio = fields[3].trim(); // Municipio
                String departamento = fields[4].trim(); // Departamento
                int isPrime = Integer.parseInt(fields[5].trim()); // Is prime (0 or 1 from server)
                long responseTime = Long.parseLong(fields[6].trim()); // Response time

                // Write the line to the log file
                writer.write(String.format("%s, %s, %s, %s, %s, %d, %d\n",
                        cc, mesa, puesto, municipio, departamento, isPrime, responseTime));
            }
        }
    }
}