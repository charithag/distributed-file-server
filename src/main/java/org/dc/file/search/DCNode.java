package org.dc.file.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DCNode {

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService scheduler
                = Executors.newSingleThreadScheduledExecutor();

        Store store = Store.getInstance();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input the Bootstrap Server IP: ");
        store.setServerIp(scanner.nextLine());
        System.out.print("Input the Bootstrap Server Port: ");
        store.setServerPort(scanner.nextInt());
        scanner.nextLine();
        store.displayFilesList();
        String uuid = UUID.randomUUID().toString();
        Peer localPeer = MessageUtils.init(uuid);
        MessageUtils.sendTCPMessage(store.getServerIp(),
                                    store.getServerPort(),
                                    "REG " + localPeer.getIp() + " " + localPeer.getPort() + " " + uuid);
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
                    MessageUtils.sendTCPMessage(store.getServerIp(),
                                                store.getServerPort(),
                                                "UNREG " + localPeer.getIp() + " " + localPeer.getPort() + " " + uuid);
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
                    if (!results.isEmpty()) {
                        SearchResult searchResult = new SearchResult(key, localPeer, 0, results);
                        store.addSearchResult(searchResult);
                    }
                    for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
                        Peer peer = entry.getValue();
                        MessageUtils.sendUDPMessage(peer.getIp(),
                                                    peer.getPort(),
                                                    "SER " + localPeer.getIp() + " " + localPeer.getPort()
                                                    + " \"" + key + "\" 2");
                    }
                    Runnable resultTask = () -> Store.getInstance().displaySearchResults();
                    int delay = 5;
                    scheduler.schedule(resultTask, delay, TimeUnit.SECONDS);
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
