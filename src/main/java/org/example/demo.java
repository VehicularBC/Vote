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

public class demo {

    public static void main(String[] args) throws Exception {
        config.getNowDate(RSAUtils.keyMap.get(0));
        config.getNowDate(RSAUtils.keyMap.get(1));

        String msg = "dddddaafaafaaaiojainviaovaovnwaiwadddddaafinviabovaovnwaiwadddddaafaafaaaiojainviabovaovnwaiwa";
        String recvMsgEn = RSAUtils.encrypt(msg, RSAUtils.keyMap.get(0));
        String recvMsgDe = RSAUtils.decrypt(recvMsgEn, RSAUtils.keyMap.get(1));

        config.getNowDate(recvMsgEn);
        config.getNowDate(recvMsgDe);

        config.getNowDate(config.newUserName);
    }
}