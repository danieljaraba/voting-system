package adapter;

import ServerIce.IndividualResponse;
import ServerIce.MasterCallback;
import ServerIce.MultipleResponse;
import com.zeroc.Ice.Current;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MasterCallbackAdapter implements MasterCallback {

    private ConcurrentHashMap<String, tuple> responses = new ConcurrentHashMap<>();

    @Override
    public void sendIndividualResponse(IndividualResponse r, Current current) {

    }

    @Override
    public void sendMultipleResponse(MultipleResponse r, Current current) {

    }
}
