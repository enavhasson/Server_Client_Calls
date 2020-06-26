package com.example.server_client_calls;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    //todo basic UserService- template
    class User {
        public String username;
    }

    @POST("/user") //CREATE
    void createUser(@Body User user);

    @GET("/user/{userId}")//READ
    User getUser(@Path("userId") String userId,@Body User user);

    @POST("/user") //CREATE
    void updateUser(@Body User user); //todo

    @DELETE("/user/{userId}")  //DELETE
    void deleteUser(@Path("userId") String userId);
}
