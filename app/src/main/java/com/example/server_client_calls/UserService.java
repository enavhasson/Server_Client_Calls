package com.example.server_client_calls;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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

    class SetUserPrettyNameRequest{
        public @Nullable String pretty_name;
    }

    class SetUserImgUrlRequest{
        public @Nullable String image_url;
    }

    @GET("/users/{username}/token/") //READ
    Call<TokenResponse> getUserToken(@Path("username") String username);

    @GET("/user/") //READ
    Call<UserResponse> getUserRes(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("/user/edit/")
    Call<UserResponse> updateUserPretty(@Header("Authorization") String token , @Body SetUserPrettyNameRequest request);

    @Headers("Content-Type: application/json")
    @POST("/user/edit/")
    Call<UserResponse> updateUserImageUrl(@Header("Authorization") String token , @Body SetUserImgUrlRequest request);

    @POST("/user/edit/") //CREATE
    Call<TokenResponse> createUser(@Body User user);

    @GET("/user/{userId}")//READ
    User getUser(@Path("username") String username,@Body User user);

    @POST("/user") //CREATE
    void updateUser(@Body User user); //todo

    @DELETE("/user/{userId}")  //DELETE
    void deleteUser(@Path("userId") String userId);
}
