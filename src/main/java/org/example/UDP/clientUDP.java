
package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

//客户端
public class clientUDP {
    private static final int MAXREV = 255;


    public static void main(String[] args) throws IOException {
        String userName = "gibbon";
        byte[] msg = userName.getBytes();

        InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, 8888);

        client.send(sendPack);
        System.out.println("Client send msg complete");
        client.close();


        // 开启服务器，等待接收信息
        DatagramSocket server = new DatagramSocket(9999);
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);

        System.out.println("---------------------------------");
        System.out.println("new car is waiting......");
        System.out.println("---------------------------------");
        while (true) {
            server.receive(recvPacket);

            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            System.out.println("Handing at client "
                    + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress());

            System.out.println("Server Receive Data:" + new String(receiveMsg));
            server.send(recvPacket);

            // 数据写入文件，结束服务器端
            File f = new File(userName + ".id");
            if (!f.exists()) { f.createNewFile(); }
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new String(receiveMsg));
            bw.close();
        }
    }
}