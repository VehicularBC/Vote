/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

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
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class caCar {
    private String host = "localhost";
    private int port = 8888;
    private static String fileName="";

    public static void main(String[] args) throws Exception {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("服务端已启动，等待客户端连接..");

            while (true) {
                Socket socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                SocketThread socketThread = new SocketThread(socket);
                socketThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
