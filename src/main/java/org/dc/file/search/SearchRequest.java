package org.dc.file.search;

public class SearchRequest {

    private long timeStamp;
    private String searchKey;
    private int hopCount;
    private Peer peer;

    public SearchRequest(long timeStamp, String searchKey, int hopCount, Peer peer) {
        this.timeStamp = timeStamp;
        this.searchKey = searchKey;
        this.hopCount = hopCount;
        this.peer = peer;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public String getSearchId() {
        return peer.getKey() + ":" + searchKey;
    }
}
