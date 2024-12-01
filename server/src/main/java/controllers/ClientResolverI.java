package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.zeroc.Ice.Current;

import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolver;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import concurrency.ThreadPool;
import services.QueryService;

import static utils.ServerUtils.createChunks;

public class ClientResolverI implements ClientResolver {

    private final QueryService queryService;
    private final ThreadPool threadPool;
    private final int chunkSize = 1000;

    public ClientResolverI(QueryService queryService, ThreadPool threadPool) {
        this.queryService = queryService;
        this.threadPool = threadPool;
    }

    @Override
    public void sendId(String id, ClientCallbackPrx client, Current current) {
        threadPool.execute(() -> {
            Long startAt = System.currentTimeMillis();
            String response = queryService.querySingleDocument(id);
            Long endAt = System.currentTimeMillis();
            IndividualResponse individualResponse = new IndividualResponse(endAt - startAt, response);
            client.sendIndividualResponse(individualResponse);
        });
    }

    @Override
    public void sendFile(String[] list, ClientCallbackPrx client, Current current) {
        threadPool.execute(() -> {
            String[] responseArray = new String[0];
            Long startAt = System.currentTimeMillis();
            Long endAt;
            if (list.length > chunkSize) {
                List<List<String>> chunks = createChunks(List.of(list), chunkSize);
                BlockingQueue<List<String>> resultQueue = new LinkedBlockingQueue<>();

                List<String> masterChunk = null;
                if (!chunks.isEmpty()) {
                    masterChunk = chunks.remove(0); // Reserve the first chunk for the master
                }

                for (List<String> chunk : chunks) {
                    threadPool.execute(() -> {
                        try {
                            List<String> result = queryService.queryMultipleDocuments(chunk);
                            resultQueue.put(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                resultQueue.put(new ArrayList<>());
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

                endAt = System.currentTimeMillis();
                responseArray = combinedResults.toArray(new String[0]);

            } else {
                List<String> response = queryService.queryMultipleDocuments(List.of(list));
                endAt = System.currentTimeMillis();
                responseArray = response.toArray(new String[response.size()]);
            }
            MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
            client.sendMultipleResponse(multipleResponse);
        });
    }

    @Override
    public void setThreadNumber(int threadCount, ClientCallbackPrx client, Current current) {
        threadPool.setNumberOfThreads(threadCount);
    }

}
