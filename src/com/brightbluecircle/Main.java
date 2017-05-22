package com.brightbluecircle;

import com.brightbluecircle.Subscription.StuttSub;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) {

        // Local time
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date d = new Date();
        System.out.println(df.format(d));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

        StuttSub subCtrl = new StuttSub();

        subCtrl.testMessge();
        try {
            subCtrl.pullMessages();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());


        }

    }
}
