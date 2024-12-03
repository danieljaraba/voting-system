package adapter;

import ServerIce.IndividualResponse;
import ServerIce.MasterCallback;
import ServerIce.MultipleResponse;
import concurrency.MasterWorkerDistributed;

import com.zeroc.Ice.Current;

public class MasterCallbackAdapter implements MasterCallback {

    private MasterWorkerDistributed masterWorkerDistributed;

    public MasterCallbackAdapter() {
        this.masterWorkerDistributed = null;
    }

    @Override
    public void sendIndividualResponse(IndividualResponse r, Current current) {

    }

    @Override
    public void sendMultipleResponse(MultipleResponse r, String taskId, Current current) {
        if (masterWorkerDistributed != null) {
            masterWorkerDistributed.receiveFile(taskId, r.values);
        }
    }

    public void setMasterWorkerDistributed(MasterWorkerDistributed masterWorkerDistributed) {
        this.masterWorkerDistributed = masterWorkerDistributed;
    }
}
