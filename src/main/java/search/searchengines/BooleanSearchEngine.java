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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BooleanSearchEngine implements SearchEngine {
    private final IndexingResults wordIndex = new IndexingResults();

    public BooleanSearchEngine(File pdfsDir) {
        indexPdfs(pdfsDir);
        sortIndex();
    }

    @Override
    public List<PageEntry> search(String word) {
        return this.wordIndex.getOrDefault(word, new ArrayList<>());
    }

    private void indexPdfs(File pdfsDir) {
        List<Path> pdfFiles;
        try {
            pdfFiles = FileUtils.getFilesFromFolderByExtension(pdfsDir.toPath(), FileUtils.PDF_FILE_EXTENSION);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read pdf files from " + pdfsDir + " folder.", e);
        }

        for (Path pdfFile : pdfFiles) {
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfFile.toFile()))) {
                final int pdfPageCount = pdfDocument.getNumberOfPages();
                for (int i = 1; i <= pdfPageCount; ++i) {
                    final PdfPage pdfPage = pdfDocument.getPage(i);

                    final WordFrequencies wordFrequencies = calculateWordFrequenciesForPage(pdfPage);
                    addWordFrequenciesToIndex(pdfFile.getFileName().toString(), i, wordFrequencies);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read pdf document " + pdfFile.getFileName().toString() + ".", e);
            }
        }
    }

    private WordFrequencies calculateWordFrequenciesForPage(PdfPage page) {
        final String pdfPageText = PdfTextExtractor.getTextFromPage(page);
        final String[] words = pdfPageText.split("\\P{IsAlphabetic}+");

        WordFrequencies wordFrequencies = new WordFrequencies();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            word = word.toLowerCase();
            wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
        }

        return wordFrequencies;
    }

    private void addWordFrequenciesToIndex(String filename, int page, WordFrequencies wordFrequencies) {
        for (Map.Entry<String, Integer> wordFrequency : wordFrequencies.entrySet()) {
            final PageEntry pageEntry = new PageEntry(filename, page, wordFrequency.getValue());
            this.wordIndex.computeIfAbsent(wordFrequency.getKey(), (a) -> new ArrayList<>()).add(pageEntry);
        }
    }

    private void sortIndex() {
        for (Map.Entry<String, List<PageEntry>> wordFrequency: this.wordIndex.entrySet()) {
            Collections.sort(wordFrequency.getValue());
        }
    }
}
