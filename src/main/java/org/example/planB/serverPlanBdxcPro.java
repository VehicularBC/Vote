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
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.text.SimpleDateFormat;

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
import org.example.utils.saveTXT;
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

//?????????
public class serverPlanBdxcPro {
    private static final int MAXRECEIVED = 256 * 16;
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

    // HFCAClient caClient = null;
    public static void RegisterUser(String userName) {// Create a CA client for interacting with the CA.
//        Properties props = new Properties();
//        props.put("pemFile", "src/main/resources/crypto-config/peerOrganizations/" + config.pemDir);
//        props.put("allowAllHostNames", "true");
//        HFCAClient caClient = HFCAClient.createNewInstance("https://" + config.peerHostIp + ":" + config.peerHostPort, props);
//        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
//        caClient.setCryptoSuite(cryptoSuite);

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

        try {
            // Check to see if we've already enrolled the user.
            // if (wallet.get(userName) != null) {
            // System.out.println("An identity for the user " + userName + " already exists
            // in the wallet");
            // return;
            // }

            // Register the user, enroll the user, and import the new identity into the
            // wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(userName);
            registrationRequest.setAffiliation(config.affiliation);
            registrationRequest.setEnrollmentID(userName);

            String enrollmentSecret = caClient.register(registrationRequest, admin);

            // ????????????enrollmentRequest???????????????????????????????????????????????????
            // ?????????????????????????????????hf.Affiliation, hf.EnrollmentID, hf.Type??????????????????
            EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
            enrollmentRequest.addAttrReq("hf.Affiliation"); // default attribute
            enrollmentRequest.addAttrReq("hf.EnrollmentID"); // default attribute

            Enrollment enrollment = caClient.enroll(userName, enrollmentSecret, enrollmentRequest);

            Identity user = Identities.newX509Identity(config.MSPId, enrollment);
            // Identity user = Identity.createIdentity(config.MSPId, enrollment.getCert(),
            // enrollment.getKey());
            wallet.put(userName, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String voteForcar(String carID) throws Exception { // ???????????????????????????????????????????????????byte???????????????????????????????????????????????????????????????
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
            // config.getNowDate(new String());
            e.printStackTrace();
        }
        return new String(voteResult, StandardCharsets.UTF_8);
    }

    private static void InitParam() throws Exception {
        Path networkConfigPath = Paths.get("src", "main", "resources", "crypto-config", "peerOrganizations", orgName,
                orgConnectionName);

        // Create a wallet for managing identities
        Path walletPath = Paths.get("wallet");
        wallet = Wallets.newFileSystemWallet(walletPath);

        cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();

        /*
         * ?????? HFCAclient???
         */
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

        /*
         * ??????SDK builder???gateway???network???contract
         */
        // ?????????????????????????????????
        builder = Gateway.createBuilder();
        builder.identity(wallet, config.localUserName).networkConfig(networkConfigPath).discovery(true);

        // get the network and contract
        gateway = builder.connect();
        network = gateway.getNetwork(config.channelName); // mychannel
        contract = network.getContract(config.contractName); // fabcar
    }

    /*
     * ???????????? ????????????????????????????????????
     */
    public static boolean whiteBox(int input) throws Exception {
        if (input >= config.reputation) {
            return true;
        }
        return false;
    }

    public static String msgReceive(String data) {
        /*
         * ??????????????????????????????????????????json ??????????????????commit???commit?????????????????????
         */

        return "";
    }

    public static void main(String[] args) throws Exception {//?????????????????????????????????????????????????????????????????????????????????
         InitParam(); // ?????????????????????

        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(8888);
      //  DatagramSocket client = new DatagramSocket();

        /* 1. ??????????????? */
        System.out.println("-----------------------------------------------------------");
        config.getNowDate(new String("Server current start......"));
        System.out.println("-----------------------------------------------------------");

        HashMap<String,String> messagemap = new HashMap<String,String>();
        // String recIp = "";
        long begin = System.currentTimeMillis();
        ArrayList<String> recIp = new ArrayList<String>();

        while (true) {

            /* 2. ???????????????????????? */
            server.receive(receive);

            byte[] msgByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());
            String msgString = new String(msgByte,"UTF-8");

            System.out.println("??????????????????" + msgString );
            System.out.println("");





            //   if(msgString.length()>20) System.out.println(msgString.substring(0,14));

          //  System.out.println(msgString);

            //??????????????????UID????????????????????????????????????1
            if(msgString.substring(0,1).equals("1")){
            //    messagemap.put(receive.getAddress().getHostAddress(),msgString.substring(config.newUserName.length()+1,msgString.length()));
                System.out.println("?????????????????????UID");
             //   System.out.println(msgString.substring(config.newUserName.length()+1,msgString.length()));
             //   recIp.add(receive.getAddress().getHostAddress());
                Thread t1 = new ThreadUID(msgString.substring(config.newUserName.length()+1,msgString.length()),receive.getAddress().getHostAddress());
                t1.start();
            //    flag++;
                continue;
            }
            if(messagemap.containsKey(receive.getAddress().getHostAddress())){
                String oldmess = messagemap.get(receive.getAddress().getHostAddress());
                String newmess = oldmess + msgString.substring(config.newUserName.length(),msgString.length());
                if(newmess.length() > 2048){
                    System.out.println(newmess);
                    ThreadZKP t2 = new ThreadZKP(newmess, receive.getAddress().getHostAddress());
                    t2.start();
                    messagemap.remove(receive.getAddress().getHostAddress());
                }else messagemap.put(receive.getAddress().getHostAddress(),newmess);
                continue;
            }
            else {
                messagemap.put(receive.getAddress().getHostAddress(),msgString.substring(config.newUserName.length(),msgString.length()));
             //   recIp.add(receive.getAddress().getHostAddress());
            }

            //  byte[] msgByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength()); // ????????????

            //  message = message + new String(msgByte);
//                System.out.println(msgByte.length);
        }



    }
}




class ThreadUID extends Thread{
    String uids;
    String inetAddr;
    public ThreadUID(String uids,String inetAddr){
        this.uids = uids;
        this.inetAddr = inetAddr;
    }

