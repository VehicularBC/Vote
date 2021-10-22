/* 预切换方案
* RSU服务器
* 接受其他区域RSU的申请或者车辆的申请
* */

package org.example;

import java.io.File;
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
import java.util.Set;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.HashSet;
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
public class rsuPlanC {
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
    public static void RegisterUser(String userName) {
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
    public static void main(String[] args) throws Exception {
//        InitParam();  // 初始化系统参数

        /* 1. 启动服务器 */
        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(11999);

        System.out.println("-----------------------------------------------------------");
        config.getNowDate(new String("Server current start......"));
        System.out.println("-----------------------------------------------------------");

        DatagramSocket client = new DatagramSocket();



        String judeg = java_py_test.judge_one("40");
        System.out.println(judeg);


        while (true) {
            /* 2. 监听到客户端消息 */
            long begin = System.currentTimeMillis();
            server.receive(receive);
            String recIp = receive.getAddress().getHostAddress();  // IP
            byte[] msgByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());  // 收到消息

            String msgRece = new String(msgByte);
            System.out.println(msgRece);

            JSONObject json = JSONObject.parseObject(msgRece);  // 字符串转json格式
//            System.out.println(json);
            /* 收到消息格式 */
            int type = Integer.parseInt(json.getString("Type"));
            String newUserName = json.getString("userID");


            JsonUtils txt = null;

            int dstPort = 0;
            try {
                switch (type) {
                    case 1:
                        // 收到离开请求：查询信誉值然后制作token字段

                        JsonUtils tokenJson = null;
                        String token = JSON.toJSONString(tokenJson);

                        txt = new JsonUtils("2", 0, config.localUserName, "", new String[]{}, "");
                        dstPort = 10999;
                    case 0:
//                    // RSU提前切换
//                    RegisterUser(newUserName);
                        break;
                    case 4:
                        // 收到入网申请身份请求，返回身份文件
                        // 包括身份确认、超时确认

                        if (1 == 1) {
                            String content = new String(Files.readAllBytes(Paths.get("wallet/" + newUserName + ".id")));
                            txt = new JsonUtils("4", 0, config.localUserName, "", new String[]{}, "");
                        }
                        break;
                    default:
                        break;
                }
                if (dstPort != 0) {
                    String sendMsg = JSON.toJSONString(txt);
                    byte[] msg = sendMsg.getBytes();

                    InetAddress inetAddr = InetAddress.getByName(recIp);
                    SocketAddress socketAddr = new InetSocketAddress(inetAddr, dstPort);
                    DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);
                    client.send(sendPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 当前用户交互已完成，断开连接
            System.out.println("-----------------------------------------------------------");
            config.getNowDate("认证车总时长:" + (System.currentTimeMillis() - begin) / 1000.0 + "秒");
            System.out.println("finish the interaction with user " + newUserName);
            System.out.println("-----------------------------------------------------------");

        }


        // client.close();
    }
}