package com.example.server_client_calls

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequest

import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.*
import com.google.gson.Gson
import java.util.*

const val TOKEN_KEY = "token"
const val USERNAME_KEY = "username"

class MainActivity : AppCompatActivity() {
    private val PERMISSION_INTERNET_ID = 1
    private val REGEX_VALID_USERNAME = "[a-zA-Z0-9]+"
    private val INVALD_USERNAME_MES =
        "username input ,allowed only letters and digits ,no whitespaces or special characters!!"
    private val USERNAME_SP = "username"
    private lateinit var sp: SharedPreferences
    private var token: String? = null

    private val SP_NAME = "main"
    private lateinit var usernameTextView: TextView
    private lateinit var insertButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intentPermission()
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        token = sp.getString(TOKEN_KEY, null)
        initView()
        initButton()
    }

    private fun initView() {
        usernameTextView = findViewById(R.id.username_TV)
        insertButton = findViewById(R.id.insert_button)
    }

    private fun initButton() {
        insertButton.setOnClickListener(View.OnClickListener {
            val username=usernameTextView.text.toString()
            if (checkValidUS(username)) {
                sp.edit().putString(USERNAME_SP,username).apply()
                getUserToken(username)
            } else {
                Toast.makeText(
                    applicationContext,
                    INVALD_USERNAME_MES,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun login() {

    }

    private fun checkValidUS(username: String): Boolean {
        return username.matches(Regex(REGEX_VALID_USERNAME))
    }

    private fun intentPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                PERMISSION_INTERNET_ID
            )
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_INTERNET_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                TODO()
            } else {
                // the user has denied our request! =-O
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.INTERNET
                    )
                ) {
                    // reached here? means we asked the user for this permission more than once,
                    // and they still refuse. This would be a good time to open up a dialog
                    // explaining why we need this permission
                    Toast.makeText(
                        applicationContext,
                        "App can't operate without INTERNET permission", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun saveToken(token: String) {
        this.token = token
        sp.edit().putString(TOKEN_KEY, token).apply()
    }


    private fun getUserToken(username: String) {
        //todo loading view
        val workerRequest = OneTimeWorkRequest.Builder(GetApiUserWorker::class.java)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(Data.Builder().putString(USERNAME_KEY, username).build())
            .build()

        WorkManager.getInstance(this).enqueue(workerRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo == null) return@Observer

                Log.d("ClientServerActivity", "worker has reached state ${workInfo.state}")

                if (workInfo.state == WorkInfo.State.FAILED) {
                    // TODO: update UI for failure?
                    return@Observer
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // TODO: update UI for success! :)
                    val tokenJson = workInfo.outputData.getString(TOKEN_KEY)
                    Log.d("getUserToken_liveData", "user : $username,token : $tokenJson")
                    val data_tokenRes =
                        Gson().fromJson(tokenJson, UserService.TokenResponse::class.java).data
                    this.saveToken(data_tokenRes)
                    //todo upsate UI
                    return@Observer
                } else {
                    // not interesting, wait until the worker will reach the state we want
                    // TODO update ui for "loading..." ?
                    return@Observer
                }
            })
    }

}
}
