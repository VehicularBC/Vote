package org.example;


import java.util.Date;
import java.text.SimpleDateFormat;


public class config {

    public static String peerHostPort = "192.168.96.7:7054";
    public static String MSPId = "Org1MSP";
    public static String orgNumber = "org1.example.com";

    public static String adminName = "admin";
    public static String localUserName = "gibbon_1";
    public static String affiliation = "org1.department1";
    public static String channelName = "mychannel";
    public static String contractName = "fabcar";
    public static String contractFuncName = "Vote";

    public static String newUserName = "gibbon_3_07181934";


    public static void getNowDate(String action) {

        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss!!!");
        System.out.println(dateFormat.format(date) + action);
    }
}