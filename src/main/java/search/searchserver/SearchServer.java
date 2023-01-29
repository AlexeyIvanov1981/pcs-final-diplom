package search.searchserver;

import search.searchengines.SearchEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SearchServer {
    private static final int PORT = 8989;

    private ExecutorService clientExecutor;

    private final SearchEngine searchEngine;

    public SearchServer(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            clientExecutor = Executors.newCachedThreadPool();

            System.out.println("Server has started.");
            for (; ; ) {
                Socket clientSocket = serverSocket.accept();
                handleClientConnection(clientSocket, searchEngine);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != clientExecutor) {
                clientExecutor.shutdown();
            }
        }
    }

    private void handleClientConnection(Socket clientSocket, SearchEngine searchEngine) {
        System.out.println("Incoming connection.");
        clientExecutor.execute(new ClientHandler(clientSocket, searchEngine));
    }
}
