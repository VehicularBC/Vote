package org.example.utils;

import java.io.FileWriter;
import java.io.IOException;

public class saveTXT {
    private static String filepath = "/home/hbx/Desktop/Vote/src/main/java/org/example/utils/EC-ZKRP/ZKRP.txt";

    public static void saveAstxt(String content) {
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filepath);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
