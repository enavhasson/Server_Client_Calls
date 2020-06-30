package com.example.server_client_calls

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

const val TOKEN_KEY = "token"
const val USERNAME_KEY = "username"
const val PRETTY_NAME_KEY = "pretty_name"
const val IMG_URL_KEY = "image_url"
const val IS_PRETTY_KEY = "true"

const val USER_INFO_KEY = "user_info"

class MainActivity : AppCompatActivity() {
    private val PERMISSION_INTERNET_ID = 1
    private val REGEX_VALID_USERNAME = "[a-zA-Z0-9]+"
    private val INVALD_USERNAME_MES =
        "username input ,allowed only letters and digits ,no whitespaces or special characters!!"
    private val PRETTY_NAME_STR = "welcome again, "
    private val PRETTY_NAME_STR_NULL = "welcome "
    private val USERNAME_SP = "username"

    private val CRAB_IMG_URL = "/images/crab.png"
    private val UNICORN_IMG_URL = "/images/unicorn.png"
    private val ALIEN_IMG_URL = "/images/alien.png"
    private val ROBOT_IMG_URL = "/images/robot.png"
    private val OCTOPUS_IMG_URL = "/images/octopus.png"
    private val FROG_IMG_URL = "/images/frog.png"
    private val IMAGES = arrayOf("/images/crab.png", "/images/unicorn.png", "/images/alien.png",
        "/images/robot.png", "/images/octopus.png", "/images/frog.png")

    private lateinit var sp: SharedPreferences
    private var token: String? = null

