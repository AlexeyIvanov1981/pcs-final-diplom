package search.searchengines;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import search.searchengines.model.IndexingResults;
import search.searchengines.model.PageEntry;
import search.searchengines.model.WordFrequencies;
import utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    private final IndexingResults indexingResults;

    public BooleanSearchEngine(File pdfsDir) {
        indexingResults = indexPdfs(pdfsDir);
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> pageEntriesForWord = indexingResults.get(word);

        Collections.sort(pageEntriesForWord);

        return pageEntriesForWord;
    }


    private IndexingResults indexPdfs(File pdfsDir) {
        List<Path> pdfFiles;
        try {
            pdfFiles = FileUtils.getFilesFromFolderByExtension(pdfsDir.toPath(), FileUtils.PDF_FILE_EXTENSION);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read pdfs from " + pdfsDir + " folder.");
        }

        IndexingResults indexingResults = new IndexingResults();

        for (Path pdfFile: pdfFiles) {
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfFile.toFile())) ) {
                final int pdfPageCount = pdfDocument.getNumberOfPages();
                for (int i = 1; i <= pdfPageCount; ++i) {
                    final PdfPage pdfPage = pdfDocument.getPage(i);
                    final String pdfPageText = PdfTextExtractor.getTextFromPage(pdfPage);
                    final String[] words = pdfPageText.split("\\P{IsAlphabetic}+");

                    WordFrequencies wordsFrequencies = new WordFrequencies();
                    for (String word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        wordsFrequencies.put(word, wordsFrequencies.getOrDefault(word, 0) + 1);
                    }

                    for (Map.Entry<String, Integer> wordFrequency : wordsFrequencies.entrySet()) {
                        final PageEntry pageEntry = new PageEntry(pdfFile.getFileName().toString(), i, wordFrequency.getValue());

                        if (indexingResults.containsKey(wordFrequency.getKey())) {
                            final List<PageEntry> indexingResultForWord = indexingResults.get(wordFrequency.getKey());

                            List<PageEntry> updatedIndexingResultForWord = new ArrayList<>(indexingResultForWord);
                            updatedIndexingResultForWord.add(pageEntry);
                            indexingResults.put(wordFrequency.getKey(), updatedIndexingResultForWord);
                        } else {
                            indexingResults.put(wordFrequency.getKey(), List.of(pageEntry));
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return indexingResults;
    }
}
