package adapters;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.IceGrid.QueryPrx;
import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolverPrx;

public class ClientPrinterAdapter {
    private final ClientResolverPrx service;

    public ClientPrinterAdapter(Communicator communicator) {
        try {
            QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
            if (query == null) {
                throw new Error("IceGrid/Query is not available");
            }
            System.out.println("Query: " + query);
            this.service = ClientResolverPrx.checkedCast(query.findObjectByType("::ClientIce::ClientResolver"));
            if (this.service == null) {
                throw new Error("ClientResolver service not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to initialize ClientPrinterAdapter: " + e.getMessage());
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

    public void setChunkSize(int chunkSize, ClientCallbackPrx callbackPrx) {
        service.setChunkSize(chunkSize, callbackPrx);
    }
}
