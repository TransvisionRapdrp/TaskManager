package com.example.taskmanager.api;

import com.example.taskmanager.model.LoginDetails;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Team;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface RegisterAPI {
    @POST("userLogin")//1
    @FormUrlEncoded
    Call<List<LoginDetails>> getLoginDetails(@Field("mobile") String mobile, @Field("password") String password);

    @POST("StatusFetch")//2
    @FormUrlEncoded
    Call<List<Status>> getStatus(@Field("EMPID") String EMPID, @Field("TEAMID") String TEAMID, @Field("FLAG") String FLAG);

    @GET("TeamNameFetch")//3
    Call<List<Team>> getTeamNames();

    @POST("StatusInsert")//4
    @FormUrlEncoded
    Call<List<Status>> insertStatus(@Field("EMPID") String EMPID,@Field("EMPNAME") String EMPNAME, @Field("TEAMID")
            String TEAMID,@Field("STATUS") String STATUS,@Field("MANAGEMENT") String MANAGEMENT);

    @POST("StatusDelete")//5
    @FormUrlEncoded
    Call<List<Status>> deleteStatus(@Field("STATUSID") String STATUSID, @Field("EMPID") String EMPID);

    @POST("userRegister")//4
    @FormUrlEncoded
    Call<List<Status>> userRegister(@Field("EMPNAME") String EMPNAME, @Field("TEAMID") String TEAMID,@Field("DESIGNATION") String DESIGNATION,
                                    @Field("VERSION") String VERSION, @Field("MOBILE") String MOBILE, @Field("PASSWORD") String PASSWORD);

}