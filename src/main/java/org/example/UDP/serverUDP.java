
package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

//服务端
public class serverUDP {
    private static final int MAXRECEIVED = 255;
    public static void main(String[] args) throws IOException {
        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(8888);

        System.out.println("---------------------------------");
        System.out.println("Server current start......");
        System.out.println("---------------------------------");

        while (true) {
            server.receive(receive);

            byte[] recvByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());

            System.out.println("Server receive msg:" + new String(recvByte));


            // 收到新车请求，由该车负责调用其他车辆链码
            // 由链码返回1的认证车进行发送数据
            byte[] msg = new String("connect test successfully!!!").getBytes();
            DatagramSocket client = new DatagramSocket();
            InetAddress inetAddr = InetAddress.getLocalHost();
            SocketAddress socketAddr = new InetSocketAddress(inetAddr, 9999);
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);
            client.send(sendPacket);
            client.close();
        }

    }
}