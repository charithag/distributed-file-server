package org.dc.file.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Store {

    private static final Object LOCK = new Object();
    private static Store store;
    private static List<String> fileNames;
    private static volatile Map<String, Peer> peerMap;
    private static volatile Map<String, SearchRequest> searchRequestMap;
    private volatile SearchRequest mySearchRequest;
    private volatile List<SearchResult> searchResults;
    private int serverPort;
    private String serverIp;

    private Store() {
        peerMap = new HashMap<>();
        fileNames = new ArrayList<>();
        searchRequestMap = new HashMap<>();
        int fileCount = ThreadLocalRandom.current().nextInt(3, 5);
        while (fileCount > 0) {
            int fileIndex = ThreadLocalRandom.current().nextInt(0, Constants.FILES.length - 1);
            String file = Constants.FILES[fileIndex];
            if (!fileNames.contains(file)) {
                fileNames.add(file);
                fileCount--;
            }
        }
        new Thread(() -> {
            while (true) {
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                synchronized (LOCK) {
                    final String[] keys = {null};
                    searchRequestMap.entrySet().stream().filter(entry -> timeStamp > entry.getValue().getTimeStamp() + (10 * 1000)).forEach(entry -> {
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

    public static Store getInstance() {
        if (store == null) {
            store = new Store();
        }
        return store;
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

    public void displayPeerList() {
        String list = "\n=========== Peers List ===========\n";
        for (Map.Entry<String, Peer> entry : peerMap.entrySet()) {
            Peer peer = entry.getValue();
            list += "Peer IP: " + peer.getIp() + " Port: " + peer.getPort() + "\n";
        }
        System.out.println(list + "==================================");
    }

    public List<String> findInFiles(String key) {
        List<String> results = new ArrayList<>();
        String list = "\n=========== Local Search Results ===========\n";
        for (String fileName : fileNames) {
            if (fileName.toUpperCase().contains(key.toUpperCase())) {
                if (fileName.equalsIgnoreCase(key)) {
                    results.add(fileName);
                    list += fileName + "\n";
                } else {
                    String[] fileNameParts = fileName.split(" ");
                    String[] keyParts = key.split(" ");
                    for (String filePart : fileNameParts) {
                        boolean hasPart = true;
                        for (String keyPart : keyParts) {
                            if (!filePart.equalsIgnoreCase(keyPart)) {
                                hasPart = false;
                            }
                        }
                        if (hasPart) {
                            results.add("\"" + fileName + "\"");
                            list += fileName + "\n";
                        }
                    }
                }
            }
        }
        System.out.println(list + "============================================");
        return results;
    }

    public void displayFilesList() {
        String list = "\n=========== Files List ===========\n";
        for (String fileName : fileNames) {
            list += fileName + "\n";
        }
        System.out.println(list + "==================================");
    }

    public List<String> getFilesList() {
        return fileNames;
    }

    public boolean addSearchRequest(SearchRequest searchRequest) {
        synchronized (LOCK) {
            return searchRequestMap.put(searchRequest.getSearchId(), searchRequest) == null;
        }
    }

    public void displaySearchRequestsList() {
        String list = "\n=========== Search Requests List ===========\n";
        for (Map.Entry<String, SearchRequest> entry : searchRequestMap.entrySet()) {
            list += "Peer: " + entry.getValue().getPeer().getKey() + " Key: " + entry.getValue().getSearchKey() + "\n";
        }
        System.out.println(list + "============================================");
    }

    public void displaySearchResults() {
        String list = "\n=========== Search Results ===========\n" +
                      "Search Key:" + mySearchRequest.getSearchKey()
                      + "\nPeer\t\t\t\t|Hops\t|Count\t|Files\n";
        for (SearchResult result : searchResults) {
            Peer peer = result.getPeerWithResults();
            list += peer.getIp() + ":" + peer.getPort() + "\t ";
            list += result.getHopCount() + "\t\t ";
            list += result.getResults().size() + "\t\t ";
            for (String fileName : result.getResults()) {
                list += fileName + " ";
            }
            list += "\n";
        }
        System.out.println(list + "======================================");
    }

}
