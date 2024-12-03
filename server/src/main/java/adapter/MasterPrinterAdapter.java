package adapter;

import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolverPrx;

public class MasterPrinterAdapter {
    private final MasterResolverPrx service;

    public MasterPrinterAdapter(MasterResolverPrx service) {
        this.service = service;
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
