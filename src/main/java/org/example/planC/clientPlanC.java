
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
public class clientPlanC {
    private static final int MAXREV = 4096;

    public static void main(String[] args) throws Exception {
        /* 8888发信息：广播和单播
        * 9999接受广播回复
        * 10999接受单播回复 */
        long begin = System.currentTimeMillis();

        DatagramSocket serverForBroad = new DatagramSocket(9999);
        DatagramSocket serverForId = new DatagramSocket(10999);
        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");

        /* 1. 广播 */
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
        ArrayList receivedUID = new ArrayList();

        String msgEn = RSAUtils.encrypt(new String("1" + config.newUserName), RSAUtils.keyMap.get(0));
//        config.getNowDate(msgEn);
        byte[] msg = msgEn.getBytes();

        
        DatagramSocket client = new DatagramSocket();
        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddrForBroad, 8888);

        /* 2. 开启服务器，等待接收信息 */
        long break1 = System.currentTimeMillis();
        boolean hasBroadcast = false;
        while (true) {
            if (!hasBroadcast) {
                config.getNowDate(new String("1.1.broadcasting"));
                client.send(sendPack);
                hasBroadcast = true;
            } else {
                config.getNowDate(new String("1.1.waiting UID"));
            }
            serverForBroad.receive(recvPacket);

            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            config.getNowDate(new String("Handing at client "
                    + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress() +
                    ", Server Receive Data:" + new String(receiveMsg)));

            String receMsgEn = new String(receiveMsg);
            String receiveMsgDe = RSAUtils.decrypt(receMsgEn, RSAUtils.keyMap.get(1));
            receivedUID.add(receiveMsgDe);
            if (receivedUID.size() >= 1) {
                break;
            }
        }
        System.out.println("第一阶段:" + (System.currentTimeMillis() - break1) / 1000.0 + "秒");

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

        /* 结束 */

        System.out.println("---------------------------------------------------");
        System.out.println("总时间:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
        config.getNowDate(new String("received identify and then exit"));
        System.out.println("---------------------------------------------------");

        // 善后工作不及时了吧
        bw.close();  // 关闭文件
        client.close();// 关闭连接
    }
}