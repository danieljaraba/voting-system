package config;

import java.util.ArrayList;
import java.util.List;

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
        try (Communicator communicator = Util.initialize(args, extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("ClientResolver");
            adapter.add(clientResolver, Util.stringToIdentity("clientResolver1"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }

    public static MasterPrinterAdapter getMasterPrinterAdapter(String[] args) {
        List<String> extraArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, extraArgs)) {
            MasterPrinterAdapter serviceManager = new MasterPrinterAdapter(communicator);
            return serviceManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MasterCallbackPrx getMasterCallbackPrx(String[] args, MasterPrinterAdapter masterPrinterAdapter,
            MasterCallbackAdapter masterCallback) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = MasterConfig.initializeCommunicator(args, extraArgs)) {
            ObjectAdapter adapter = MasterConfig.createObjectAdapter(communicator, "MasterCallback");

            MasterCallbackPrx callbackPrx = masterPrinterAdapter.initializeCallback(adapter, masterCallback);
            communicator.waitForShutdown();
            return callbackPrx;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
