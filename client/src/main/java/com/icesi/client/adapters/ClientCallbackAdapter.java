package com.icesi.client.adapters;

import ClientIce.Callback;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import com.zeroc.Ice.Current;

public class ClientCallbackAdapter implements Callback {

    @Override
    public void sendIndividualResponse(IndividualResponse r, Current current) {

    }

    @Override
    public void sendMultipleResponse(MultipleResponse r, Current current) {

    }
}
