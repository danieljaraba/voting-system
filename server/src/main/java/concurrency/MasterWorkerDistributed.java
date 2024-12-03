package concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    }

    /**
     * Procesa una lista de IDs dividiéndolos en chunks y enviándolos para su
     * procesamiento.
     * 
     * @param ids Lista de identificadores a procesar.
     * @return Lista combinada de resultados.
     * @throws InterruptedException Si la operación es interrumpida o se excede el
     *                              tiempo de espera.
     */
    public List<String> processFile(List<String> ids) throws InterruptedException {
        List<List<String>> chunks = createChunks(ids, chunkSize);
        int numberOfChunks = chunks.size();
        String taskId = UUID.randomUUID().toString();
        BlockingQueue<List<String>> resultQueue = new LinkedBlockingQueue<>();
        responses.put(taskId, resultQueue);

        System.out.println("Enviando " + numberOfChunks + " chunks para procesar.");
        for (List<String> chunk : chunks) {
            try {
                masterPrinterAdapter.sendFile(chunk.toArray(new String[0]), masterCallbackPrx, taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> combinedResults = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(numberOfChunks);

        for (int i = 0; i < numberOfChunks; i++) {
            List<String> result = resultQueue.poll(60, TimeUnit.SECONDS); // Timeout de 60 segundos
            if (result != null) {
                combinedResults.addAll(result);
                latch.countDown();
            } else {
                System.err.println("Timeout esperando respuesta para chunk #" + (i + 1));
                latch.countDown();
            }
        }

        // Esperar a que todas las respuestas sean recogidas
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        if (!completed) {
            System.err.println("Timeout esperando a que todas las respuestas lleguen.");
        }

        responses.remove(taskId);
        return combinedResults;
    }

    /**
     * Recibe los resultados de un chunk procesado.
     * 
     * @param taskId  Identificador de la tarea.
     * @param results Resultados recibidos.
     */
    public void receiveFile(String taskId, String[] results) {
        List<String> result = List.of(results);
        BlockingQueue<List<String>> resultQueue = responses.get(taskId);
        if (resultQueue != null) {
            resultQueue.add(result);
        } else {
            System.err.println("No se encontró la cola de resultados para taskId: " + taskId);
        }
    }

}
