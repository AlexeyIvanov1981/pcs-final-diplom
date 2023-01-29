package search.searchserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import search.searchengines.SearchEngine;
import search.searchengines.model.PageEntry;
import search.searchserver.model.dto.PageEntryDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final Socket clientSocket;
    private final SearchEngine searchEngine;

    public ClientHandler(Socket clientSocket, SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {
            final String searchTerm = getClientInput(input);

            final List<PageEntry> searchResults = searchEngine.search(searchTerm);

            output.println(prepareSearchResponse(searchResults));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != clientSocket) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Incoming connection has been served.");
    }

    private String getClientInput(BufferedReader client) throws IOException {
        final String clientInput = client.readLine();

        if (clientInput != null) {
            String[] parsedClientInput = clientInput.trim().split("[ \\t\\n,?;.:!]");
            if (parsedClientInput.length > 0) {
                return parsedClientInput[0];
            } else {
                throw new IOException("Unable to parse client input.");
            }
        } else {
            throw new IOException("Client input is empty.");
        }
    }

    private String prepareSearchResponse(final List<PageEntry> searchResults) throws JsonProcessingException {
        final List<PageEntryDto> pageEntriesDto = jsonMapper.convertValue(searchResults, new TypeReference<>() {
        });

        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageEntriesDto);
    }
}
