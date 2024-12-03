package controllers;

import java.util.List;

import ServerIce.IndividualResponse;
import ServerIce.MasterCallbackPrx;
import ServerIce.MasterResolver;
import ServerIce.MultipleResponse;
import com.zeroc.Ice.Current;

import concurrency.MasterWorkerProcessor;
import concurrency.ThreadPool;
import services.QueryService;

public class MasterResolverI implements MasterResolver {

    private final QueryService queryService;
    private final ThreadPool threadPool;
    private final MasterWorkerProcessor masterWorkerProcessor;

    public MasterResolverI(QueryService queryService, ThreadPool threadPool) {
        this.queryService = queryService;
        this.threadPool = threadPool;
        this.masterWorkerProcessor = new MasterWorkerProcessor(queryService, threadPool, 10000);
    }

    @Override
    public void sendId(String id, MasterCallbackPrx client, Current current) {
        threadPool.execute(() -> {
            Long startAt = System.currentTimeMillis();
            String response = queryService.querySingleDocument(id);
            Long endAt = System.currentTimeMillis();
            IndividualResponse individualResponse = new IndividualResponse(endAt - startAt, response);
            client.sendIndividualResponse(individualResponse);
        });
    }

    @Override
    public void sendFile(String[] list, MasterCallbackPrx client, String taskId, Current current) {
        System.out.println("Processing file with " + list.length + " lines");
        threadPool.execute(() -> {
            Long startAt = System.currentTimeMillis();
            List<String> results = masterWorkerProcessor.processFile(List.of(list));
            Long endAt = System.currentTimeMillis();
            String[] responseArray = results.toArray(new String[0]);
            MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
            client.sendMultipleResponse(multipleResponse, taskId);
        });
    }

    @Override
    public void setThreadNumber(int threadCount, MasterCallbackPrx client, Current current) {
        threadPool.setNumberOfThreads(threadCount);
    }

    @Override
    public void setChunkSize(int chunkSize, MasterCallbackPrx client, Current current) {
        masterWorkerProcessor.setChunkSize(chunkSize);
    }
}
