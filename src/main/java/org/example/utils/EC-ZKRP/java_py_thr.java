package com.example;

import java.lang.System;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.InetAddress;
import java.net.Socket;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.InputStream;

public class java_py_thr {
    public static String judeg(String args) {

        String res = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String host = addr.getHostName();

            Socket socket = new Socket(host, 12300);

            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);

            out.print(args);
            out.print("over");

            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String tmp = null;
            StringBuilder sb = new StringBuilder();

            while ((tmp = br.readLine()) != null)
                sb.append(tmp).append('\n');
            // System.out.print(sb);
            socket.close();
            res = sb.toString();
            // System.out.print(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
