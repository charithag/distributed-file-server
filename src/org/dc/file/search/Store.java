package org.dc.file.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Store {

    private static Store store;
    private volatile Map<String, Peer> peerMap;

    private List<String> fileNames;

    private Store() {
        peerMap = new HashMap<>();
        fileNames = new ArrayList<>();
        int fileCount = ThreadLocalRandom.current().nextInt(3, 5);
        while (fileCount > 0) {
            int fileIndex = ThreadLocalRandom.current().nextInt(0, Constants.FILES.length - 1);
            String file = Constants.FILES[fileIndex];
            if (!fileNames.contains(file)) {
                fileNames.add(file);
                fileCount--;
            }
        }
    }

    public static Store getInstance() {
        if (store == null) {
            store = new Store();
        }
        return store;
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
        String list = "\n=========== Search Results ===========\n";
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
                            results.add(fileName);
                            list += fileName + "\n";
                        }
                    }
                }
            }
        }
        System.out.println(list + "======================================");
        return results;
    }

    public void displayFilesList() {
        String list = "\n=========== Files List ===========\n";
        for (String fileName : fileNames) {
            list += fileName + "\n";
        }
        System.out.println(list + "==================================");
    }
}
