package adapters;

import ClientIce.ClientCallback;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import com.zeroc.Ice.Current;

import java.io.IOException;

import static utils.ClientUtils.generateConsolidatedLog;

public class ClientCallbackAdapter implements ClientCallback {

    private static long responses = 0L;

    @Override
    public void sendIndividualResponse(IndividualResponse r, Current current) {
        System.out.println("Respuesta del server: " + r.value + ", " + r.responseTime);
    }

    @Override
    public void sendMultipleResponse(MultipleResponse r, Current current) {
        System.out.println("Respuesta del server: " + r.responseTime);
        try {
            generateConsolidatedLog(r.values, "Logs-" + responses + ".txt");
            responses++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
