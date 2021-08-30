
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
import java.util.ArrayList;
import java.util.List;

//客户端
public class clientPlanB {
    private static final int MAXREV = 4096;

    public static void main(String[] args) throws IOException {
        /* 8888发信息：广播和单播
         * 9999接受广播回复
         * 10999接受单播回复 */

        DatagramSocket serverForBroad = new DatagramSocket(9999);
        DatagramSocket serverForId = new DatagramSocket(10999);
        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");

        /* 1. 广播 */
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
        ArrayList receivedUID = new ArrayList();

        byte[] msg = new String("1" + config.newUserName).getBytes();


        DatagramSocket client = new DatagramSocket();
        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddrForBroad, 8888);

//        client.close();
        /* 2. 开启服务器，等待接收信息 */
        boolean hasBroadcast = false;
        while (true) {
            if (!hasBroadcast) {
                config.getNowDate(new String("1.1.broadcasting"));
                client.send(sendPack);
                hasBroadcast = true;
            }
            serverForBroad.receive(recvPacket);

            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            config.getNowDate(new String("Handing at client "
                    + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress() +
                    ", Server Receive Data:" + new String(receiveMsg)));

            receivedUID.add(new String(receiveMsg));
            if (receivedUID.size() >= 3) {
                break;
            }
        }

        /* 3. 随机发送uid到认证车 */
        String result = new String("2-" + config.newUserName);
        for(int i = 0; i < receivedUID.size(); i++) {
            result = result + "-" + receivedUID.get(i);
        }
        config.getNowDate(result);
        msg = result.getBytes();

        client = new DatagramSocket();

        InetAddress inetAddrForId = InetAddress.getByName("192.168.3.57");
        sendPack = new DatagramPacket(msg, msg.length, inetAddrForId, 8888);
        client.send(sendPack);
//        client.close();

        /* 4. 接收.id文件 */
        serverForId.receive(recvPacket);

        byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                recvPacket.getOffset(),
                recvPacket.getOffset() + recvPacket.getLength());

        config.getNowDate(new String("Handing at client "
                + recvPacket.getAddress().getHostName() + " ip "
                + recvPacket.getAddress().getHostAddress() +
                ", Server Receive Data:" + new String(receiveMsg)));
        serverForId.send(recvPacket);

        // 保存
        File f = new File(config.newUserName + ".id");
        if (!f.exists()) {
            f.createNewFile();
        }
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(new String(receiveMsg));
        bw.close();

        // 结束
        System.out.println("---------------------------------");
        config.getNowDate(new String("received identify and then exit"));
        System.out.println("---------------------------------");
    }
}