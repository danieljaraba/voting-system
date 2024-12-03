import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import ClientIce.ClientResolver;
import concurrency.ThreadPool;
import config.MasterResolverConfig;
import controllers.MasterResolverI;
import database.DatabaseConfig;
import services.QueryService;
import utils.CacheLoader;
import utils.PrimeFactorizer;

public class WorkerNode {
    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        DataSource dataSource = DatabaseConfig.getDataSource();
        Connection dbConnection;
        CacheLoader cacheLoader;
        PrimeFactorizer primeFactorizer;
        ThreadPool threadPool = new ThreadPool(10);
        try {
            cacheLoader = new CacheLoader();
            primeFactorizer = new PrimeFactorizer();
            dbConnection = dataSource.getConnection();
            System.out.println("Worker Connected to the database");
        } catch (Exception e) {
            dbConnection = null;
            cacheLoader = null;
            primeFactorizer = null;
            e.printStackTrace();
        }

        if (dbConnection != null && cacheLoader != null && primeFactorizer != null) {
            QueryService queryManager = new QueryService(dbConnection, cacheLoader, primeFactorizer);
            MasterResolverI masterResolverI = new MasterResolverI(queryManager, threadPool);
            MasterResolverConfig.initializeClientResolverAdapter(args, extraArgs, masterResolverI);
        }
    }
}
