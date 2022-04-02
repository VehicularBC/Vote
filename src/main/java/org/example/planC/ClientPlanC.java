/* 预切换方案
 * 车辆客户端
 * 要离开时发送请求或者入网时申请RSU
 * */
// port 8888 is the RSU1
// port 9999 is the RSU2
// port 10999 is the car
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
import java.util.concurrent.TimeUnit;

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

//客户端
public class ClientPlanC {
    private static final int MAXREV = 4096;
    /* 错误码
     * 2001 等待UID超时
     * 2002 等待身份文件超时
     * 2003 打开文件保存出错 */

    public static boolean printError(int e) {
        if (e == 0) {
            // 无事发生
            return false;
        } else {

//            // 需要输出信息：错误码、完成状态、耗费时长
//            System.out.println("---------------------------------------------------------------------");
//            config.getNowDate("白板车认证总时长:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
//            System.out.println(new String("received identify and then exit"));
//            System.out.println("---------------------------------------------------------------------");
            if (e == 2001) {
                System.out.println("广播等待回复超时");
            }
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] msgB = null;
        String dstIP = null;

        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");  //Broad address
        InetAddress inetAddrForRSU = InetAddress.getByName("192.168.1.103");
        InetAddress inetAddrForRSU2 = InetAddress.getByName("192.168.1.7");
        DatagramSocket client = new DatagramSocket();
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
        DatagramSocket serverForId = new DatagramSocket(10999);

        boolean leaveState = false;
        // 1.自身用户信息，得到token
        //   leaveState = true;
        JsonUtilsC txt = new JsonUtilsC("1", 0, config.localUserName);   //向前一区域RSU发送离开请求
        String msg = JSON.toJSONString(txt);
        msgB = msg.getBytes();
        DatagramPacket sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForRSU, 8888);
        client.send(sendPack);   //向前一区域RSU发送离开请求
        config.getNowDate("1 向RSU发送离开请求");
        long break1 = System.currentTimeMillis();
        while (!leaveState) {
            // 等返回token



                serverForId.receive(recvPacket);   //等待前一区域RSU返回Token

                byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                        recvPacket.getOffset() + recvPacket.getLength());
                JSONObject json = JSONObject.parseObject(new String(receiveMsg));  //将消息转换为Json
                if (Integer.parseInt(json.getString("Type")) == 2) {
                    // 保存token
                    config.getNowDate("2 从RSU得到加密Token");
                    config.token = json.getString("token");
                    String tokengot = RSAUtils.decrypt(config.token, org.example.RSAUtils.keyMap.get(1));  //将加密的Token解密
                    config.tokenhash= tokengot.hashCode();  //计算Token哈希值
                    config.getNowDate("3 Token解密完成，存储完成");
                    leaveState = true ;
                    break;
                } else if ((System.currentTimeMillis() - break1) / 1000.0 >= 5.0) {
                    client.close(); // 关闭连接
                    break;


            }
        }

        long start = 0;

        // 2.发送token，来获取id文件
        if (leaveState) {

            long break2 = System.currentTimeMillis();
            txt = new JsonUtilsC("3", 0, config.localUserName);
            txt.setHash(config.tokenhash);
            msg = JSON.toJSONString(txt);
            msgB = msg.getBytes();
            sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForRSU2, 9999);

            TimeUnit.MILLISECONDS.sleep(600);//延时600ms，模拟跨区过程

            start = System.currentTimeMillis();
            client.send(sendPack);          //进入下一区域，将token的哈希值传递给该区域RSU，开始计时
            config.getNowDate("4 Token哈希值已发往下一区域RSU");

            /* 接收.id文件 */
            String walletContent = "";
            while (true) {

                serverForId.receive(recvPacket);    //接收ID文件

                byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                        recvPacket.getOffset() + recvPacket.getLength());

                JSONObject json = JSONObject.parseObject(new String(receiveMsg));
                if (Integer.parseInt(json.getString("Type")) == 4) {
                    walletContent = json.getString("wc");

                    /* 保存 */
                    File f = new File(config.newUserName + ".id");
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    FileWriter fw = new FileWriter(f.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(walletContent);
                    bw.close();    // 关闭文件
                    config.getNowDate("5 收到身份文件，保存完成");
                    break;

                } else if ((System.currentTimeMillis() - break2) / 1000.0 >= 5.0) {
                    client.close(); // 关闭连接
                    break;
                }
            }
        }


        /* 结束;输出日志 */
        // 需要输出信息：错误码、完成状态、耗费时长
        System.out.println("---------------------------------------------------------------------");
        config.getNowDate("白板车认证总时长:" + (System.currentTimeMillis() - start) + "ms");
        System.out.println(new String("exit"));
        System.out.println("---------------------------------------------------------------------");

        /* 善后工作不计时了吧 */
        client.close();  // 关闭连接
    }
}