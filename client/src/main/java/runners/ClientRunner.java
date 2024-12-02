package runners;

import ClientIce.ClientCallbackPrx;
import config.ClientConfig;
import adapters.ClientPrinterAdapter;
import ui.ClientInterface;
import utils.ClientUtils;
import utils.InputParser;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the main execution flow of the client.
 */
public class ClientRunner {

    public void run(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = ClientConfig.initializeCommunicator(args, "config.client", extraArgs)) {
            ObjectAdapter adapter = ClientConfig.createObjectAdapter(communicator, "ClientCallback");
            ClientPrinterAdapter serviceManager = new ClientPrinterAdapter(communicator);

            String username = ClientInterface.promptUsername();
            ClientCallbackPrx callbackPrx = serviceManager.initializeCallback(adapter);

            processInputs(serviceManager, callbackPrx, username);

            ClientInterface.closeScanner();
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processInputs(ClientPrinterAdapter serviceManager, ClientCallbackPrx callbackPrx, String username) {
        String input;
        do {
            input = ClientInterface.promptInput();
            if (!"exit".equalsIgnoreCase(input)) {
                processInput(input, serviceManager, callbackPrx, username);
            }
        } while (!"exit".equalsIgnoreCase(input));
    }

    private void processInput(String input, ClientPrinterAdapter serviceManager, ClientCallbackPrx callbackPrx,
            String username) {
        try {
            if (InputParser.isId(input)) {
                serviceManager.sendId(input, callbackPrx);
            } else if (InputParser.isThreadNumber(input)) {
                int threadCount = Integer.parseInt(input.substring(1));
                serviceManager.setThreadNumber(threadCount, callbackPrx);
            } else if (InputParser.isFilePath(input)) {
                List<String> ids = ClientUtils.readIdsFromFile(input);
                serviceManager.sendFile(ids.toArray(new String[0]), callbackPrx);
            } else if (InputParser.isChunkSize(input)) {
                int chunkSize = Integer.parseInt(input.substring(1));
                serviceManager.setChunkSize(chunkSize, callbackPrx);
            } else {
                System.out.println("Invalid input. Please enter a valid ID, file path, or thread number (e.g., N5).");
            }
        } catch (Exception e) {
            System.err.println("Error processing input: " + e.getMessage());
        }
    }
}
