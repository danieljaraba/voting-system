package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CacheLoader {

    private Map<Integer, String> cache;

    public CacheLoader() throws IOException {
        cache = new HashMap<>();
        loadCache("db_cache.txt");
    }

    public void loadCache(String cacheFile) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(cacheFile);
        if (is == null) {
            throw new IOException("Archivo de caché no encontrado: " + cacheFile);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    int key = Integer.parseInt(parts[0]);
                    String value = parts[1];
                    cache.put(key, value);
                }
            }
        }
    }

    public String getCacheValue(int key) {
        return cache.get(key);
    }
}
