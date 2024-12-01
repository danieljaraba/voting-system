package com.icesi.client.config;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

import java.util.List;

public class ClientConfig {
    public static Communicator initializeCommunicator(String[] args, String configFile, List<String> extraArgs) {
        return com.zeroc.Ice.Util.initialize(args, configFile, extraArgs);
    }

    public static ObjectAdapter createObjectAdapter(Communicator communicator, String adapterName) {
        return communicator.createObjectAdapter(adapterName);
    }
}
