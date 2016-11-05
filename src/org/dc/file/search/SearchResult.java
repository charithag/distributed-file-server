package org.dc.file.search;

import java.util.List;

public class SearchResult {

    private String searchKey;
    private Peer peerWithResults;
    private int hopCount;
    private List<String> results;

    public SearchResult(String searchKey, Peer peerWithResults, int hopCount, List<String> results) {
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

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}
