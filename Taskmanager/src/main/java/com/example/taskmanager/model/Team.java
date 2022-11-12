package com.example.taskmanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Team implements Serializable {
    @SerializedName("TEAMID")
    private String TEAMID;

    public String getTEAMID() {
        return TEAMID;
    }

    public String getTEAMNAME() {
        return TEAMNAME;
    }

    @SerializedName("TEAMNAME")
    private String TEAMNAME;
}
