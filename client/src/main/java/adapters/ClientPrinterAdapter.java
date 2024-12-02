package adapters;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolverPrx;

public class ClientPrinterAdapter {
    private final ClientResolverPrx service;
    private final static int MAX_SIZE_FILE = 1000;

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
        if (list.length > MAX_SIZE_FILE) {
            for (int i = 0; i < list.length; i += MAX_SIZE_FILE) {
                String[] chunk = new String[Math.min(MAX_SIZE_FILE, list.length - i)];
                System.arraycopy(list, i, chunk, 0, chunk.length);
                if (i + MAX_SIZE_FILE < list.length) {
                    service.sendFile(chunk, callbackPrx, false);
                } else {
                    service.sendFile(chunk, callbackPrx, true);
                }
            }
        } else {
            service.sendFile(list, callbackPrx, true);
        }
    }

    public void setThreadNumber(int threadCount, ClientCallbackPrx callbackPrx) {
        service.setThreadNumber(threadCount, callbackPrx);
    }
}
