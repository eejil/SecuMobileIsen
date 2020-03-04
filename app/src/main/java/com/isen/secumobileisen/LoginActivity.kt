package com.isen.secumobileisen

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
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

    fun loadSymmetricKey(keyAlias: String) : Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        var secretKey: SecretKey?
        secretKey = (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey

        return secretKey
    }
}
