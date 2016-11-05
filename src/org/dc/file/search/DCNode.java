package org.dc.file.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DCNode {

    public static void main(String[] args) throws IOException {
        Store store = Store.getInstance();
        store.displayFilesList();
        long username = Calendar.getInstance().getTimeInMillis() % 1000000000;
        Peer localPeer = MessageUtils.init(username);
        MessageUtils.sendTCPMessage(Constants.BOOTSTRAP_SERVER,
                                    Constants.BOOTSTRAP_PORT,
                                    "REG " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "exit":
                    for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
                        Peer peer = entry.getValue();
                        MessageUtils.sendUDPMessage(peer.getIp(),
                                                    peer.getPort(),
                                                    "LEAVE " + localPeer.getIp() + " " + localPeer.getPort());
                    }
                    MessageUtils.sendTCPMessage(Constants.BOOTSTRAP_SERVER,
                                                Constants.BOOTSTRAP_PORT,
                                                "UNREG " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
                    break;
                case "peers":
                    store.displayPeerList();
                    break;
                case "files":
                    store.displayFilesList();
                    break;
                case "search":
                    System.out.print("Enter key: ");
                    String key = scanner.nextLine();
                    SearchRequest searchRequest = new SearchRequest(Calendar.getInstance().getTimeInMillis(),
                                                                    key, 2, localPeer);
                    store.setMySearchRequest(searchRequest);
                    store.addSearchRequest(searchRequest);
                    store.setSearchResults(new ArrayList<>());
                    List<String> results = Store.getInstance().findInFiles(searchRequest.getSearchKey());
                    SearchResult searchResult = new SearchResult(key, localPeer, 0, results);
                    store.addSearchResult(searchResult);
                    for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
                        Peer peer = entry.getValue();
                        MessageUtils.sendUDPMessage(peer.getIp(),
                                                    peer.getPort(),
                                                    "SER " + localPeer.getIp() + " " + localPeer.getPort()
                                                    + " \"" + key + "\" 2");
                    }
                    break;
                case "requests":
                    Store.getInstance().displaySearchRequestsList();
                    break;
                case "results":
                    Store.getInstance().displaySearchResults();
                    break;
                default:
                    System.err.println("Invalid input");
            }
        }
    }

}
