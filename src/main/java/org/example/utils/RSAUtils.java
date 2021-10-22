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

/**
 * Java RSA 加密工具类
 *
 */
public class RSAUtils {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 512;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    public static Map<Integer, String> keyMap = new HashMap<Integer, String>();

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map

//        keyMap.put(0, publicKeyString);  // 0表示公钥
//        keyMap.put(1, privateKeyString);  // 1表示私钥

        keyMap.put(0, new String("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJCRtc+keRmLve9Jjq1vNMgOD15Ya0wxhRc4cPaqSEmjzCBg5+3XU/j4xuwtF/aLCaG8LwmQaTWEcVtGORA0dp8CAwEAAQ=="));
        keyMap.put(1, new String("MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAkJG1z6R5GYu970mOrW80yA4PXlhrTDGFFzhw9qpISaPMIGDn7ddT+PjG7C0X9osJobwvCZBpNYRxW0Y5EDR2nwIDAQABAkAJNyb+LXkEUw9KCZS57+gCOKM4jschyQy3n8/TnKP7zaETCVUcvrsIhsBi3a1vKRH53NqVY49DppHR26UoawXBAiEA5IPhw6KzN3JKcSP02o8ZXUMTSjoX/TBbxSu6QszXA3MCIQCh9RTZmgd+0H+0/AJLt3GaiqgIdcX8ZLDqnYOwNHNtJQIgC5nCWUsmK/dqXgoEQSAomnpwPUFrvFe7IOxSXVfGxo8CIEWM6hdIfk+HWlBuqM27SZ4ETYTUjuGEnDUkz5ir7aXBAiEA4mLkQDLDch/2PtcbJr5IPur2esTTopUdoXusFDbTRDU="));
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * 解码PublicKey
     * @param key
     * @return
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解码PrivateKey
     * @param key
     * @return
     */
    public static PrivateKey  getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec x509EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}