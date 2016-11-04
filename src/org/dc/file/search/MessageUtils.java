package org.dc.file.search;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class MessageUtils {

    private static long username;
    private static Peer localPeer;

    private MessageUtils() {
    }

    public static Peer init(long username) throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        MessageUtils.username = username;
        new Thread(() -> {
            while (true) {
                try {
                    Socket connectionSocket = serverSocket.accept();
                    BufferedReader receivedData =
                            new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String clientSentence = receivedData.readLine();
                    System.out.println("Received: " + clientSentence);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        InetAddress localIp = InetAddress.getLocalHost();
        String hostAddress = localIp.getHostAddress();
        localPeer = new Peer(hostAddress, serverSocket.getLocalPort());
        return localPeer;
    }

    public static void sendMessage(final String destinationIp, final int destinationPort, final String message) {
        new Thread(() -> {
            try {
                int length = message.length() + 5;
                String payload = String.format("%04d", length) + " " + message;
                Socket clientSocket = new Socket(destinationIp, destinationPort);
                DataOutputStream toRemote = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader fromRemote = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                toRemote.write(payload.getBytes());
                System.out.println("REQUEST TO REMOTE: " + payload);
                MessageUtils.processReply(fromRemote.readLine());
                if (clientSocket.isConnected()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void processReply(String response) throws IOException {
        System.out.println("RESPONSE RECEIVED: " + response);
        String[] data = response.split(" ");
        int length = Integer.parseInt(data[0]);
        if (length != response.length()) {
            System.err.println("Invalid length");
            return;
        }
        switch (data[1]) {
            case "REGOK":
                addToNeighboursList(data);
                break;
            case "UNROK":
                System.out.println("******* Terminating Peer ********");
                System.exit(0);
                break;
        }
    }

    private static void addToNeighboursList(String[] data) throws IOException {
        int peerCount = Integer.parseInt(data[2]);
        if (peerCount == 0) {
            System.out.println("There is no any peers in the network yet.");
        } else if (peerCount < 9997) {
            Store store = Store.getInstance();
            int rnd1 = getRandom(peerCount) * 3;
            int rnd2 = getRandom(peerCount) * 3;
            while (rnd1 == rnd2 && peerCount > 1) {
                rnd2 = getRandom(peerCount) * 3;
            }
            Peer peer1 = new Peer(data[rnd1], Integer.parseInt(data[rnd1 + 1]));
            String joinMsg = "JOIN " + localPeer.getIp() + " " + localPeer.getPort() + "\n";
            sendMessage(peer1.getIp(), peer1.getPort(), joinMsg);
            store.addPeer(peer1);
            if (peerCount > 1) {
                Peer peer2 = new Peer(data[rnd2], Integer.parseInt(data[rnd2 + 1]));
                sendMessage(peer2.getIp(), peer2.getPort(), joinMsg);
                store.addPeer(peer2);
            }
            store.displayPeerList();
        } else if (peerCount == 9997) {
            System.err.println("Bootstrap server already filled");
        } else if (peerCount == 9998) {
            System.err.println("Server error");
        } else if (peerCount < 9999) {
            System.err.println("Peer already registered");
            sendMessage(Constants.BOOTSTRAP_SERVER, Constants.BOOTSTRAP_PORT, "UNREG " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
        }
    }

    private static int getRandom(int peerCount) {
        return ThreadLocalRandom.current().nextInt(1, peerCount + 1);
    }

}
