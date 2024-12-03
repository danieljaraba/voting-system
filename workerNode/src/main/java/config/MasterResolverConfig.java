package config;

import java.util.List;

import ServerIce.MasterResolver;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;


public class MasterResolverConfig {
    public static void initializeClientResolverAdapter(String[] args, List<String> extraArgs,
            MasterResolver clientResolver) {
        try (Communicator communicator = Util.initialize(args, "config.worker", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("MasterResolver");
            adapter.add(clientResolver, Util.stringToIdentity("MasterResolver"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }
}
