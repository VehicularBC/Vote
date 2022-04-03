package org.example;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Base64;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class config {

    public static String peerHostIp = "192.168.2.156";
    public static String peerHostPort = "7054";  // 默认如此
    public static String MSPId = "Org1MSP";
    public static String orgNumber = "org1.example.com";

    public static String adminName = "admin";
    public static String adminPasswd = "adminpw";
    public static String localUserName = new String("o" + String.valueOf(System.currentTimeMillis()));
    public static String affiliation = "org1.department1";
    public static String pemDir = "org1.example.com/ca/ca.org1.example.com-cert.pem";


    public static String channelName = "mychannel";
    public static String contractName = "fabcar";
    public static String contractFuncName = "Vote";

    public static String newUserName = new String("n" + String.valueOf(System.currentTimeMillis()));
//    public static String newUserName = "n1635493009281";


    public static int port1 = 8888;  // 一阶段广播地址
    public static int port2 = 9999;  // 二阶段发
    public static int port3 = 10888;

    public static int reputation = 0;  // 自身信誉值
    public static int minUIDNum = 1;
    public static boolean encrypt = true;

    public static boolean muteAll = false;

    // plan C
    public static String token = null;
    public static int tokenhash = 0;
    public static long requestTime = 0;


    public static void getNowDate(String action) {
        if (muteAll) {
            return;
        }
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss:SSS!!!");
        System.out.println(dateFormat.format(date) + action);
        return;
    }
}
