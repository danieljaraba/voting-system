package config;

import java.util.List;

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
}
