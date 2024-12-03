package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import ServerIce.MasterCallbackPrx;
import adapter.MasterPrinterAdapter;

import static utils.ServerUtils.createChunks;

public class MasterWorkerDistributed {

    private int chunkSize;
    private MasterPrinterAdapter masterPrinterAdapter;
    private MasterCallbackPrx masterCallbackPrx;
    private ConcurrentHashMap<String, BlockingQueue<List<String>>> responses = new ConcurrentHashMap<>();

    public MasterWorkerDistributed(int chunkSize, MasterPrinterAdapter masterPrinterAdapter,
            MasterCallbackPrx masterCallbackPrx) {
        this.chunkSize = chunkSize;
        this.masterPrinterAdapter = masterPrinterAdapter;
        this.masterCallbackPrx = masterCallbackPrx;
        this.responses = new ConcurrentHashMap<>();
    }

    public List<String> processFile(List<String> ids) throws InterruptedException {
        int targetSize = ids.size();
        List<List<String>> chunks = createChunks(ids, chunkSize);
        String taskId = UUID.randomUUID().toString();
        BlockingQueue<List<String>> resultQueue = new LinkedBlockingQueue<>();
        responses.put(taskId, resultQueue);

        for (List<String> chunk : chunks) {
            masterPrinterAdapter.sendFile(chunk.toArray(new String[0]), masterCallbackPrx, taskId);
        }

        List<String> combinedResults = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(targetSize);

        while (combinedResults.size() < targetSize) {
            List<String> result = resultQueue.take();
            combinedResults.addAll(result);
            latch.countDown();
        }

        latch.await();

        responses.remove(taskId);
        return combinedResults;
    }

    public void receiveFile(String taskId, String[] results) {
        List<String> result = List.of(results);
        BlockingQueue<List<String>> resultQueue = responses.get(taskId);
        if (resultQueue != null) {
            resultQueue.add(result);
        }
    }

}
