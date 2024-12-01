import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import database.DatabaseConfig;
import services.QueryService;
import utils.CacheLoader;
import utils.PrimeFactorizer;

public class Server {
    public static void main(String[] args) {
        DataSource dataSource = DatabaseConfig.getDataSource();
        Connection dbConnection;
        CacheLoader cacheLoader;
        PrimeFactorizer primeFactorizer;
        try {
            cacheLoader = new CacheLoader();
            primeFactorizer = new PrimeFactorizer();
            dbConnection = dataSource.getConnection();
            System.out.println("Connected to the database");
        } catch (Exception e) {
            dbConnection = null;
            cacheLoader = null;
            primeFactorizer = null;
            e.printStackTrace();
        }

        if (dbConnection != null && cacheLoader != null && primeFactorizer != null) {
            QueryService queryManager = new QueryService(dbConnection, cacheLoader, primeFactorizer);
            List<Integer> citizenIds = List.of(603308456, 620074463, 951646451, 421554480, 876515646,
                    482220811, 323290590, 76325522, 757113463, 601107656);
            queryManager.queryWithCache(citizenIds);
            queryManager.queryWithoutCache(citizenIds);
        }
    }
}
