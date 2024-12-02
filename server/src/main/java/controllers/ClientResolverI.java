package controllers;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

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

    private final ConcurrentHashMap<String, BlockingQueue<String>> clientBuffers;

    public ClientResolverI(QueryService queryService, ThreadPool threadPool) {
        this.queryService = queryService;
        this.threadPool = threadPool;
        this.clientBuffers = new ConcurrentHashMap<>();
        this.masterWorkerProcessor = new MasterWorkerProcessor(queryService, threadPool, 6250);
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
    public void sendFile(String[] list, ClientCallbackPrx client, boolean isLast, Current current) {
        threadPool.execute(() -> {
            String clientId = client.ice_getIdentity().name; // Identifica al cliente único
            clientBuffers.putIfAbsent(clientId, new LinkedBlockingQueue<>()); // Crea un buffer si no existe
            BlockingQueue<String> buffer = clientBuffers.get(clientId);

            try {
                for (String item : list) {
                    buffer.put(item);
                }

                if (isLast) {
                    processFile(clientId, client);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setThreadNumber(int threadCount, ClientCallbackPrx client, Current current) {
        threadPool.setNumberOfThreads(threadCount);
    }

    private void processFile(String clientId, ClientCallbackPrx client) {
        Long startAt = System.currentTimeMillis();
        String[] list = clientBuffers.get(clientId).toArray(new String[0]);

        List<String> results = masterWorkerProcessor.processFile(List.of(list));

        Long endAt = System.currentTimeMillis();
        String[] responseArray = results.toArray(new String[0]);
        MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
        client.sendMultipleResponse(multipleResponse);
    }

}
