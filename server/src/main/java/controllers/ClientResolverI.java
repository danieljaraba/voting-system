package controllers;

import java.util.List;

import com.zeroc.Ice.Current;

import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolver;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import concurrency.MasterWorkerProcessor;
import concurrency.ThreadPool;
import services.QueryService;

public class ClientResolverI implements ClientResolver {

    private final QueryService queryService;
    private final ThreadPool threadPool;
    private final MasterWorkerProcessor masterWorkerProcessor;

    public ClientResolverI(QueryService queryService, ThreadPool threadPool) {
        this.queryService = queryService;
        this.threadPool = threadPool;
        this.masterWorkerProcessor = new MasterWorkerProcessor(queryService, threadPool, 10000);
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
            Long startAt = System.currentTimeMillis();
            List<String> results = masterWorkerProcessor.processFile(List.of(list));
            Long endAt = System.currentTimeMillis();
            String[] responseArray = results.toArray(new String[0]);
            MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
            client.sendMultipleResponse(multipleResponse);
        });
    }

    @Override
    public void setThreadNumber(int threadCount, ClientCallbackPrx client, Current current) {
        threadPool.setNumberOfThreads(threadCount);
    }

    @Override
    public void setChunkSize(int chunkSize, ClientCallbackPrx client, Current current) {
        masterWorkerProcessor.setChunkSize(chunkSize);
    }
}
