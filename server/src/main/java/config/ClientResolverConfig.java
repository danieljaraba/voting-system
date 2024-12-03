package config;

import java.util.List;
import java.util.logging.Logger;

import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolverPrx;
import adapter.MasterCallbackAdapter;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;

import ClientIce.ClientResolver;

public class ClientResolverConfig {

    private static final Logger logger = Logger.getLogger(ClientResolverConfig.class.getName());

    private static void validateExtraArgs(List<String> extraArgs) {
        if (!extraArgs.isEmpty()) {
            System.err.println("too many arguments");
            for (String v : extraArgs) {
                System.out.println(v);
            }
        }
    }

    public static void initializeClientResolverAdapter(Communicator communicator, List<String> extraArgs,
            ClientResolver clientResolver) {
        try {
            validateExtraArgs(extraArgs);

            ObjectAdapter adapter = communicator.createObjectAdapter("ClientResolver");
            String identity = "clientResolver1"; // Identidad fija
            adapter.add(clientResolver, Util.stringToIdentity(identity));
            adapter.activate();
            logger.info("ClientResolver adapter activated successfully with identity: " + identity);
        } catch (Exception e) {
            logger.severe("Error in initializeClientResolverAdapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static MasterResolverPrx initializeMasterResolverPrx(Communicator communicator, List<String> extraArgs) {
        try {
            validateExtraArgs(extraArgs);

            QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
            if (query == null) {
                logger.severe("IceGrid/Query is not available");
                return null;
            }

            MasterResolverPrx service = MasterResolverPrx
                    .checkedCast(query.findObjectByType("::ServerIce::MasterResolver"));
            if (service == null) {
                logger.severe("MasterResolver service not found");
            }
            return service;
        } catch (Exception e) {
            logger.severe("Error in initializeMasterResolverPrx: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static MasterCallbackPrx initializeMasterCallbackPrx(Communicator communicator, List<String> extraArgs,
            MasterCallbackAdapter masterCallback) {

        try {
            validateExtraArgs(extraArgs);

            ObjectAdapter adapter = communicator.createObjectAdapter("MasterCallback");
            String identity = "masterCallback1"; // Identidad fija
            ObjectPrx objectPrx = adapter.add(masterCallback, Util.stringToIdentity(identity));
            adapter.activate();

            MasterCallbackPrx callbackPrx = MasterCallbackPrx.checkedCast(objectPrx);
            if (callbackPrx == null) {
                logger.severe("Failed to cast ObjectPrx to MasterCallbackPrx for identity: " + identity);
            }
            return callbackPrx;
        } catch (Exception e) {
            logger.severe("Error in initializeMasterCallbackPrx: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
