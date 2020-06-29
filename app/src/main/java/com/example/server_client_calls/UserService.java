package com.example.server_client_calls;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    //todo basic UserService- template
    class User {
        public @Nullable  String username;
        public @Nullable String pretty_name;
        public @Nullable String image_url;
    }
    class TokenResponse{
        public String data;
    }

    class UserResponse{
        public User data;
    }

    @GET("/users/{username}/token/") //READ
    Call<TokenResponse> getUserToken(@Path("username") String username);

    @GET("/user/") //READ
    Call<UserResponse> getUserRes(@Header("Authorization") String token);

    @POST("/user") //CREATE
    Call<TokenResponse> createUser(@Body User user);

    @GET("/user/{userId}")//READ
    User getUser(@Path("username") String username,@Body User user);

    @POST("/user") //CREATE
    void updateUser(@Body User user); //todo

    @DELETE("/user/{userId}")  //DELETE
    void deleteUser(@Path("userId") String userId);
}
