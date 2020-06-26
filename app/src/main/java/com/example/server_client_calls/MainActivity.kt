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

class MainActivity : AppCompatActivity() {
    private val PERMISSION_INTERNET_ID = 1
    private val REGEX_VALID_USERNAME = "[a-zA-Z0-9]+"
    private val INVALD_USERNAME_MES =
        "username input ,allowed only letters and digits ,no whitespaces or special characters!!"
    private val USERNAME_SP="username"
    private lateinit var sp: SharedPreferences
    private val SP_NAME = "main"
    private lateinit var usernameTextView: TextView
    private lateinit var insertButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intentPermission()
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        initView()
        initButton()
    }

    private fun initView() {
        usernameTextView = findViewById(R.id.username_TV)
        insertButton = findViewById(R.id.insert_button)
    }

    private fun initButton() {
        insertButton.setOnClickListener(View.OnClickListener {
            if (checkValidUS(usernameTextView.text.toString())) {
                sp.edit().putString(USERNAME_SP,usernameTextView.text.toString()).apply()
                login()
            }
            else{
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
}
