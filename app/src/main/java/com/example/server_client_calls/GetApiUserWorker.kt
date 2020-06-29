package com.example.server_client_calls
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson

class GetApiUserWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val callToSlash = ServerHolder.getInstance()
            .server
            .getUserToken(inputData.getString("username"))

        val response = callToSlash?.execute()
        if (response != null) {
            Log.d("GetApiUserWorker", "got response: ${ response.code() }")
            if (response.code() != 200 || !response.isSuccessful) { //todo check response.code() != 200
                return Result.failure()
            }
        }

        val tokenResult: UserService.TokenResponse = response?.body() ?: return Result.failure()

        Log.d("GetApiUserWorker", "got response with result: ${ tokenResult.data }")
        val token = Gson().toJson(tokenResult)
        return Result.success(Data.Builder().putString(TOKEN_KEY,token).build())
    }
}
