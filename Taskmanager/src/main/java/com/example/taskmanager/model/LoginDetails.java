package com.example.taskmanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginDetails implements Serializable {
    @SerializedName("EMPID")
    private String EMPID;
    @SerializedName("EMPNAME")
    private String EMPNAME;
    @SerializedName("DESIGNATION")
    private String DESIGNATION;
    @SerializedName("TEAMID")
    private String TEAMID;
    @SerializedName("MOBILE")
    private String MOBILE;
    @SerializedName("EMAIL")
    private String EMAIL;

    public String getVERSION() {
        return VERSION;
    }

    @SerializedName("VERSION")
    private String VERSION;

    public String getEMPID() {
        return EMPID;
    }

    public String getEMPNAME() {
        return EMPNAME;
    }

    public String getDESIGNATION() {
        return DESIGNATION;
    }

    public String getTEAMID() {
        return TEAMID;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getROLE() {
        return ROLE;
    }

    @SerializedName("ROLE")
    private String ROLE;
}
