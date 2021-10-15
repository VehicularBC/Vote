package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class java_py_test {

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        Process proc;
        try {
            long t1 = System.currentTimeMillis();
            String[] args1 = new String[] { "py", "Users/gibbon/Desktop/fabcar/java/src/main/java/org/example/utils/EC-ZKRP/ZKRP_verify.py", "80" };
            proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            long t2 = System.currentTimeMillis();
            line = in.readLine();
            System.out.println(line);
            long end = System.currentTimeMillis();
            in.close();
            proc.waitFor();
            System.out.println("总时延为" + (end - begin) + "ms");
            System.out.println("调用时间为" + (t2 - t1) + "ms");
            System.out.println("读入执行结果时间为" + (end - t2) + "ms");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}