package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
    private final int chunkSize = 1250;
    private final ConcurrentHashMap<String, BlockingQueue<String>> clientBuffers;

    public ClientResolverI(QueryService queryService, ThreadPool threadPool) {
        this.queryService = queryService;
        this.threadPool = threadPool;
        this.clientBuffers = new ConcurrentHashMap<>();
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
                // Agrega los datos al buffer del cliente
                for (String item : list) {
                    buffer.put(item);
                }

                // Si es el último chunk, procesa el buffer completo
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
        threadPool.execute(() -> {
            Long startAt = System.currentTimeMillis();
            String[] list = clientBuffers.get(clientId).toArray(new String[0]);
            if (list.length == 0) {
                return;
            }
            List<String> responseList = queryService.queryMultipleDocuments(List.of(list));
            String[] responseArray = responseList.toArray(new String[0]);
            Long endAt = System.currentTimeMillis();
            MultipleResponse multipleResponse = new MultipleResponse(endAt - startAt, responseArray);
            client.sendMultipleResponse(multipleResponse);
        });
    }

}
