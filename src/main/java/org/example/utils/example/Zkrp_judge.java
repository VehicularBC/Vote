package com.example;

import java.io.IOException;
import com.example.java_py_thr;

public class Zkrp_judge {

    public static void main(String[] args) {
        Process proc;
        try {
            String[] args1 = new String[] { "py", "/Users/gibbon/Desktop/fabcar/java/src/main/java/org/example/utils/EC-ZKRP/pythr.py", "80" };
            proc = Runtime.getRuntime().exec(args1);
            java_py_thr thr = new java_py_thr();
            thr.judge(args);
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}