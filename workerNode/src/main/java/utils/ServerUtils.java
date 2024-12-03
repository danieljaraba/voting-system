package utils;

import java.util.ArrayList;
import java.util.List;

public class ServerUtils {
    /**
     * Splits a list into chunks of a specified size.
     *
     * @param list      The list to split.
     * @param chunkSize The size of each chunk.
     * @return A list of chunks.
     */
    public static List<List<String>> createChunks(List<String> list, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }
}
