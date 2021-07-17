
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
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;
import java.nio.file.Files;
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

//服务端
public class serverUDP {
    private static final int MAXRECEIVED = 255;
    private static String peerHostPort = "https://192.168.96.7:7054";
    private static String localName = "gibbon_1";
    private Socket socket;
    public static void getNowDate(String action) {
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss!!!");
        System.out.println(dateFormat.format(date) + action);
    }

    public static void createNewUser(String userName) {
        try {
            // Create a CA client for interacting with the CA.
            Properties props = new Properties();
            props.put("pemFile", "src/main/resources/crypto-config/" +
                    "peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
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

    public static String voteForcar(String carID) { //为新车进行投票，调用链码，返回一个byte类数组，第一个元素为链码返回结果（待测试）
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

            builder.identity(wallet, localName).networkConfig(networkConfigPath).discovery(true);

            // create a gateway connection
            try (Gateway gateway = builder.connect()) {
                // get the network and contract
                Network network = gateway.getNetwork("mychannel");
                Contract contract = network.getContract("fabcar");

                getNowDate(new String("3.1.1.waiting for voting"));
                voteResult = contract.createTransaction("Vote").submit(carID);
//                System.out.println(new String(voteResult, StandardCharsets.UTF_8));
                getNowDate(new String("3.1.2.receiving for voting"));

            } catch (ContractException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String aaa = new String(voteResult, StandardCharsets.UTF_8);
//        System.out.println(aaa);
        return aaa;
    }

    public static void main(String[] args) throws IOException {
        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(8888);

        System.out.println("---------------------------------");
        getNowDate(new String("Server current start......"));
        System.out.println("---------------------------------");

        while (true) {
            server.receive(receive);

            byte[] recvByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());
            String userName = new String(recvByte);
            String recIp = receive.getAddress().getHostAddress();
//            System.out.println("Server receive msg:" + new String(recvByte));
            getNowDate(new String("1.Server receive msg from user " + new String(recvByte)));

            try {
                // 收到新车请求，由该车负责调用其他车辆链码
                getNowDate(new String("2.1.invoking chaincode for " + userName));
                // 由链码返回1的认证车进行发送数据
                String voteresult = voteForcar(userName);
                getNowDate(new String("2.2.received chaincode response " + voteresult + " for " + userName));

                if (voteresult.equals("true")) {
                    getNowDate(new String("3.1.beginging create identify for " + userName));
                    createNewUser(userName);
                    getNowDate(new String("3.2.finishing creating identify for " + userName));


                    getNowDate(new String("4.1.beginging to send identify for " + userName));
                    String content = new String(Files.readAllBytes(Paths.get("wallet/" + userName + ".id")));//原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/java/java-read-file-to-string.html
                    // 获取数据，发送到白板车
                    byte[] msg = content.getBytes();
                    DatagramSocket client = new DatagramSocket();
                    InetAddress inetAddr = InetAddress.getByName(recIp);
                    SocketAddress socketAddr = new InetSocketAddress(inetAddr, 9999);
                    DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);
                    client.send(sendPacket);
                    client.close();
                    getNowDate(new String("4.2.finished sending identify for " + userName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}