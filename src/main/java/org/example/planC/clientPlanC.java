/* 预切换方案
 * 车辆客户端
 * 要离开时发送请求或者入网时申请RSU
 * */

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

//客户端
public class clientPlanC {
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
        int err = 0;

        JsonUtilsC txt = null;

        boolean leaveState = false;
        // 1.自身用户信息，得到token
        leaveState = True;
        long break1 = System.currentTimeMillis();
        while (!leaveState) {
            // 等返回token
            if (true) {
                break;
            }
            txt = new JsonUtils("1", 0, config.localUserName, "", "");

            InetAddress inetAddrForId = InetAddress.getByName(dstIP);
            sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForId, 8888);
            client.send(sendPack);

            /* 接收 token */
            String walletContent = "";
            while (true) {
                serverForId.receive(recvPacket);
                byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                        recvPacket.getOffset() + recvPacket.getLength());

                JSONObject json = JSONObject.parseObject(new String(receiveMsg));
                if (Integer.parseInt(json.getString("Type")) == 2) {
                    // 保存token
                    config.token = json.getString("token");
                    break;
                } else if ((System.currentTimeMillis() - break1) / 1000.0 >= 5.0) {
                    client.close(); // 关闭连接
                    break;
                }

        }


        // 2.发送token，来获取id文件
        if (leaveState) {
            long break2 = System.currentTimeMillis();
            txt = new JsonUtils("4", 0, config.localUserName, "", new String[]{}, "");

            InetAddress inetAddrForId = InetAddress.getByName(dstIP);
            sendPack = new DatagramPacket(msgB, msgB.length, inetAddrForId, 8888);
            client.send(sendPack);

            /* 接收.id文件 */
            String walletContent = "";
            while (true) {
                serverForId.receive(recvPacket);
                byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(),
                        recvPacket.getOffset() + recvPacket.getLength());

                JSONObject json = JSONObject.parseObject(new String(receiveMsg));
                if (Integer.parseInt(json.getString("Type")) == 4) {
                    walletContent = json.getString("wc");
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
                } else if ((System.currentTimeMillis() - break2) / 1000.0 >= 5.0) {
                    client.close(); // 关闭连接
                    break;
                }
            }
        }


        /* 结束;输出日志 */
        // 需要输出信息：错误码、完成状态、耗费时长
        System.out.println("---------------------------------------------------------------------");
        config.getNowDate("白板车认证总时长:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
        System.out.println(new String("received identify and then exit"));
        System.out.println("---------------------------------------------------------------------");

        /* 善后工作不计时了吧 */
        client.close();  // 关闭连接
    }
}