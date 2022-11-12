package com.example.taskmanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Status implements Serializable {
    @SerializedName("EMPNAME")
    private String EMPNAME;
    @SerializedName("EMPID")
    private String EMPID;

    public String getEMPID() {
        return EMPID;
    }

    public String getSTATUSID() {
        return STATUSID;
    }

    @SerializedName("STATUSID")
    private String STATUSID;

    public String getMANAGEMENT() {
        return MANAGEMENT;
    }

    @SerializedName("MANAGEMENT")
    private String MANAGEMENT;

    public String getEMPNAME() {
        return EMPNAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getDATE() {
        return DATE;
    }

    @SerializedName("STATUS")
    private String STATUS;

    public String getTIME() {
        return TIME;
    }

    @SerializedName("TIME")
    private String TIME;

    public String getMessage() {
        return Message;
    }

    @SerializedName("Message")
    private String Message;
    @SerializedName("DATE")
    private String DATE;
}
