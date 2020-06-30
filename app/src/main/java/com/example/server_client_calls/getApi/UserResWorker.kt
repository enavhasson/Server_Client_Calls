package com.example.server_client_calls.getApi

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.server_client_calls.ServerHolder
import com.example.server_client_calls.TOKEN_KEY
import com.example.server_client_calls.USER_INFO_KEY
import com.example.server_client_calls.UserService
import com.google.gson.Gson

class UserResWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val input = "token " + inputData.getString(TOKEN_KEY)
        val callToSlash = ServerHolder.getInstance()
            .server
            .getUserRes(input)

        val response = callToSlash?.execute()
        if (response != null) {
            Log.d("GetApiUserResWorker", "got response: ${response.code()}")
            if (response.code() != 200 || !response.isSuccessful) {
                return Result.failure()
            }
        }
        val userResult: UserService.UserResponse = response?.body() ?: return Result.failure()

        Log.d("GetApiUserResWorker", "got response with result: ${userResult.data}")
        val userInfo = Gson().toJson(userResult)
        return Result.success(Data.Builder().putString(USER_INFO_KEY, userInfo).build())
    }
}

