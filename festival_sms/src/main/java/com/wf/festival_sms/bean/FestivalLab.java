package com.wf.festival_sms.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/14.
 */

public class FestivalLab {

    private static FestivalLab instance;

    private FestivalLab() {
        mList.add(new Festival(1, "国庆节"));
        mList.add(new Festival(2, "中秋节"));
        mList.add(new Festival(3, "元旦"));
        mList.add(new Festival(4, "春节"));
        mList.add(new Festival(5, "端午节"));
        mList.add(new Festival(6, "七夕"));
        mList.add(new Festival(7, "圣诞节"));
        mList.add(new Festival(8, "儿童节"));
        mMsgList.add(new Msg(1,1,"1"));
        mMsgList.add(new Msg(2,1,"1"));
        mMsgList.add(new Msg(3,2,"1"));
        mMsgList.add(new Msg(4,1,"1"));
        mMsgList.add(new Msg(5,1,"1"));
        mMsgList.add(new Msg(6,1,"1"));
        mMsgList.add(new Msg(7,1,"1"));
        mMsgList.add(new Msg(8,1,"1"));
    }

    private List<Msg> mMsgList = new ArrayList<>();



    public List<Festival> getFestivals() {
        return new ArrayList<>(mList);
    }

    public Festival getFestival(int id) {
        for (Festival festival : mList) {
            if (festival.getId() == id) {
                return festival;
            }

        }
        return null;
    }

    private List<Festival> mList = new ArrayList<>();

    public static FestivalLab getInstance() {
        if (instance == null) {
            synchronized (FestivalLab.class) {
                if (instance == null) {
                    instance = new FestivalLab();
                }
            }
        }
        return instance;
    }

    public List<Msg> getMsgListByid(int id){
        List<Msg> msgs = new ArrayList<>();
        for (Msg msg:mMsgList
             ) {
            if (msg.getFestivalId() == id)
                msgs.add(msg);
        }
        return msgs;
    }

    public Msg getMsgById(int id){
        for (Msg msg:mMsgList) {
            if (msg.getId() == id){
                return msg;
            }
        }
        return null;
    }
}
