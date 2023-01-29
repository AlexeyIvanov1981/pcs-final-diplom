import search.searchengines.BooleanSearchEngine;
import search.searchengines.SearchEngine;
import search.searchserver.SearchServer;
import settings.ProjectSettings;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        SearchEngine engine = new BooleanSearchEngine(new File(ProjectSettings.PDFS_FOLDER_PATH));

        SearchServer searchServer = new SearchServer(engine);
        searchServer.start();
    }
}