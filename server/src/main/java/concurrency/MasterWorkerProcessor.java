package concurrency;

import ServerIce.MasterCallbackPrx;
import adapter.MasterPrinterAdapter;
import services.QueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.ServerUtils.createChunks;

/**
 * Handles the Master-Worker pattern for processing large lists of IDs.
 */
public class MasterWorkerProcessor {

    private final QueryService queryService;
    private final ThreadPool threadPool;
    private int chunkSize;
    private final MasterCallbackPrx masterCallbackPrx;
    private final MasterPrinterAdapter masterPrinterAdapter;

    /**
     * Constructs a MasterWorkerProcessor.
     *
     * @param queryService The service used to query data for each chunk.
     * @param threadPool   The thread pool for managing worker threads.
     * @param chunkSize    The size of each chunk to be processed.
     */
    public MasterWorkerProcessor(QueryService queryService,
                                 ThreadPool threadPool,
                                 int chunkSize,
                                 MasterPrinterAdapter masterPrinterAdapter,
                                 MasterCallbackPrx masterCallbackPrx) {
        this.queryService = queryService;
        this.threadPool = threadPool;
        this.chunkSize = chunkSize;
        this.masterPrinterAdapter = masterPrinterAdapter;
        this.masterCallbackPrx = masterCallbackPrx;
    }

    /**
     * Processes a large list of IDs using the Master-Worker pattern.
     *
     * @param ids The list of IDs to process.
     * @return The combined results from all chunks.
     */
    public List<String> processFile(List<String> ids) {
        List<List<String>> chunks = createChunks(ids, chunkSize);

        BlockingQueue<List<String>> resultQueue = new LinkedBlockingQueue<>();

        if (ids.size()<65535) {

            List<String> masterChunk = null;
            if (!chunks.isEmpty()) {
                masterChunk = chunks.remove(0);
            }

            for (List<String> chunk : chunks) {
                threadPool.execute(() -> {
                    try {
                        List<String> result = queryService.queryMultipleDocuments(chunk);
                        resultQueue.put(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            resultQueue.put(new ArrayList<>()); // Add empty result to avoid blocking
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }

            List<String> masterResults = new ArrayList<>();
            if (masterChunk != null) {
                masterResults = queryService.queryMultipleDocuments(masterChunk);
            }

            List<String> combinedResults = new ArrayList<>(masterResults);
            for (int i = 0; i < chunks.size(); i++) {
                try {
                    combinedResults.addAll(resultQueue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }

            return combinedResults;
        }else {
            threadPool.execute(() ->{
                for (List<String> chunk : chunks) {
                    masterPrinterAdapter.sendFile(chunk.toArray(new String[0]), masterCallbackPrx);
                }
            });
        }
    }

    public void setChunkSize(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than zero.");
        }
        this.chunkSize = chunkSize;
    }
}
