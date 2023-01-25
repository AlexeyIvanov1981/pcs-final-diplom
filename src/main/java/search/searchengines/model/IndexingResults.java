package search.searchengines.model;

import java.util.HashMap;
import java.util.List;

/**
 * Class to store indexing results. Key is the word we index, value is {@link PageEntry} list we found for this word.
 */
public class IndexingResults extends HashMap<String, List<PageEntry>> {
}
