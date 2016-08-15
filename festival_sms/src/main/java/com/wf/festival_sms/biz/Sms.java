package com.wf.festival_sms.biz;

import android.app.PendingIntent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by lenovo on 2016/8/14.
 */

public class Sms {

    public int sendMsg(String number, String msg, PendingIntent sendIntent, PendingIntent deliverIntent) {
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> contents = manager.divideMessage(msg);

        for (String content : contents) {
            manager.sendTextMessage(number, null, content, sendIntent, deliverIntent);
        }
        return contents.size();
    }

    public int sendMsg(Set<String> numbers, String msg, PendingIntent sendIntent, PendingIntent deliverIntent) {
        int result = 0;
        for (String number : numbers) {
            int count = sendMsg(number, msg, sendIntent, deliverIntent);
            result += count;
        }
        return result;
    }
}
