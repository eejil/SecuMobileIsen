package com.isen.secumobileisen

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
//import com.scottyab.rootbeer.RootBeer
import kotlinx.android.synthetic.main.activity_login.*
import java.security.Key
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    private val SIGNATURE: String = ""
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    private var NETWORK_PERMISSION = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(!goodInstaller()){
            AlertDialog.Builder(this)
                .setTitle("ATTENTION")
                .setMessage("Votre application n'a pas été installée par un programme reconnu.")
                .setNeutralButton("Ok") { _, _ -> }
                .create()
                .show()
        }

        if(isEmulator()){
            AlertDialog.Builder(this)
                .setTitle("ATTENTION")
                .setMessage("Votre application tourne sur un emulateur.")
                .setNeutralButton("Ok") { _, _ -> }
                .create()
                .show()
        }

        if(!goodSignature()){
            AlertDialog.Builder(this)
                .setTitle("ATTENTION")
                .setMessage("Votre application ne possède pas la signature du constructeur.")
                .setNeutralButton("Ok") { _, _ -> }
                .create()
                .show()
        }

        initialise()

        //checkRooting()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            NETWORK_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        btn_addK.setOnClickListener {
            generateSymmetricKey(et_key.text.toString())
        }
    }

    private fun initialise() {
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!
            .setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        CreateAccountActivity::class.java
                    )
                )
            }
        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()
        if (checkNetwork()) {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mProgressBar!!.setMessage("Registering User...")
                mProgressBar!!.show()
                mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        mProgressBar!!.hide()
                        if (task.isSuccessful) {
                            updateUI()
                        } else {
                            Toast.makeText(
                                this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (checkSharedPreferences(email.toString(), password.toString())) {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@LoginActivity, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    fun generateSymmetricKey(keyAlias: String): Key {

        /*val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setKeySize(128)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
            }
            */

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()

            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }



        else {
            Log.d("LoginActiivty", "Non")

        }
        return keyStore.getKey(keyAlias, null)
    }

    fun loadSymmetricKey(keyAlias: String): Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        var secretKey: SecretKey?
        secretKey = (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey

        return secretKey
    }

    private fun checkSharedPreferences(mail: String, password: String): Boolean {
        val db = getSharedPreferences("user_db", Activity.MODE_PRIVATE)
        val mail_alias = "mail" + mail
        val password_alias = "password" + password

        if (db.contains(mail_alias)) {
            return true
        }

        return false
    }

    private fun checkNetwork(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    private fun askNetworkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                    NETWORK_PERMISSION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

    }

    /*private fun checkRooting(){

        var rootBeer  = RootBeer(this)

        if (rootBeer.isRooted) {
            val mySnackbar = Snackbar.make(mylayout, "appareil rooté", Snackbar.LENGTH_LONG)
            mySnackbar.show()
        }
        else {
            val mySnackbar2 = Snackbar.make(mylayout, "appareil non-rooté", Snackbar.LENGTH_LONG)
            mySnackbar2.show()
        }
    }*/
    private fun goodInstaller(): Boolean {
        val installer: String? = this.packageManager.getInstallerPackageName(this.packageName)
        return installer != null && installer.startsWith("com.android.vending")
    }

    private fun getCurrentSignature(): String? {
        try {
            val packageInfo: PackageInfo =
                this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)

            for (signature in packageInfo.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA-512")
                md.update(signature.toByteArray())
                val currentSignature: String = Base64.encodeToString(md.digest(), Base64.DEFAULT)

                Log.d(
                    "REMOVEME",
                    "Include this string as a value for SIGNATURE:" + currentSignature
                )

                return currentSignature
            }
        } catch (e: Exception) {
            e.stackTrace
        }

        return null
    }

    private fun goodSignature(): Boolean {
        if (getCurrentSignature().equals(SIGNATURE)) {
            return true
        }
        return false
    }

    private fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }

}
