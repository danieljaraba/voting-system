package controllers;

import java.util.List;

import com.zeroc.Ice.Current;

import ClientIce.ClientCallbackPrx;
import ClientIce.ClientResolver;
import ClientIce.IndividualResponse;
import ClientIce.MultipleResponse;
import concurrency.ThreadPool;
import services.QueryService;

public class ClientResolverI implements ClientResolver {

    private QueryService queryService;
    private ThreadPool threadPool;

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
            Long startAt = System.currentTimeMillis();
            List<String> response = queryService.queryMultipleDocuments(List.of(list));
            Long endAt = System.currentTimeMillis();
            String[] responseArray = response.toArray(new String[response.size()]);
            MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
            client.sendMultipleResponse(multipleResponse);
        });
    }

    @Override
    public void setThreadNumber(int threadCount, ClientCallbackPrx client, Current current) {
        threadPool.setNumberOfThreads(threadCount);
    }

}
