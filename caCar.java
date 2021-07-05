/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class caCar {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) throws Exception {
        // 1、创建客户端对象
        ServerSocket ss = new ServerSocket(10002);

        // 2、获取连接过来的客户端对象
        Socket s = ss.accept();

        String ip = s.getInetAddress().getHostAddress();

        // 3、通过 Socket 对象获取输入流，读取客户端发来的数据
        InputStream in = s.getInputStream();

        byte[] buf = new byte[1024];

        int len = in.read(buf);
        String text = new String(buf, 0, len);
        System.out.println(ip + ":" + text);


        // 调用链码

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "user").networkConfig(networkConfigPath).discovery(true);
        // create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("fabcar");

            byte[] result;

            result = contract.evaluateTransaction("queryAllCars");
            System.out.println(new String(result));

            result = contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");
            if (result == 1) {
                try {
                    // register user "username"
                    String userName = "cur";
                    // Create a CA client for interacting with the CA.
                    Properties props = new Properties();
                    props.put("pemFile", "src/main/resources/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
                    props.put("allowAllHostNames", "true");
                    HFCAClient caClient = HFCAClient.createNewInstance("https://192.168.3.48:7054", props);
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
                    System.out.println("Successfully enrolled user " + userName + " and imported it into the wallet");

                    // 传输文件
                    String[] cmd = {"scp -rp ..."};
                    Process process = Runtime.getRuntime().exec(cmd);
                    InputStream in;
                    BufferedReader br;
                    in = process.getInputStream();
                    br = new BufferedReader(new InputStreamReader(in));
                    while (in.read() != -1) {
                        result = br.readLine();
                        log.info("job result [" + result + "]");
                    }
                    br.close();
                    in.close();
                    //process.waitFor();
                    process.destroy();
                } catch (Throwable e) {
                    log.error("执行linux命令出错:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
