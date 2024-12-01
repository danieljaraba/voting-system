package adapters;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolverPrx;

public class ClientPrinterAdapter {
    private final ClientResolverPrx service;

    public ClientPrinterAdapter(Communicator communicator) {
        this.service = ClientResolverPrx.checkedCast(communicator.propertyToProxy("ClientResolver.Proxy"));
        if (this.service == null) {
            throw new Error("Invalid proxy");
        }
    }

    public ClientCallbackPrx initializeCallback(ObjectAdapter adapter) {
        com.zeroc.Ice.Object obj = new ClientCallbackAdapter();
        ObjectPrx objectPrx = adapter.add(obj, com.zeroc.Ice.Util.stringToIdentity("ClientCallback"));
        adapter.activate();
        return ClientCallbackPrx.checkedCast(objectPrx);
    }

    public void sendId(String id, ClientCallbackPrx callbackPrx) {
        service.sendId(id, callbackPrx);
    }

    public void sendFile(String[] list, ClientCallbackPrx callbackPrx) {
        service.sendFile(list, callbackPrx);
    }

    public void setThreadNumber(int threadCount, ClientCallbackPrx callbackPrx) {
        service.setThreadNumber(threadCount, callbackPrx);
    }
}
