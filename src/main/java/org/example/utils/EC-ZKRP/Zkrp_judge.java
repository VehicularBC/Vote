package org.example;

import java.io.IOException;

public class Zkrp_judge {

    public static String Repjudge(String args) {
        Process proc;
        String judgeres = null;
        try {
            String[] args1 = new String[] { "python", "/Users/gibbon/Desktop/fabcar/java/src/main/java/org/example/utils/EC-ZKRP/pythr.py" };
            proc = Runtime.getRuntime().exec(args1);
            java_py_thr thr = new java_py_thr();
            judgeres = thr.judeg(args);
            proc.waitFor();
            // System.out.println(judgeres);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return judgeres;
    }
}