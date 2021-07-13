package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.hyperledger.fabric.gateway.ContractException;

/**
 * Socket多线程处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 */
public class SocketThread extends Thread {
    private String peerHostPort = "https://192.168.2.200:7054";
    private Socket socket;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    public void createNewUser(String userName) {
        try {

            // Create a CA client for interacting with the CA.
            Properties props = new Properties();
            props.put("pemFile",
                    "src/main/resources/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
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

            X509Identity adminIdentity = (X509Identity) wallet.get("admin");
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

            // Register the user, enroll the user, and import the new identity into the
            // wallet.
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

    public byte[] voteForcar(String carID) { //为新车进行投票，调用链码，返回一个byte类数组，第一个元素为链码返回结果（待测试）
        byte[] voteResult = null;
        try {
            // invoke the voting chaincode
            // Load a file system based wallet for managing identities.
            Path walletPath = Paths.get("wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);
            // load a CCP
            Path networkConfigPath = Paths.get("src", "main", "resources", "crypto-config", "peerOrganizations",
                    "org1.example.com", "connection-org1.yaml");

            Gateway.Builder builder = Gateway.createBuilder();

            builder.identity(wallet, "user").networkConfig(networkConfigPath).discovery(true);

            // create a gateway connection
            try (Gateway gateway = builder.connect()) {
                // get the network and contract
                Network network = gateway.getNetwork("mychannel");
                Contract contract = network.getContract("Vote");

                voteResult = contract.createTransaction("Vote").submit(carID);
                System.out.println(new String(voteResult, StandardCharsets.UTF_8));

            } catch (ContractException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voteResult;
    }

    public void run() {
        // 根据输入输出流和客户端连接
        try {
            InputStream inputStream = socket.getInputStream();

            // 得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);// 提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);// 加入缓冲区
            String temp = null;
            String info = "";
            while ((temp = bufferedReader.readLine()) != null) {
                info += temp;
                System.out.println("已接收到客户端连接");
                System.out.println("服务端接收到客户端信息：" + info + ",当前客户端ip为：" + socket.getInetAddress().getHostAddress());
            }
            OutputStream outputStream = socket.getOutputStream();// 获取一个输出流，向服务端发送信息
            // 输出
            PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
//            printWriter.print("test1");
            printWriter.flush();

            String userName = info.substring(info.length() - 8)
            byte voteresult = voteForcar(userName)[0];

            if (voteresult == 1) {
                System.out.println("你需要为新车服务");
                // 申请身份
                String newName = userName;
                createNewUser(newName);
                /* 发送文件 */
                // 读取文件到文件流fis
                FileInputStream fis = new FileInputStream("wallet/" + newName + ".id");

                // 读取文件流，写入到输出流os
                int i;
                System.out.print("传输中");
                while ((i = fis.read()) != -1) {
                    outputStream.write(i);
                    System.out.print(".");
                }
                System.out.println();
                System.out.println("传输完成");

                socket.shutdownOutput();// 关闭输出流
            } else {
                System.out.println("你不需要为新车服务");
            }
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