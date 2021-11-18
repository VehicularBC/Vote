/* 预切换方案
 * RSU服务器
 * 接受其他区域RSU的申请或者车辆的申请
 * */

package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;



import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.text.SimpleDateFormat;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


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

import org.example.JsonToken;
import org.example.JsonUtilsC;
import org.example.RSAUtils;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.apache.milagro.amcl.RSA2048.private_key;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric.gateway.Wallet;
//import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.Attribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


//import org.example.java_py_thr;
//import org.example.java_py_test;
//import org.example.Zkrp_judge;

//服务端
public class RSUPlanc {
    private static final int MAXRECEIVED = 255;
    private Socket socket;

    private static String orgName = "org1.example.com";
    private static String orgConnectionName = "connection-org1.yaml";


    private static Wallet wallet = null;
    private static X509Identity adminIdentity = null;
    private static User admin = null;
    private static HFCAClient caClient = null;
    private static CryptoSuite cryptoSuite = null;

    private static Gateway.Builder builder = null;
    private static Gateway gateway = null;
    private static Network network = null;
    private static Contract contract = null;

    //    HFCAClient caClient = null;
    public static void registerUser(String userName) {
        try {
            // Check to see if we've already enrolled the user.
//            if (wallet.get(userName) != null) {
//                System.out.println("An identity for the user " + userName + " already exists in the wallet");
//                return;
//            }

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(userName);
            registrationRequest.setAffiliation(config.affiliation);
            registrationRequest.setEnrollmentID(userName);
            String enrollmentSecret = caClient.register(registrationRequest, admin);

            //定义一个enrollmentRequest，在里面设置需要加入到证书中的属性
            //不设置的话，只把默认的hf.Affiliation, hf.EnrollmentID, hf.Type加入到证书中
            EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
            enrollmentRequest.addAttrReq("hf.Affiliation");		//default attribute
            enrollmentRequest.addAttrReq("hf.EnrollmentID");	//default attribute

            Enrollment enrollment = caClient.enroll(userName, enrollmentSecret, enrollmentRequest);

            Identity user = Identities.newX509Identity(config.MSPId, enrollment);
//            Identity user = Identity.createIdentity(config.MSPId, enrollment.getCert(), enrollment.getKey());

            wallet.put(userName, user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String voteForcar(String carID) throws Exception { //为新车进行投票，调用链码，返回一个byte类数组，第一个元素为链码返回结果（待测试）
        byte[] voteResult = null;

        // create a gateway connection
        try {
            config.getNowDate(new String("2.1.1.waiting for voting"));

            voteResult = contract.createTransaction("VoteForTempCar").submit(carID);
            config.getNowDate(new String(voteResult, StandardCharsets.UTF_8));
            voteResult = contract.createTransaction("GetVoteResult").submit(carID);
            config.getNowDate(new String(voteResult, StandardCharsets.UTF_8));

            config.getNowDate(new String("2.1.2.receiving for voting"));
        } catch (ContractException | TimeoutException | InterruptedException e) {
//            config.getNowDate(new String());
            e.printStackTrace();
        }
        return new String(voteResult, StandardCharsets.UTF_8);
    }

    private static void InitParam() throws Exception  {
        Path networkConfigPath = Paths.get("src", "main", "resources", "crypto-config", "peerOrganizations", orgName, orgConnectionName);

        // Create a wallet for managing identities
        Path walletPath = Paths.get("wallet");
        wallet = Wallets.newFileSystemWallet(walletPath);

        cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();

        /* 注册
         * HFCAclient、
         * */
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", "src/main/resources/crypto-config/" + "peerOrganizations/" + config.pemDir);
        props.put("allowAllHostNames", "true");

        caClient = HFCAClient.createNewInstance("https://" + config.peerHostIp + ":" + config.peerHostPort, props);
        caClient.setCryptoSuite(cryptoSuite);


        adminIdentity = (X509Identity) wallet.get(config.adminName);
        if (adminIdentity == null) {
            System.out.println(config.adminName + " needs to be enrolled and added to the wallet first");
            return;
        }
        admin = new User() {

            @Override
            public String getName() {
                return config.adminName;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return config.affiliation;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return config.MSPId;
            }

        };

        /* 调用SDK
         * builder、gateway、network、contract
         * */
        // 基于本地用户和配置文件
        builder = Gateway.createBuilder();
        builder.identity(wallet, config.localUserName).networkConfig(networkConfigPath).discovery(true);

        // get the network and contract
        gateway = builder.connect();
        network = gateway.getNetwork(config.channelName);  // mychannel
        contract = network.getContract(config.contractName);  // fabcar
    }
    /* 白盒模型
     * 用来判决是否满足信誉需求 */
    public static boolean whiteBox(int input) throws Exception {
        if (input >= config.reputation) {
            return true;
        }
        return false;
    }
    public static String msgReceive(String data) {
        /*
        收到车辆发送的请求，解析得到json
        * 包括用户名和commit，commit作范围判断估值*/

        return "";
    }
    public static void main(int myport) throws Exception {   //将端口作为参数传入main函数，用不同端口模拟不同RSU

        InitParam();  // 初始化系统参数

        /* 1. 启动服务器 */
        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(myport);

        System.out.println("-----------------------------------------------------------");
        config.getNowDate(new String("Server current start......"));
        System.out.println("-----------------------------------------------------------");

        DatagramSocket client = new DatagramSocket();

        InetAddress inetAddrForBroad = InetAddress.getByName("255.255.255.255");
        InetAddress inetAddrForCar = InetAddress.getByName("192.168.1.75");
        InetAddress inetAddrForRSU = InetAddress.getByName("192.168.1.132");
        InetAddress inetAddrForRSU2 = InetAddress.getByName("192.168.1.76");


        String judeg = java_py_test.judge_one("40");
       // System.out.println(judeg);
      //  int ff = 0;
        String content = null;
        ArrayList<Integer> hashbuffer = new ArrayList<Integer>();
        ArrayList<String> idbuffer = new ArrayList<String>();



        while (true) {
            /* 2. 监听到客户端消息 */
            long begin = System.currentTimeMillis();
            server.receive(receive);
            String recIp = receive.getAddress().getHostAddress();  // IP

            byte[] msgByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());  // 收到消息
            String msgRece = new String(msgByte);
            JSONObject json = JSONObject.parseObject(msgRece);  // 字符串转json格式

            /* 收到消息格式 */
            int type = Integer.parseInt(json.getString("Type"));
            String tokengot ;     //定义tokengot变量用于存储消息中得到的token
            String newUserName = json.getString("userID");

            JsonUtilsC txt = null;

            int dstPort1 = 9999;  //the port of the RSU2
            int dstPort2 = 10999; //the port of the car

            try {

                switch (type) {
                    case 0:     //RSU间通信，收到前一区域RSU给来的token，解密并计算其哈希值，将解密后的token与其哈希值存储。

                        config.getNowDate("Got a token from another RSU. Start registering for the vehicle.");
                        tokengot = json.getString("token");
                        tokengot = RSAUtils.decrypt(tokengot, RSAUtils.keyMap.get(1));  //将加密过的Token解密
                        int hashgot = tokengot.hashCode();    //计算解密后Token的哈希值
                        JSONObject token1 = JSONObject.parseObject(tokengot);  //将字符串形式的token转换为json
                        newUserName = token1.getString("userID");
                        registerUser(newUserName);   //为该小车注册身份
                        content = new String(Files.readAllBytes(Paths.get("wallet/" + newUserName + ".id")));
                        idbuffer.add(content);     //存储身份文件
                        hashbuffer.add(hashgot);   //存储token的哈希值
                        config.getNowDate("The vehicle has been registered, and the hash value of the toke and the ID information have been stored");
                        break;


                    case 1:     // 收到车辆离开请求：查询信誉值然后制作token字段，将其加密后传给白板车以及下一区域RSU

                        config.getNowDate("Got a leaving request from a vehicle, start making a token for the vehicle and the next RSU.");
                        long requestTime = json.getLong("curTime");
                        int reputation = 70;
//加个消息
                        byte[] msg2car = null;
                        byte[] msg2RSU2 = null;

                        JsonToken jt = new JsonToken(newUserName , reputation , requestTime);  //在Token中存入信息，包括用户名，名誉值以及用户请求时间，
                        String token = JSON.toJSONString(jt);
                        String encryptToken = RSAUtils.encrypt(token,RSAUtils.keyMap.get(0));   //将Token加密

                        JsonUtilsC message2car = new JsonUtilsC("2", 0 , newUserName) ;    //将加密后的Token放入消息中
                        JsonUtilsC message2RSU = new JsonUtilsC("0", 0 , newUserName) ;

                        message2RSU.setToken(encryptToken);
                        message2car.setToken(encryptToken);

                        String msg = JSON.toJSONString(message2car);
                        msg2car = msg.getBytes();   //Message to car

                        String msgB = JSON.toJSONString(message2RSU);
                        msg2RSU2 = msgB.getBytes();  //Message to RSU2

                        DatagramPacket sendPacktoCar = new DatagramPacket(msg2car, msg2car.length, inetAddrForCar, 10999);
                        server.send(sendPacktoCar);   //将token发送给车
                        DatagramPacket sendPacktoRSU2 = new DatagramPacket(msg2RSU2, msg2RSU2.length, inetAddrForRSU2, 9999);
                        server.send(sendPacktoRSU2);   //将token发送给下一个RSU

                        config.getNowDate("Send the token to the vehicle and the next RSU.");

                        break;


                    case 3:     //收到小车发来的Token的哈希值，与缓存中的哈希值进行对比，若成功，则使用token中的信息为小车注册身份，并将身份文件返回给小车，在缓存中将该token以及对应的哈希值删除。
                        config.getNowDate("Got a hash value of a token from a vehicle");
                        int tokenhash = json.getInteger("hash");

                        if(hashbuffer.contains(tokenhash)){  //对比哈希值
                            //
                            int ind = hashbuffer.indexOf(tokenhash);                  //获取该哈希值在ArrayList中的索引

                            JsonUtilsC ID2Car = new JsonUtilsC("4", 0, config.localUserName);
                            content = idbuffer.get(ind);
                            ID2Car.setWC(content);
                            String id = JSON.toJSONString(ID2Car);
                            byte[] id2car = id.getBytes();   //Message to car
                            sendPacktoCar = new DatagramPacket(id2car, id2car.length, inetAddrForCar, 10999);
                            server.send(sendPacktoCar);    //将存有身份信息的消息传给白板车
                            config.getNowDate("Hash matched，The ID information has been transmitted to the vehicle.");
                            hashbuffer.remove(ind);        //删除缓存
                            idbuffer.remove(ind);

                        }


                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 当前用户交互已完成，断开连接
            System.out.println("-----------------------------------------------------------");
//            config.getNowDate("认证车总时长:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
//            System.out.println("finish the interaction with user " + newUserName);
//            System.out.println("-----------------------------------------------------------");

        }




        // client.close();
    }
}