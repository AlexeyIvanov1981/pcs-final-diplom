package search.searchengines;

import search.searchengines.model.PageEntry;

import java.util.List;

public interface SearchEngine {
    List<PageEntry> search(String word);
}