    private val SP_NAME = "main"
    private lateinit var usernameTextView: EditText
    private lateinit var loadingTextView: TextView
    private lateinit var prettyNameTextView: TextView
    private lateinit var insertButton: Button
    private lateinit var insertPrettyButton: Button
    private lateinit var profileImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intentPermission()
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        token = sp.getString(TOKEN_KEY, null)
        init()
        initButton()
        if (token != null) {
            initImages()
            removeInsertUsernameView()
            getUserInfo(this.token!!)
        }
    }

    private fun init() {
        usernameTextView = findViewById(R.id.username_TV)

        loadingTextView = findViewById(R.id.loadingTextView)
        loadingTextView.visibility = View.INVISIBLE

        prettyNameTextView = findViewById(R.id.prettyNameTV)
        prettyNameTextView.visibility = View.INVISIBLE

        insertButton = findViewById(R.id.insert_button)
        insertPrettyButton = findViewById(R.id.insertPrettyNameButton)

        profileImg = findViewById(R.id.profileImageView)
        profileImg.visibility = View.INVISIBLE
    }

    private fun initView() {
        loadingTextView.visibility = View.INVISIBLE
        prettyNameTextView.visibility = View.INVISIBLE
    }

    fun showImagesUI(){
        showImgUI(ServerHolder.BASE_URL+ALIEN_IMG_URL,findViewById(R.id.alien_image))
        showImgUI(ServerHolder.BASE_URL+CRAB_IMG_URL,findViewById(R.id.crab_image))
        showImgUI(ServerHolder.BASE_URL+FROG_IMG_URL,findViewById(R.id.frog_image))
        showImgUI(ServerHolder.BASE_URL+OCTOPUS_IMG_URL,findViewById(R.id.octopus_image))
        showImgUI(ServerHolder.BASE_URL+ROBOT_IMG_URL,findViewById(R.id.robot_image))
        showImgUI(ServerHolder.BASE_URL+UNICORN_IMG_URL,findViewById(R.id.unicorn_image))
    }

    private fun initImages() {
        showImagesUI()
        findViewById<View>(R.id.alien_image).setOnClickListener { view: View? ->
            updateUserImgUrl(ALIEN_IMG_URL)
        }
        findViewById<View>(R.id.crab_image).setOnClickListener { view: View? ->
            updateUserImgUrl(CRAB_IMG_URL)
        }
        findViewById<View>(R.id.frog_image).setOnClickListener { view: View? ->
            updateUserImgUrl(FROG_IMG_URL)
        }
        findViewById<View>(R.id.octopus_image).setOnClickListener { view: View? ->
            updateUserImgUrl(OCTOPUS_IMG_URL)
        }
        findViewById<View>(R.id.robot_image).setOnClickListener { view: View? ->
            updateUserImgUrl(ROBOT_IMG_URL)
        }
        findViewById<View>(R.id.unicorn_image).setOnClickListener { view: View? ->
            updateUserImgUrl(UNICORN_IMG_URL)
        }
    }


    private fun removeInsertUsernameView() {
        insertButton.visibility = View.INVISIBLE
        usernameTextView.visibility = View.INVISIBLE
        prettyNameTextView.visibility = View.VISIBLE
        insertPrettyButton.visibility = View.VISIBLE
    }

    private fun initButton() {
        insertButton.setOnClickListener(View.OnClickListener {
            val username = usernameTextView.text.toString()
            if (checkValidUS(username)) {
                getUserToken(username)
                initImages()
            } else {
                Toast.makeText(
                    applicationContext,
                    INVALD_USERNAME_MES,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        insertPrettyButton.setOnClickListener(View.OnClickListener {
            showAddPrettyNameDialog(this)
        })
        insertPrettyButton.visibility = View.INVISIBLE
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
            if (!grantResults.isNotEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
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
                    return@Observer
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // update UI for success! :)
                    val tokenJson = workInfo.outputData.getString(TOKEN_KEY)
                    Log.d("getUserToken_liveData", "user : $username,token : $tokenJson")
                    val data_tokenRes =
                        Gson().fromJson(tokenJson, UserService.TokenResponse::class.java).data
                    this.saveToken(data_tokenRes)
                    removeInsertUsernameView()
                    getUpdateUserPrettyName("")
                    return@Observer
                } else {
                    // not interesting, wait until the worker will reach the state we want
                    return@Observer
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun updatePrettyNameUI(user: UserService.User) {
        if (user.pretty_name.isNullOrEmpty()) {
            prettyNameTextView.text = PRETTY_NAME_STR_NULL + user.username
        } else {
            prettyNameTextView.text = PRETTY_NAME_STR + user.pretty_name
        }
        loadingTextView.visibility = View.INVISIBLE
    }


    fun updateUI(user: UserService.User) {
        updateImgUrl(user.image_url)
        updatePrettyNameUI(user)
        loadingTextView.visibility = View.INVISIBLE
        prettyNameTextView.visibility = View.VISIBLE
        profileImg.visibility = View.VISIBLE

    }

    private fun showImgUI(img_url: String, intoImage: ImageView) {
        Glide.with(this).load(img_url).into(intoImage)
    }

    fun updateImgUrl(img_url: String?) {
        if (img_url != null) {
            showImgUI(ServerHolder.BASE_URL + img_url, profileImg)
        }
    }

    private fun getUserInfo(token: String) {
        val workerRequest = OneTimeWorkRequest.Builder(GetApiUserResWorker::class.java)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType
                    (NetworkType.CONNECTED).build()
            )
            .setInputData(Data.Builder().putString(TOKEN_KEY, token).build()).build()

        WorkManager.getInstance(this).enqueue(workerRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo == null) {
                    return@Observer
                }

                Log.d("ClientServerActivity", "worker has reached state ${workInfo.state}")

                if (workInfo.state == WorkInfo.State.FAILED) {
                    return@Observer
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // update UI for success! :)
                    val userInfoJson = workInfo.outputData.getString(USER_INFO_KEY)
                    Log.d("getUserInfo_liveData", "userInfo : $userInfoJson")
                    val dataUserInfo =
                        Gson().fromJson(userInfoJson, UserService.UserResponse::class.java).data
                    updateUI(dataUserInfo)

                    return@Observer
                } else {
                    // not interesting, wait until the worker will reach the state we want
                    loadingTextView.visibility = View.VISIBLE
                    return@Observer
                }
            })
    }

    private fun getUpdateUserPrettyName(newPrettyName: String) {
        val workerRequest = OneTimeWorkRequest.Builder(GetApiUserUpdatePrettyWorker::class.java)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType
                    (NetworkType.CONNECTED).build()
            )
            .setInputData(
                Data.Builder().putString(TOKEN_KEY, token).putString(PRETTY_NAME_KEY, newPrettyName)
                    .build()
            ).build()

        WorkManager.getInstance(this).enqueue(workerRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo == null) return@Observer
                Log.d("ClientServerActivity", "worker has reached state ${workInfo.state}")

                if (workInfo.state == WorkInfo.State.FAILED) {
                    return@Observer

                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // update UI for success! :)
                    val userInfoJson = workInfo.outputData.getString(USER_INFO_KEY)
                    Log.d("getUserInfo_PrettyName", "userInfo : $userInfoJson")
                    val dataUserInfo =
                        Gson().fromJson(userInfoJson, UserService.UserResponse::class.java).data
                    updateUI(dataUserInfo)
                    return@Observer
                } else {
                    // not interesting, wait until the worker will reach the state we want
                    loadingTextView.visibility = View.VISIBLE
                    return@Observer
                }
            })
    }

    private fun updateUserImgUrl(newImgUrl: String) {
        val workerRequest = OneTimeWorkRequest.Builder(UpdateImgUrlWorker::class.java)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType
                    (NetworkType.CONNECTED).build()
            )
            .setInputData(
                Data.Builder().putString(TOKEN_KEY, token).putString(IMG_URL_KEY, newImgUrl)
                    .build()
            ).build()

        WorkManager.getInstance(this).enqueue(workerRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo == null) return@Observer
                Log.d("ClientServerActivity", "worker has reached state ${workInfo.state}")

                if (workInfo.state == WorkInfo.State.FAILED) {
                    return@Observer

                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // update UI for success! :)
                    val userInfoJson = workInfo.outputData.getString(USER_INFO_KEY)
                    Log.d("UpdateUserImgUrl", "userInfo : $userInfoJson")
                    val imgUrl =
                        Gson().fromJson(userInfoJson, UserService.UserResponse::class.java).data.image_url
                    loadingTextView.visibility = View.INVISIBLE
                    updateImgUrl(imgUrl)
                    return@Observer
                } else {
                    // not interesting, wait until the worker will reach the state we want
                    loadingTextView.visibility = View.VISIBLE
                    return@Observer
                }
            })
    }


    private fun showAddPrettyNameDialog(c: Context) {
        val inputText = EditText(c)
        val dialog = AlertDialog.Builder(c)
            .setView(inputText)
            .setPositiveButton("Set") { _, _ ->
                val input = inputText.text.toString()
                if (input.isNotEmpty()) {
                    getUpdateUserPrettyName(input)
                }
            }
            .setNegativeButton("Cancel", null)
            .setTitle("input a new pretty name")
        dialog.create()
        dialog.show()
    }

}
