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

public class blankCar {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) throws Exception {
        // 发送信息到某节点
        Socket socket = new Socket("192.168.1.100", 10002);

        // 2、获取 Socket 流中输入流
        OutputStream out = socket.getOutputStream();
        // 3、使用输出流将指定的数据写出去
        out.write("IP|user".getBytes());

        // 等待节点达成共识返回结果
        // 读取服务端返回的数据，使用 Socket 读取流
        InputStream in = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len = in.read(buf);
        String text = new String(buf, 0, len);

        System.out.println(text);
        if (text == "1") {
            // 如果正确接收文件并解压到指定目录

        } else {
            System.out.println("error");
        }
        socket.close();  // 4、关闭 Socket 服务
    }

}
