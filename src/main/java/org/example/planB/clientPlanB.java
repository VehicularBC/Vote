
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
public class clientPlanB {
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

        /* 制造RSU签名摘要
        * 1.获取车辆信誉值
        * 2.进行签名 */
        int carReputation = 1;
        // 数字签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(RSAUtils.getPrivateKey(RSAUtils.keyMap.get(1)));  // 私钥签名
//        signature.update(new String(String.valueOf(carReputation)).getBytes());
        signature.update(new String("C = (0x5b2a30f234c921342488f72816074fa3a00e82cf6097aa6b0ee73ba3 , 0x70e1c45e1a512406efbccae2d3f8742c2bda47a4faeb8f52680b6742)\n" +
                "C' = (0xd481f873d684a2ba2b0cad81b376214aa39cf6270984fbda4d53f106 , 0xef847d2d94cc93f41686b44fb1106ccaccde7fe00ff8493fce8950ff)\n" +
                "C'' = (0xba412b6c6a569dc8e93048b573b39e57aeb1fecfd63ef6ce9ad4f428 , 0xc04017783302bfca1e982c841fb61d2ddfc3a279d3d9297ad3e8fca7)\n" +
                "C1' = (0x74d2c3e9278a7bad48429b2566265f82c314d67a2e6064f6505f310d , 0x2df22aef3026b5ff11b9f19f345a3dc57c0c3593b64c0ea035c89267)\n" +
                "C2' = (0x50ea89635c85815c06046ce9efe7b4f6dc3d3ee21e383307a3c28d6 , 0xfb5889e7cbdfd71e0173439d6acaa2bab09a40d5436376d74450aba6)\n" +
                "R = 249810819438774246179065676773868888676388300954217050982368782935044\n" +
                "EL = (2185746235111599876748933452662056647877904631351505094538792557224, 47955222495009547204797724004601882133388295408305352521680571719922, -6469592113752543222244675372979752603686113630811187850302264394590609020613365148399704986385465778724657974241665204167606262609730, 472600353593178787889058214998215975364SQR1 = (7063931031876342549145481153955882658193185066954324665476123426833, 10163319679926217394695253128479251678043427139032399987747198749603248453647340252980670468559922250, 164806967977471341121290580399895079648439471153872224493817346331307426572348459187115683483933162658106265435839566994078325886194921, -2371180994655732944188282158652269851826764647992152363172441102828721416560722357637108802265700898798955018425606016415864739958").getBytes());
        byte[] result = signature.sign();
        String commit = Hex.encodeHexString(result);
        System.out.println("jdk rsa sign : " + Hex.encodeHexString(result));


        /* 8888发信息：广播和单播
         * 9999接受广播回复
         * 10999接受单播回复 */
        String dstIP = null;
        DatagramSocket client = new DatagramSocket();
        DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
        DatagramSocket serverForBroad = new DatagramSocket(9999);
        DatagramSocket serverForId = new DatagramSocket(10999);
        long begin = System.currentTimeMillis();

        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");

        /* 1. 广播 */

//        String s = JSON.toJSONString(txt);
//        System.out.println(s);
//        JSONObject json = JSONObject.parseObject(s);
//        System.out.println(json);
//
//        System.out.println(json.getString("Type"));
//        System.out.println(json.getJSONArray("uList"));

        // #################################################################
        JsonUtils txt = new JsonUtils("1", 0, config.newUserName, commit, new String[]{}, "");
        String msg = JSON.toJSONString(txt);
        System.out.println(msg);
//        System.out.println("jdk rsa sign : " + msg);
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
            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());

            String receMsg = new String(receiveMsg);
            config.getNowDate(new String("Handing at client "
                + recvPacket.getAddress().getHostName() + " ip "
                + recvPacket.getAddress().getHostAddress() +
                ", Server Receive Data:" + receMsg));

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
        for (int i = 0; i < receivedUID.size(); i ++) {
            uList[i] = (String)(receivedUID.get(i));
        }
//        System.out.println(uList);
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
            byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                    recvPacket.getOffset(),
                    recvPacket.getOffset() + recvPacket.getLength());
            config.getNowDate(new String("Handing at client "
                    + recvPacket.getAddress().getHostName() + " ip "
                    + recvPacket.getAddress().getHostAddress() +
                    ", Server Receive Data:" + new String(receiveMsg)));

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
                bw.close();  // 关闭文件
                break;
            } else if ((System.currentTimeMillis() - break1) / 1000.0 >= 5.0) {
                client.close();  // 关闭连接
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
        client.close();  // 关闭连接
    }
}