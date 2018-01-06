package org.dc.file.search;

import org.dc.file.search.dto.DFile;
import org.dc.file.search.dto.Peer;

import java.util.List;

public class SearchResult {

    private String searchKey;
    private Peer peerWithResults;
    private int hopCount;
    private List<DFile> results;

    public SearchResult(String searchKey, Peer peerWithResults, int hopCount, List<DFile> results) {
        this.searchKey = searchKey;
        this.peerWithResults = peerWithResults;
        this.hopCount = hopCount;
        this.results = results;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Peer getPeerWithResults() {
        return peerWithResults;
    }

    public void setPeerWithResults(Peer peerWithResults) {
        this.peerWithResults = peerWithResults;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public List<DFile> getResults() {
        return results;
    }

    public void setResults(List<DFile> results) {
        this.results = results;
    }
}
