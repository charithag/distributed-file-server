package org.dc.file.search;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.List;
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
                    String message = receivedData.readLine();
                    System.out.println("Received message: " + message);
                    processMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress localIp = InetAddress.getLocalHost();
        String hostAddress = localIp.getHostAddress();
        while (interfaces.hasMoreElements()) {
            NetworkInterface interf = interfaces.nextElement();
            if (interf.isUp() && !interf.isLoopback()) {
                List<InterfaceAddress> adrs = interf.getInterfaceAddresses();
                for (InterfaceAddress adr : adrs) {
                    InetAddress inadr = adr.getAddress();
                    if (inadr instanceof Inet4Address) {
                        hostAddress = inadr.getHostAddress();
                        System.out.println("Updated host address: " + hostAddress);
                    }
                }
            }
        }
        localPeer = new Peer(hostAddress, serverSocket.getLocalPort());
        return localPeer;
    }

    public static void sendMessage(final String destinationIp, final int destinationPort, final String message) {
        new Thread(() -> {
            try {
                int length = message.length() + 5;
                String payload = String.format("%04d", length) + " " + message;
                if (!Constants.BOOTSTRAP_SERVER.equals(destinationIp) && Constants.BOOTSTRAP_PORT != destinationPort) {
                    payload += "\n";
                }
                Socket clientSocket = new Socket(destinationIp, destinationPort);
                DataOutputStream toRemote = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader fromRemote = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                toRemote.write(payload.getBytes());
                System.out.println("MESSAGE SENT: " + payload);
                String response = fromRemote.readLine();
                if (response != null) {
                    System.out.println("RESPONSE RECEIVED: " + response);
                    MessageUtils.processMessage(response);
                }
                if (clientSocket.isConnected()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                Peer peer = new Peer(destinationIp, destinationPort);
                System.err.println("Unable to connect with remote " + peer.getKey() + ". " + e.getMessage());
                Store store = Store.getInstance();
                store.removePeer(peer);
            }
        }).start();
    }

    private static void processMessage(String response) throws IOException {
        String[] data = response.split(" ");
        int length = Integer.parseInt(data[0]);
        if (length != response.length()) {
            System.err.println("Invalid length");
            return;
        }
        Store store = Store.getInstance();
        Peer peer;
        switch (data[1]) {
            case "REGOK":
                addToNeighboursList(data);
                break;
            case "UNROK":
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("******* Terminating Peer ********");
                System.exit(0);
                break;
            case "JOIN":
                peer = new Peer(data[2], Integer.parseInt(data[3]));
                store.addPeer(peer);
                sendMessage(peer.getIp(), peer.getPort(), "JOINOK 0");
                break;
            case "JOINOK":
                if (data[2].equals("0")) {
                    System.out.println("Successfully joined with a peer");
                } else {
                    System.err.println("Join failed");
                }
                break;
            case "LEAVE":
                peer = new Peer(data[2], Integer.parseInt(data[3]));
                store = Store.getInstance();
                store.removePeer(peer);
                sendMessage(peer.getIp(), peer.getPort(), "LEAVEOK 0");
                break;
            case "LEAVEOK":
                if (data[2].equals("0")) {
                    System.out.println("Successfully disconnected form peer");
                } else {
                    System.err.println("Leave failed");
                }
                break;
            case "SER":
                List<String> result = store.findInFiles(data[2]);
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
            String joinMsg = "JOIN " + localPeer.getIp() + " " + localPeer.getPort();
            sendMessage(peer1.getIp(), peer1.getPort(), joinMsg);
            store.addPeer(peer1);
            if (peerCount > 1) {
                Peer peer2 = new Peer(data[rnd2], Integer.parseInt(data[rnd2 + 1]));
                sendMessage(peer2.getIp(), peer2.getPort(), joinMsg);
                store.addPeer(peer2);
            }
        } else if (peerCount == 9997) {
            System.err.println("Bootstrap server already filled");
        } else if (peerCount == 9998) {
            System.err.println("Server error");
        } else if (peerCount < 9999) {
            System.err.println("Peer already registered");
            sendMessage(Constants.BOOTSTRAP_SERVER, Constants.BOOTSTRAP_PORT, "UNREG "
                            + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
        }
    }

    private static int getRandom(int peerCount) {
        return ThreadLocalRandom.current().nextInt(1, peerCount + 1);
    }

}
