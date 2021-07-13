/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Timer;
import java.util.TimerTask;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class FabricDemo {
    private static final Path NETWORK_CONFIG_PATH = Paths.get("src", "main", "resources", "connection.json");
    private static final Path credentialPath = Paths.get("src", "main","resources", "crypto-config",
            "peerOrganizations", "org1.example.com", "users", "User1@org1.example.com", "msp");
//    private static final Path NETWORK_CONFIG_PATH = Paths.get("connection.json");
//    private static final Path credentialPath = Paths.get("crypto-config",
//            "peerOrganizations", "org1.example.com", "users", "User1@org1.example.com", "msp");

    private static long dateMethod(String lastReceiveTime) {
//        System.out.println("最后时间" + lastReceiveTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = df.parse(currTime());
            Date date2 = df.parse(lastReceiveTime);
            long diff = date1.getTime() - date2.getTime();
//            System.out.println("毫秒数：" + diff);
            //计算两个时间之间差了多少分钟
            long minutes = diff / (1000 * 60);
            return minutes;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    private static String currTime() throws Exception{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currTime = df.format(date);
//        System.out.println("当前时间" + currTime);
        return currTime;
    }

    public static void main(String[] args) throws Exception {
        String userName = "appUserGibbon202105172005";
        String channelName = "mychannel";
        String contractName = "fabcar";

        try {
            //使用agridepart中的user1初始化一个网关wallet账户用于连接网络
            Path walletPath = Paths.get("wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);

            Path certificatePath = credentialPath.resolve(Paths.get("signcerts", "cert.pem"));
            X509Certificate certificate = readX509Certificate(certificatePath);

            Path privateKeyPath = credentialPath.resolve(
                    Paths.get("keystore", "5643b146538dd76ae79331ac932ec66be0c11c16107886edf97dece95c277950_sk"));
            PrivateKey privateKey = getPrivateKey(privateKeyPath);

            if (wallet.get(userName) != null) {
                System.out.println("An identity for the user \"" + userName + "\" already exists in the wallet");
            } else {
                wallet.put(userName, Identities.newX509Identity("Org1MSP", certificate, privateKey));
                System.out.println("Successfully enrolled user \"" + userName + "\" and imported it into the wallet");
            }

            //根据connection.json 获取Fabric网络连接对象
            GatewayImpl.Builder builder = (GatewayImpl.Builder) Gateway.createBuilder();


            //Path networkConfigPath = Paths.get("..", "..", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
            builder.identity(wallet, userName).networkConfig(NETWORK_CONFIG_PATH).discovery(true);

            //连接网关
            Gateway gateway = builder.connect();

            // get the network and contract
            Network network = gateway.getNetwork(channelName);
            Contract contract = network.getContract(contractName);

            byte[] result;

            result = contract.evaluateTransaction("queryAllCars");
            System.out.println(new String(result));

            contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");

            result = contract.evaluateTransaction("queryCar", "CAR10");
            System.out.println(new String(result));

            contract.submitTransaction("changeCarOwner", "CAR10", "Archie");

            result = contract.evaluateTransaction("queryCar", "CAR10");
            System.out.println(new String(result));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }

}
