
package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//客户端
public class clientUDP {
    private static final int MAXREV = 255;

    public static void main(String[] args) throws IOException {
        /* 1. 广播 */
        config.getNowDate(new String("1.1.broadcasting"));
        byte[] msg = config.newUserName.getBytes();
        InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
//        InetAddress inetAddr = InetAddress.getByName("192.168.3.55");
//        InetAddress inetAddr = InetAddress.getByName("192.168.3.57");
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, 8888);

        client.send(sendPack);
        client.close();
        config.getNowDate(new String("1.2.finishing client broadcasting"));


        /* 2. 开启服务器，等待接收信息 */
        DatagramSocket server = new DatagramSocket(9999);
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);

        System.out.println("---------------------------------");
        config.getNowDate(new String("new car is waiting......"));
        System.out.println("---------------------------------");

        server.receive(recvPacket);

        byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                recvPacket.getOffset(),
                recvPacket.getOffset() + recvPacket.getLength());


//            System.out.println("Handing at client "
//                    + recvPacket.getAddress().getHostName() + " ip "
//                    + recvPacket.getAddress().getHostAddress());
        config.getNowDate(new String("Handing at client "
                + recvPacket.getAddress().getHostName() + " ip "
                + recvPacket.getAddress().getHostAddress() +
                ", Server Receive Data:" + new String(receiveMsg)));
        server.send(recvPacket);


        /* 2. 数据写入文件，结束服务器端 */
        File f = new File(config.newUserName + ".id");
        if (!f.exists()) { f.createNewFile(); }
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(new String(receiveMsg));
        bw.close();

        System.out.println("---------------------------------");
        config.getNowDate(new String("received identify and then exit"));
        System.out.println("---------------------------------");
    }
}