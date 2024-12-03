import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import ServerIce.MasterCallback;
import ServerIce.MasterCallbackPrx;
import adapter.MasterCallbackAdapter;
import adapter.MasterPrinterAdapter;
import config.ClientResolverConfig;
import controllers.ClientResolverI;
import database.DatabaseConfig;
import services.QueryService;
import utils.CacheLoader;
import utils.PrimeFactorizer;

import ClientIce.ClientResolver;
import concurrency.MasterWorkerDistributed;
import concurrency.ThreadPool;

public class Server {
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
            System.out.println("Connected to the database");
        } catch (Exception e) {
            dbConnection = null;
            cacheLoader = null;
            primeFactorizer = null;
            e.printStackTrace();
        }

        if (dbConnection != null && cacheLoader != null && primeFactorizer != null) {
            QueryService queryManager = new QueryService(dbConnection, cacheLoader, primeFactorizer);
            MasterPrinterAdapter masterPrinterAdapter = ClientResolverConfig.getMasterPrinterAdapter(args);
            MasterCallbackAdapter masterCallback = new MasterCallbackAdapter();
            MasterCallbackPrx masterCallbackPrx = ClientResolverConfig.getMasterCallbackPrx(args, masterPrinterAdapter,
                    masterCallback);
            ClientResolver clientResolver = new ClientResolverI(queryManager, threadPool, masterPrinterAdapter,
                    masterCallbackPrx, masterCallback);
            ClientResolverConfig.initializeClientResolverAdapter(args, extraArgs, clientResolver);
        }
    }
}
