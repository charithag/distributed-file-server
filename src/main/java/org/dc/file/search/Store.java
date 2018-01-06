package org.dc.file.search;

import org.dc.file.search.dto.DFile;
import org.dc.file.search.dto.Peer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Store {

    private static final Object LOCK = new Object();
    private static volatile Map<String, DFile> localFiles = new HashMap<>();
    private static volatile Map<String, Peer> peerMap = new HashMap<>();
    private static volatile Map<String, SearchRequest> searchRequestMap = new HashMap<>();
    private static final Store INSTANCE = new Store();
    private volatile SearchRequest mySearchRequest;
    private volatile List<SearchResult> searchResults;
    private int serverPort;
    private String serverIp;
    private Peer localPeer;
    private long logicalClock;

    private Store() {
        logicalClock = 0L;
        int fileCount = ThreadLocalRandom.current().nextInt(3, 5);
        String[] files = Constants.getFiles();
        while (fileCount > 0) {
            int fileIndex = ThreadLocalRandom.current().nextInt(0, files.length - 1);
            String file = files[fileIndex];
            if (!localFiles.containsKey(file)) {
                DFile dFile = new DFile();
                dFile.setFileName(file);
                localFiles.put(file, dFile);
                fileCount--;
            }
        }
        new Thread(() -> {
            while (true) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                synchronized (LOCK) {
                    final String[] keys = {null};
                    searchRequestMap.entrySet().stream()
                            .filter(entry -> currentTime > entry.getValue().getTimeStamp() + (10 * 1000)).forEach(entry -> {
                        keys[0] = entry.getKey();
                    });
                    for (String key : keys) {
                        searchRequestMap.remove(key);
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Map<String, DFile> getLocalFiles() {
        return localFiles;
    }

    public static void setLocalFiles(Map<String, DFile> localFiles) {
        Store.localFiles = localFiles;
    }

    public long getLogicalClock() {
        return logicalClock;
    }

    public void adjustLogicalClock(long externalClock){
        this.logicalClock = ((logicalClock < externalClock) ? externalClock : logicalClock) + 1;
    }

    public static Store getInstance() {
        return INSTANCE;
    }

    public String getServerIp(){
        return serverIp;
    }

    public void setServerIp(String ip){
        this.serverIp = ip;
    }

    public int getServerPort(){
        return serverPort;
    }

    public void setServerPort(int port){
        this.serverPort = port;
    }

    public SearchRequest getMySearchRequest() {
        return mySearchRequest;
    }

    public void setMySearchRequest(SearchRequest mySearchRequest) {
        this.mySearchRequest = mySearchRequest;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public void addSearchResult(SearchResult searchResult) {
        searchResults.add(searchResult);
    }

    public Map<String, Peer> getPeerMap() {
        return peerMap;
    }

    public synchronized void addPeer(Peer peer) {
        peerMap.put(peer.getKey(), peer);
    }

    public synchronized void removePeer(Peer peer) {
        peerMap.remove(peer.getKey());
    }

    public Set<Map.Entry<String, Peer>> getPeerList() {
        return peerMap.entrySet();
    }

    public Peer getLocalPeer() {
        return localPeer;
    }

    public void setLocalPeer(Peer localPeer) {
        this.localPeer = localPeer;
    }

    public List<DFile> findInFiles(String key) {
        List<DFile> results = new ArrayList<>();
        StringBuilder list = new StringBuilder("\n=========== Local Search Results ===========\n");
        List<String> fileNames = new ArrayList<>(localFiles.keySet());
        for (String fileName : fileNames) {
            if (fileName.toUpperCase().contains(key.toUpperCase())) {
                results.add(localFiles.get(fileName));
                list.append(fileName).append("\n");
            }
        }
        System.out.println(list + "============================================");
        return results;
    }

    public List<String> getFilesList() {
        return new ArrayList<>(localFiles.keySet());
    }

    public boolean addSearchRequest(SearchRequest searchRequest) {
        synchronized (LOCK) {
            return searchRequestMap.put(searchRequest.getSearchId(), searchRequest) == null;
        }
    }

}
