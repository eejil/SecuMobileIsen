package com.isen.secumobileisen

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.security.Key
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    private val SIGNATURE: String = "HbiYuFB2kKyIs/VLWd92GjauJDceQxrMAnwI9dwJ9DU=\n"
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    private var keyAES = generateSymmetricKey("keyAES")

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

        /*btn_addK.setOnClickListener {
            generateSymmetricKey(et_key.text.toString())
        }
       */
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
            .setOnClickListener { startActivity(Intent(this@LoginActivity,
                CreateAccountActivity::class.java)) }
        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Registering User...")
            mProgressBar!!.show()
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("KEY_AES", keyAES);
        startActivity(intent)
        finish()
    }

    fun generateSymmetricKey(keyAlias: String): Key {

        val IV = "jdetestelekotlin"
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())


        val keyStore = KeyStore.getInstance("AndroidKeyStore")
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

        else {
            Log.d("LoginActiivty", "Non")

        }
        return keyStore.getKey(keyAlias, null)
    }

    fun loadSymmetricKey(keyAlias: String) : Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        var secretKey: SecretKey?
        secretKey = (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey

        return secretKey
    }

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
