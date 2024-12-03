import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolverPrx;
import adapter.MasterCallbackAdapter;
import adapter.MasterPrinterAdapter;
import config.ClientResolverConfig;
import controllers.ClientResolverI;
import database.DatabaseConfig;
import services.QueryService;
import utils.CacheLoader;
import utils.PrimeFactorizer;

import ClientIce.ClientResolver;
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
            Communicator communicator = null;
            try {
                communicator = Util.initialize(args);
                QueryService queryManager = new QueryService(dbConnection, cacheLoader, primeFactorizer);
                MasterResolverPrx masterResolverPrx = ClientResolverConfig.initializeMasterResolverPrx(communicator,
                        extraArgs);
                MasterPrinterAdapter masterPrinterAdapter = new MasterPrinterAdapter(masterResolverPrx);
                MasterCallbackAdapter masterCallback = new MasterCallbackAdapter();
                MasterCallbackPrx masterCallbackPrx = ClientResolverConfig.initializeMasterCallbackPrx(communicator,
                        extraArgs,
                        masterCallback);
                ClientResolver clientResolver = new ClientResolverI(queryManager, masterPrinterAdapter,
                        masterCallbackPrx,
                        masterCallback, threadPool);
                ClientResolverConfig.initializeClientResolverAdapter(communicator, extraArgs, clientResolver);
                communicator.waitForShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (communicator != null) {
                    communicator.destroy();
                }
            }
        }
    }
}
