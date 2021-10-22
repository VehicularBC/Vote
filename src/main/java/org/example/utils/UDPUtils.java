package org.example;

import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPUtils {
    public static void sendMsg(DatagramSocket client, String ip, int prot, String msg) throws Exception {
        byte[] msgByte = msg.getBytes();
        InetAddress inetAddr = InetAddress.getByName(ip);
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, prot);
        DatagramPacket sendPacket = new DatagramPacket(msgByte, msgByte.length, socketAddr);
        client.send(sendPacket);
    }
}