
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.example.java_py_test;

//客户端
public class clientPlanB {
    private static final int MAXREV = 4096;
    /*
     * 错误码 2001 等待UID超时 2002 等待身份文件超时 2003 打开文件保存出错
     */

    public static boolean printError(int e) {
        if (e == 0) {
            // 无事发生
            return false;
        } else {

            // // 需要输出信息：错误码、完成状态、耗费时长
            // System.out.println("---------------------------------------------------------------------");
            // config.getNowDate("白板车认证总时长:" + (System.currentTimeMillis() - begin) / 1000.0
            // + "秒");
            // System.out.println(new String("received identify and then exit"));
            // System.out.println("---------------------------------------------------------------------");
            if (e == 2001) {
                System.out.println("广播等待回复超时");
            }
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        int err = 0;

        /*
         * 制造RSU签名摘要 1.获取车辆信誉值 2.进行签名
         */

        // 传入carRep
        int carReputation = config.reputation;
        String zkrp = java_py_test.produce("75");

        // 数字签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(RSAUtils.getPrivateKey(RSAUtils.keyMap.get(1))); // 私钥签名
        // signature.update(new String(String.valueOf(carReputation)).getBytes());
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        signature.update(zkrp.getBytes());
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        byte[] result = signature.sign();
        String commit = Hex.encodeHexString(result);
        System.out.println("jdk rsa sign : " + Hex.encodeHexString(result));

        /*
         * 8888发信息：广播和单播 9999接受广播回复 10999接受单播回复
         */
        String dstIP = null;
        DatagramSocket client = new DatagramSocket();
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
        DatagramSocket serverForBroad = new DatagramSocket(9999);
        DatagramSocket serverForId = new DatagramSocket(10999);
        long begin = System.currentTimeMillis();

        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");

        /* 1. 广播 */

        // String s = JSON.toJSONString(txt);
        // System.out.println(s);
        // JSONObject json = JSONObject.parseObject(s);
        // System.out.println(json);
        //
        // System.out.println(json.getString("Type"));
        // System.out.println(json.getJSONArray("uList"));

        // #################################################################
        JsonUtils txt = new JsonUtils("1", 0, config.newUserName, commit, new String[] {}, "");
//        txt.setErrCode(1);
        String msg = JSON.toJSONString(txt);
        System.out.println(msg);
        // System.out.println("jdk rsa sign : " + msg);
        // #################################################################

        byte[] msgB = msg.getBytes();

        /* 2. 开启服务器，等待接收信息 */
        long break1 = System.currentTimeMillis();
        boolean hasBroadcast = false;
        DatagramPacket sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForBroad, 8888);
        ArrayList receivedUID = new ArrayList();
        while (true) {
            if (!hasBroadcast) {
                client.send(sendPack);
                hasBroadcast = true;
            }

            serverForBroad.receive(recvPacket);
            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            String receMsg = new String(receiveMsg);
            config.getNowDate(new String("Handing at client " + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress() + ", Server Receive Data:" + receMsg));

            JSONObject json = JSONObject.parseObject(receMsg);
            if (Integer.parseInt(json.getString("Type")) == 2) {
                receivedUID.add(json.getString("userID"));
                if (dstIP == null) {
                    dstIP = recvPacket.getAddress().getHostAddress();
                }
            }
            // exit judgement
            if (receivedUID.size() >= config.minUIDNum) {
                break;
            } else if ((System.currentTimeMillis() - break1) / 1000.0 >= 5.0) {
                err = 2001;
                break;
            }
        }
        if (printError(err)) {
            return;
        }
        System.out.println("第一阶段(白板车广播):" + (System.currentTimeMillis() - break1) / 1000.0 + "秒");

        /* 3. 随机发送uid到认证车 */
        String[] uList = new String[config.minUIDNum];
        for (int i = 0; i < receivedUID.size(); i++) {
            uList[i] = (String) (receivedUID.get(i));
        }
        // System.out.println(uList);
        txt = new JsonUtils("3", 0, config.newUserName, "", uList, "");
        msg = JSON.toJSONString(txt);
        System.out.println(msg);
        msgB = msg.getBytes();

        // 三板斧：定义ip、ip包装msg、发送
        InetAddress inetAddrForId = InetAddress.getByName(dstIP);
        sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForId, 8888);
        client.send(sendPack);

        /* 4. 接收.id文件 */
        String walletContent = "";
        while (true) {
            serverForId.receive(recvPacket);
            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());
            config.getNowDate(new String("Handing at client " + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress() + ", Server Receive Data:" + new String(receiveMsg)));

            JSONObject json = JSONObject.parseObject(new String(receiveMsg));
            if (Integer.parseInt(json.getString("Type")) == 4) {
                walletContent = json.getString("wC");
                config.getNowDate("白板车收到身份文件,接下来保存到本地");

                /* 保存 */
                File f = new File(config.newUserName + ".id");
                if (!f.exists()) {
                    f.createNewFile();
                }
                System.out.println("received the identity file and save locally");
                FileWriter fw = new FileWriter(f.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(walletContent);
                bw.close(); // 关闭文件
                break;
            } else if ((System.currentTimeMillis() - break1) / 1000.0 >= 5.0) {
                client.close(); // 关闭连接
                break;
            }
        }

        /* 结束;输出日志 */
        // 需要输出信息：错误码、完成状态、耗费时长
        System.out.println("---------------------------------------------------------------------");
        config.getNowDate("白板车认证总时长:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
        System.out.println(new String("received identify and then exit"));
        System.out.println("---------------------------------------------------------------------");

        /* 善后工作不计时了吧 */
        client.close(); // 关闭连接
    }
}