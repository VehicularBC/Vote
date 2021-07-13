/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class blankCar {
    private int port = 8888;

    public static int main(String[] args) throws Exception {
        String host = "192.168.3.6";
//        String host = "192.168.3.101";
        String userName = "gibbon1344";

        try {
            //创建Socket对象
            Socket socket=new Socket(host, 8888);

            //根据输入输出流和服务端连接
            OutputStream outputStream=socket.getOutputStream();//获取一个输出流，向服务端发送信息

            PrintWriter printWriter=new PrintWriter(outputStream);//将输出流包装成打印流
            printWriter.print("服务端你好，我是" + userName);
            printWriter.flush();
            socket.shutdownOutput();//关闭输出流

            InputStream inputStream=socket.getInputStream();//获取一个输入流，接收服务端的信息
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"utf-8");//包装成字符流，提高效率
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//缓冲区
            String info="";
            String temp=null;//临时变量
//            while((temp=bufferedReader.readLine())!=null){
//                info+=temp;
//                System.out.println("客户端接收服务端发送信息："+info);
//            }
            //
            //将数据写入文件
            File f = new File(userName + ".id");
            OutputStream fos = new FileOutputStream(f);
            int i;
            System.out.print("接收中");
            while ((i=inputStream.read())!=-1) {
                fos.write(i);
                System.out.print(".");
            }
            //刷新输出流fos
            fos.flush();

            //关闭相对应的资源
            bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;  // 身份合理返回1，不合理返回0
    }
}