    public void run(){

        JSONObject json = JSONObject.parseObject(uids);
        System.out.println("??????type3");
        String newUserName = json.getString("userID");
        String uListString = json.getString("uList"); // ??????????????????

        /* ???????????? */
        int dstPort = 0;

        JsonUtils txt = null;

        // System.out.println(uList);
        String[] uListStringList = uListString.split("\"");
        List list = Arrays.asList(uListStringList);
        Set set = new HashSet(list);
        String[] uListStringSet = (String[]) set.toArray(new String[0]);

        int num = 0;
        for (int j = 0; j < uListStringSet.length; j++) {
            if (uListStringSet[j].length() >= 2) {
                num++;
            }
        }
        if (num < config.minUIDNum) {
            return;
        }

        // ????????????
        System.out.println("?????????????????????????????????????????????");
        long beginReg = System.currentTimeMillis();
        serverPlanBdxcPro.RegisterUser(newUserName);
        System.out.println(newUserName);
        System.out.println("?????????????????????:" + (System.currentTimeMillis() - beginReg) / 1000.0 + "???");


        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get("wallet" , newUserName +  ".id")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        txt = new JsonUtils("4", 0, config.localUserName, "", new String[] {}, content);
        dstPort = 10999;
       // System.out.println();
        System.out.println("?????????" + newUserName + "??????????????????");
        String sendMsg = JSON.toJSONString(txt);
        byte[] msg = sendMsg.getBytes();

 //       InetAddress inetAddr = InetAddress.getByName(recIp.get(i));
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, dstPort);
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);

        DatagramSocket client = null;
        try {
            client = new DatagramSocket();
            client.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


class ThreadZKP extends Thread{
    String zkp;
    String inetAddr;

    public ThreadZKP(String zkp, String inetAddr){
        this.zkp = zkp;
        this.inetAddr = inetAddr;
    }

    public void run(){

     //   System.out.println(zkp);

        JSONObject json = JSONObject.parseObject(zkp);
        int type = Integer.parseInt(json.getString("Type"));

        /* ???????????? */
        int dstPort = 0;

        JsonUtils txt = null;

        String newUserName = json.getString("userID");
        String commit = json.getString("commit");

        // ????????????UID
        txt = new JsonUtils("2", 0, config.localUserName, "", new String[] {}, "");
        dstPort = 9999;
        System.out.println("????????????????????????" + newUserName + "??????UID");

        String sendMsg = JSON.toJSONString(txt);
        byte[] msg = sendMsg.getBytes();

        //InetAddress inetAddr = InetAddress.getByName(recIp.get(i));
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, dstPort);
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);

        DatagramSocket client = null;
        try {
            client = new DatagramSocket();
            client.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}



