package adapter;

import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolverPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;

public class MasterPrinterAdapter {
    private final MasterResolverPrx service;

    public MasterPrinterAdapter(Communicator communicator) {
        this.service = MasterResolverPrx.checkedCast(communicator.propertyToProxy("MasterResolver.Proxy"));
        if (this.service == null) {
            throw new Error("Invalid proxy");
        }
    }

    public MasterCallbackPrx initializeCallback(ObjectAdapter adapter) {
        com.zeroc.Ice.Object obj = new MasterCallbackAdapter();
        ObjectPrx objectPrx = adapter.add(obj, com.zeroc.Ice.Util.stringToIdentity("MasterCallback"));
        adapter.activate();
        return MasterCallbackPrx.checkedCast(objectPrx);
    }

    public void sendId(String id, MasterCallbackPrx callbackPrx) {
        service.sendId(id, callbackPrx);
    }

    public void sendFile(String[] list, MasterCallbackPrx callbackPrx) {
        service.sendFile(list, callbackPrx);
    }

    public void setThreadNumber(int threadCount, MasterCallbackPrx callbackPrx) {
        service.setThreadNumber(threadCount, callbackPrx);
    }

    public void setChunkSize(int chunkSize, MasterCallbackPrx callbackPrx) {
        service.setChunkSize(chunkSize, callbackPrx);
    }
}
