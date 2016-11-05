package org.dc.file.search;

import java.util.List;

public class SearchResults {

    private String key;
    private Peer peerWithResults;
    private int hopCount;
    private List<String> results;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
