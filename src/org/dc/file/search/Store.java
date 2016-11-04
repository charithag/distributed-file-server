package org.dc.file.search;

import java.util.HashMap;
import java.util.Map;

public class Store {

    private static Store store;
    private Map<String, Peer> peerMap;

    private Store() {
        peerMap = new HashMap<>();
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

    public void addPeer(Peer peer) {
        peerMap.put(peer.getKey(), peer);
    }

    public void removePeer(Peer peer) {
        peerMap.remove(peer.getKey());
    }

    public void displayPeerList() {
        String list = "=========== Peers List ===========\n";
        for (Map.Entry<String, Peer> entry : peerMap.entrySet()) {
            Peer peer = entry.getValue();
            list += "Peer IP: " + peer.getIp() + " Port: " + peer.getPort() + "\n";
        }
        System.out.println(list + "==================================");
    }
}
