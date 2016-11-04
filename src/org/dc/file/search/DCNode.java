package org.dc.file.search;

import java.io.IOException;
import java.util.Calendar;

public class DCNode {

    public static void main(String[] args) throws IOException {
        long username = Calendar.getInstance().getTimeInMillis() % 1000000000;
        Peer localPeer = MessageUtils.init(username);
        MessageUtils.sendMessage(Constants.BOOTSTRAP_SERVER, Constants.BOOTSTRAP_PORT, "REG " + localPeer.getIp() + " " + localPeer.getPort() + " " + username);
    }

}
