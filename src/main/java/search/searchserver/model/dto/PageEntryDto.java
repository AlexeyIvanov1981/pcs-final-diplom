package search.searchserver.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageEntryDto {
    @JsonProperty("pdfName")
    private String pdfName;
    @JsonProperty("page")
    private int page;
    @JsonProperty("count")
    private int count;

    public PageEntryDto() {
    }

    public PageEntryDto(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
