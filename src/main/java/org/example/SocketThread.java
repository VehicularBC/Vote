package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

/**
 * Socket多线程处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 */
public class SocketThread extends Thread {
    private String peerHostPort = "https://192.168.3.48:7054";
    private Socket socket;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    public void createNewUser(String userName) {
        try {

            // Create a CA client for interacting with the CA.
            Properties props = new Properties();
            props.put("pemFile", "src/main/resources/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
            props.put("allowAllHostNames", "true");
            HFCAClient caClient = HFCAClient.createNewInstance(peerHostPort, props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            // Check to see if we've already enrolled the user.
            if (wallet.get(userName) != null) {
                System.out.println("An identity for the user " + userName + " already exists in the wallet");
                return;
            }

            X509Identity adminIdentity = (X509Identity)wallet.get("admin");
            if (adminIdentity == null) {
                System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
                return;
            }
            User admin = new User() {

                @Override
                public String getName() {
                    return "admin";
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
                    return "org1.department1";
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
                    return "Org1MSP";
                }

            };

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(userName);
            registrationRequest.setAffiliation("org1.department1");
            registrationRequest.setEnrollmentID(userName);
            String enrollmentSecret = caClient.register(registrationRequest, admin);
            Enrollment enrollment = caClient.enroll(userName, enrollmentSecret);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put(userName, user);
            System.out.println("Successfully enrolled user" + userName + "and imported it into the wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // 根据输入输出流和客户端连接
        try {
            InputStream inputStream = socket.getInputStream();

            // 得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);// 提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);// 加入缓冲区
            String temp = null;
            String info = "";
            while ((temp = bufferedReader.readLine()) != null) {
                info += temp;
                System.out.println("已接收到客户端连接");
                System.out.println("服务端接收到客户端信息：" + info + ",当前客户端ip为："
                        + socket.getInetAddress().getHostAddress());
            }

            OutputStream outputStream = socket.getOutputStream();// 获取一个输出流，向服务端发送信息
            // 输出
            PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
            printWriter.print("");
            printWriter.flush();

            // 申请身份
            String newName = "admin";
            createNewUser(newName);
            /*发送文件*/
            //读取文件到文件流fis
            FileInputStream fis = new FileInputStream("wallet/" + newName + ".id");

            //读取文件流，写入到输出流os
            int i;
            System.out.print("传输中");
            while ((i = fis.read()) != -1) {
                outputStream.write(i);
                System.out.print(".");
            }
            System.out.println();
            System.out.println("传输完成");

            socket.shutdownOutput();// 关闭输出流

            // 关闭相对应的资源
            bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}