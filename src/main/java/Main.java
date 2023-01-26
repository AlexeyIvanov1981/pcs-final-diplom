import search.searchengines.BooleanSearchEngine;
import settings.ProjectSettings;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File(ProjectSettings.PDFS_FOLDER_PATH));
        System.out.println(engine.search("бизнес"));
    }
}