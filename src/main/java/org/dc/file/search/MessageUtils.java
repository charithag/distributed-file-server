package org.dc.file.search;

import org.dc.file.search.Constants.MessageType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MessageUtils {

    private static String username;
    private static Peer localPeer;

    private MessageUtils() {
    }

    public static Peer init(String username) throws IOException {
        MessageUtils.username = username;
        ServerSocket tempSocket = new ServerSocket(0);
        final int port = tempSocket.getLocalPort();
        tempSocket.close();
        new Thread(() -> {
            DatagramSocket serverSocket;
            try {
                serverSocket = new DatagramSocket(port);
                byte[] receiveData;
                while (true) {
                    try {
                        receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData());
                        System.out.println(
                                "UDP MESSAGE RECEIVED FROM: " + receivePacket.getAddress().getHostAddress() + ":"
                                        + receivePacket.getPort() + " MESSAGE: " + message);
                        processMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }).start();
        localPeer = new Peer(username, getLocalHostIP(), port);
        return localPeer;
    }

    public static String getLocalHostIP() throws ConnectException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
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
                        }
                    }
                }
            }
            return hostAddress;
        } catch (SocketException e) {
            throw new ConnectException("Cannot read local interfaces.");
        } catch (UnknownHostException e){
            throw new ConnectException("Cannot not find local interface address.");
        }
    }

    public static void sendTCPMessage(final String destinationIp, final int destinationPort, final String message) {
        sendTCPMessage(destinationIp, destinationPort, message, null);
    }

    public static void sendTCPMessage(final String destinationIp, final int destinationPort, final String message,
                                      ResponseHandler responseHandler) {
        new Thread(() -> {
            try (Socket clientSocket = new Socket(destinationIp, destinationPort)) {
                int length = message.length() + 5;
                String payload = String.format("%04d", length) + " " + message;
                DataOutputStream toRemote = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader fromRemote = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                toRemote.write(payload.getBytes());
                System.out.println("TCP MESSAGE SENT TO: " + destinationIp + ":" + destinationPort
                                           + " MESSAGE: " + payload);
                String response = fromRemote.readLine();
                if (response != null) {
                    System.out.println("TCP RESPONSE RECEIVED FROM: " + destinationIp + ":" + destinationPort
                                               + " MESSAGE: " + response);
                    if (responseHandler != null) responseHandler.onSuccess();
                    MessageUtils.processMessage(response);
                }
            } catch (IOException e) {
                System.out.println(
                        "Unable to connect with bootstrap server at " + destinationIp + ":" + destinationPort + ". " +
                                e.getMessage());
                if (responseHandler != null) {
                    responseHandler.onError(
                            new ConnectException("Unable to connect with bootstrap server at " + destinationIp
                                                         + ":" + destinationPort + ". " + e.getMessage()));
                }
            }
        }).start();
    }

    public static void sendUDPMessage(final String destinationIp, final int destinationPort, final String message) {
        new Thread(() -> {
            try {
                int length = message.length() + 5;
                String payload = String.format("%04d", length) + " " + message;
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(destinationIp);
                System.out.println("UDP MESSAGE SENT TO: " + destinationIp + ":" + destinationPort
                                           + " MESSAGE: " + payload);
                byte[] sendData = payload.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, destinationPort);
                clientSocket.send(sendPacket);
                clientSocket.close();
            } catch (IOException e) {
                Peer peer = new Peer(username, destinationIp, destinationPort);
                System.err.println("Unable to connect with remote " + peer.getKey() + ". " + e.getMessage());
                Store store = Store.getInstance();
                store.removePeer(peer);
            }
        }).start();
    }

    private static void processMessage(String response) throws IOException {
        int length = Integer.parseInt(response.substring(0, 4));
        if (length > response.length()) {
            System.err.println("Invalid length");
            return;
        }
        response = response.substring(0, length);
        String[] data = response.split(" ");
        Store store = Store.getInstance();
        Peer peer;
        switch (data[1]) {
            case MessageType.REGOK:
                addToNeighboursList(data);
                break;
            case MessageType.UNROK:
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("******* Terminating Peer ********");
                System.exit(0);
                break;
            case MessageType.JOIN:
                peer = new Peer(username, data[2], Integer.parseInt(data[3]));
                store.addPeer(peer);
                sendUDPMessage(peer.getIp(), peer.getPort(), MessageType.JOINOK + " 0");
                break;
            case MessageType.JOINOK:
                if (data[2].equals("0")) {
                    System.out.println("Successfully joined with a peer");
                } else {
                    System.err.println("Join failed");
                }
                break;
            case MessageType.LEAVE:
                peer = new Peer(username, data[2], Integer.parseInt(data[3]));
                store = Store.getInstance();
                store.removePeer(peer);
                sendUDPMessage(peer.getIp(), peer.getPort(), MessageType.LEAVEOK + " 0");
                break;
            case MessageType.LEAVEOK:
                if (data[2].equals("0")) {
                    System.out.println("Successfully disconnected form peer");
                } else {
                    System.err.println("Leave failed");
                }
                break;
            case MessageType.SER:
                searchFile(response, data);
                break;
            case MessageType.SEROK:
                int resultCode = Integer.parseInt(data[2]);
                if (resultCode < 9998) {
                    addSearchResult(response, data);
                } else {
                    System.err.println("onError occurred while searching for files. Code: " + resultCode);
                }
                break;
            default:
                System.err.println("Invalid operation");
        }
    }

    private static void searchFile(String response, String[] data) {
        Store store = Store.getInstance();
        Peer peer;
        int hopCount = 2;
        try {
            hopCount = Integer.parseInt(data[data.length - 1]);
        } catch (NumberFormatException ignored) {
        }
        String query = response.substring(response.indexOf('"') + 1, response.lastIndexOf('"'));
        peer = new Peer(username, data[2], Integer.parseInt(data[3]));
        SearchRequest searchRequest = new SearchRequest(Calendar.getInstance().getTimeInMillis(), query, --hopCount,
                                                        peer);
        if (store.addSearchRequest(searchRequest)) {
            if (hopCount > 0) {
                for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
                    Peer remotePeer = entry.getValue();
                    if (!remotePeer.getKey().equals(peer.getKey())) {
                        MessageUtils.sendUDPMessage(remotePeer.getIp(),
                                                    remotePeer.getPort(),
                                                    MessageType.SER + " " + peer.getIp() + " " + peer.getPort() + " \"" +
                                                            searchRequest.getSearchKey()
                                                            + "\" " + hopCount);
                    }
                }
            }
            List<String> results = store.findInFiles(searchRequest.getSearchKey());
            if (!results.isEmpty()) {
                StringBuilder resultMsg =
                        new StringBuilder(results.size() + " " + localPeer.getIp() + " " + localPeer.getPort() + " " + hopCount + " ");
                for (String result : results) {
                    resultMsg.append(" ").append(result);
                }
                sendUDPMessage(peer.getIp(), peer.getPort(), MessageType.SEROK + " " + resultMsg.toString());
            }
        } else {
            System.out.println("Ignoring duplicate request.");
        }
    }

    private static void addSearchResult(String response, String[] data) {
        Store store = Store.getInstance();
        String key = store.getMySearchRequest().getSearchKey();
        int resultsCount = Integer.parseInt(data[2]);
        List<String> results = new ArrayList<>();
        if (resultsCount > 0) {
            String fileNamesString = response.substring(response.indexOf('"'), response.lastIndexOf('"') + 1);
            results = Arrays.asList(fileNamesString.split("\" \""));
            if (resultsCount != results.size()) {
                System.err.println("Invalid results response");
                return;
            }
            for (String result : results) {
                if (!result.toUpperCase().contains(key.toUpperCase())) {
                    System.err.println("Ignoring obsolete search result");
                    return;
                }
            }
        }
        Peer peer = new Peer(username, data[3], Integer.parseInt(data[4]));
        int hopCount = store.getMySearchRequest().getHopCount() - Integer.parseInt(data[5]);
        SearchResult searchResult = new SearchResult(key, peer, hopCount, results);
        store.addSearchResult(searchResult);
    }

    private static void addToNeighboursList(String[] data) throws IOException {
        Store store = Store.getInstance();
        int peerCount = Integer.parseInt(data[2]);
        if (peerCount == 0) {
            System.out.println("There is no any peers in the network yet.");
        } else if (peerCount < 9997) {
            int rnd1 = getRandom(peerCount) * 3;
            int rnd2 = getRandom(peerCount) * 3;
            while (rnd1 == rnd2 && peerCount > 1) {
                rnd2 = getRandom(peerCount) * 3;
            }
            Peer peer1 = new Peer(username, data[rnd1], Integer.parseInt(data[rnd1 + 1]));
            String joinMsg = MessageType.JOIN + " " + localPeer.getIp() + " " + localPeer.getPort();
            sendUDPMessage(peer1.getIp(), peer1.getPort(), joinMsg);
            store.addPeer(peer1);
            if (peerCount > 1) {
                Peer peer2 = new Peer(username, data[rnd2], Integer.parseInt(data[rnd2 + 1]));
                sendUDPMessage(peer2.getIp(), peer2.getPort(), joinMsg);
                store.addPeer(peer2);
            }
        } else if (peerCount == 9997) {
            System.err.println("Bootstrap server already filled");
        } else if (peerCount == 9998) {
            System.err.println("Server error");
        } else if (peerCount == 9999) {
            System.err.println("Peer already registered");
            sendTCPMessage(store.getServerIp(),
                           store.getServerPort(),
                           MessageType.UNREG + " " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            sendTCPMessage(store.getServerIp(),
                           store.getServerPort(),
                           MessageType.REG + " " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
        }
    }

    private static int getRandom(int peerCount) {
        return ThreadLocalRandom.current().nextInt(1, peerCount + 1);
    }

}
