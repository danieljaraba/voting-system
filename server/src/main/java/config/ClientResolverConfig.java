package config;

import java.util.ArrayList;
import java.util.List;

import ClientIce.ClientCallbackPrx;
import ServerIce.MasterCallbackPrx;
import adapter.MasterCallbackAdapter;
import adapter.MasterPrinterAdapter;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import ClientIce.ClientResolver;

public class ClientResolverConfig {
    public static void initializeClientResolverAdapter(String[] args, List<String> extraArgs,
            ClientResolver clientResolver) {
        try (Communicator communicator = Util.initialize(args, "config.server", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("ClientResolver");
            adapter.add(clientResolver, Util.stringToIdentity("ClientResolver"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }

    public static MasterPrinterAdapter getMasterPrinterAdapter(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = Util.initialize(args, "config.client", extraArgs)) {
            ObjectAdapter adapter = communicator.createObjectAdapter("Client");
            MasterPrinterAdapter serviceManager = new MasterPrinterAdapter(communicator);

            communicator.waitForShutdown();
            return serviceManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MasterCallbackPrx getMasterCallbackPrx(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = MasterConfig.initializeCommunicator(args, "config.client", extraArgs)) {
            ObjectAdapter adapter = MasterConfig.createObjectAdapter(communicator, "ClientCallback");
            MasterPrinterAdapter serviceManager = new MasterPrinterAdapter(communicator);

            MasterCallbackPrx callbackPrx = serviceManager.initializeCallback(adapter);
            communicator.waitForShutdown();
            return callbackPrx;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
