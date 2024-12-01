package com.icesi.client.adapters;

import ClientIce.Callback;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import com.zeroc.Ice.Current;

import java.io.IOException;

import static com.icesi.client.utils.ClientUtils.generateConsolidatedLog;

public class ClientCallbackAdapter implements Callback {

    private static long responses = 0L;

    @Override
    public void sendIndividualResponse(IndividualResponse r, Current current) {
        System.out.println("Respuesta del server: " + r.value + ", " + r.responseTime);
    }

    @Override
    public void sendMultipleResponse(MultipleResponse r, Current current) {
        System.out.println("Respuesta del server: " + r.responseTime);
        try {
            generateConsolidatedLog(r.values, "Logs-"+responses+".csv");
            responses++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
