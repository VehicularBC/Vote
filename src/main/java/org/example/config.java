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

    public static String peerHostIp = "192.168.96.7";
    public static String peerHostPort = "7054";  // 默认如此
    public static String MSPId = "Org1MSP";
    public static String orgNumber = "org1.example.com";

    public static String adminName = "admin";
    public static String adminPasswd = "adminpw";
    public static String localUserName = "gibbon_1";
//    public static String localUserName = "user";
    public static String affiliation = "org1.department1";
    public static String pemDir = "org1.example.com/ca/ca.org1.example.com-cert.pem";


    public static String channelName = "mychannel";
    public static String contractName = "fabcar";
    public static String contractFuncName = "Vote";

    public static String newUserName = "test202108061636";

    public static int reputation = 1;
    public static Map<Integer, String> keyMap = new HashMap<Integer, String>();


    public static void getNowDate(String action) {
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss:SSS!!!");
        System.out.println(dateFormat.format(date) + action);
    }
}