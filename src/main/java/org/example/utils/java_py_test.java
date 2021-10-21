package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class java_py_test {

    public static String judge_one(String arg) {
        Process proc;
        String res = null;
        try {
            String[] args1 = new String[] { "/usr/bin/python3",
                    "/home/hbx/Desktop/Vote/src/main/java/org/example/utils/EC-ZKRP/ZKRP_verify.py", arg };
            proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            res = in.readLine();
            in.close();
            proc.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String produce(String rep) {
        Process proc2;
        String res2 = null;
        try {
            String[] args2 = new String[] { "/usr/bin/python3",
                    "/home/hbx/Desktop/Vote/src/main/java/org/example/utils/EC-ZKRP/ZKRP_produce.py", rep };
            proc2 = Runtime.getRuntime().exec(args2);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(proc2.getInputStream()));
            String line2 = null;
            res2 = in2.readLine();
            in2.close();
            proc2.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res2;
    }
}