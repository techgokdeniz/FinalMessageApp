package com.example.finalmessageapp.Models;

public class MessageData {
    private String userid,messagename,messagedesc;

    public MessageData() {
    }

    public MessageData(String userid, String messagename, String messagedesc) {
        this.userid = userid;
        this.messagename = messagename;
        this.messagedesc = messagedesc;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMessagename() {
        return messagename;
    }

    public void setMessagename(String messagename) {
        this.messagename = messagename;
    }

    public String getMessagedesc() {
        return messagedesc;
    }

    public void setMessagedesc(String messagedesc) {
        this.messagedesc = messagedesc;
    }
}

