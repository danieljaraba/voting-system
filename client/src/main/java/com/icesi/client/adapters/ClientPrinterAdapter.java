package com.icesi.client.adapters;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import ClientIce.CallbackPrx;
import ClientIce.PrinterPrx;

public class ClientPrinterAdapter {
    private final PrinterPrx service;

    public ClientPrinterAdapter(Communicator communicator) {
        this.service = PrinterPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy"));
        if (this.service == null) {
            throw new Error("Invalid proxy");
        }
    }

    public CallbackPrx initializeCallback(ObjectAdapter adapter) {
        com.zeroc.Ice.Object obj = new ClientCallbackAdapter();
        ObjectPrx objectPrx = adapter.add(obj, com.zeroc.Ice.Util.stringToIdentity("ClientCallback"));
        adapter.activate();
        return CallbackPrx.checkedCast(objectPrx);
    }

    public void sendId(String id, CallbackPrx callbackPrx) {
        service.sendId(id, callbackPrx);
    }

    public void sendFile(String[] list, CallbackPrx callbackPrx) {
        service.sendFile(list, callbackPrx);
    }

    public void setThreadNumber(int threadCount, CallbackPrx callbackPrx) {
        service.setThreadNumber(threadCount, callbackPrx);
    }
}
