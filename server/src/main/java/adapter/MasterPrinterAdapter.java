package adapter;

import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolverPrx;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.IceGrid.QueryPrx;

public class MasterPrinterAdapter {
    private final MasterResolverPrx service;

    public MasterPrinterAdapter(Communicator communicator) {
        QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
        this.service = MasterResolverPrx.checkedCast(query.findObjectByType("::ServerIce::MasterResolver"));
        if (this.service == null) {
            throw new Error("Invalid proxy");
        }
    }

    public MasterCallbackPrx initializeCallback(ObjectAdapter adapter, MasterCallbackAdapter masterCallback) {
        ObjectPrx objectPrx = adapter.add(masterCallback, com.zeroc.Ice.Util.stringToIdentity("masterCallback1"));
        adapter.activate();
        return MasterCallbackPrx.checkedCast(objectPrx);
    }

    public void sendId(String id, MasterCallbackPrx callbackPrx) {
        service.sendId(id, callbackPrx);
    }

    public void sendFile(String[] list, MasterCallbackPrx callbackPrx, String taskId) {
        service.sendFile(list, callbackPrx, taskId);
    }

    public void setThreadNumber(int threadCount, MasterCallbackPrx callbackPrx) {
        service.setThreadNumber(threadCount, callbackPrx);
    }

    public void setChunkSize(int chunkSize, MasterCallbackPrx callbackPrx) {
        service.setChunkSize(chunkSize, callbackPrx);
    }
}
