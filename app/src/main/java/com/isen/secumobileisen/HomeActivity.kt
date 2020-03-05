package com.isen.secumobileisen

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.Exception

import java.security.MessageDigest
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class HomeActivity : AppCompatActivity() {

    private val preferencesName = "SharedPreferences"

    // Step 0: EncryptedSharedPreferences take long to initialize/open, therefor it's better to do it only once and keep an instance
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        histoIcon.setOnClickListener {
            goToHisto()
        }

        listIcon.setOnClickListener {
            goToList()
        }

        listPIcon.setOnClickListener {
            goToListP()
        }

        btn_logout.setOnClickListener {
            logout()
        }
    }

    private fun goToHisto() {
        //start next activity
        val intent = Intent(this@HomeActivity, HistoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun goToList() {
        //start next activity
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun goToListP() {
        //start next activity
        val intent = Intent(this@HomeActivity, ListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    /*private fun initEncryptedSharedPreferences() {
        getSharedPreferences(preferencesName, MODE_PRIVATE).edit().apply()

        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        sharedPreferences = EncryptedSharedPreferences.create(
            preferencesName,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveValue() {
        sharedPreferences.edit().putString("DATA", SIGNATURE).apply()
    }

    private fun readValue(): String? {
        return sharedPreferences.getString("DATA", "")
    }*/

}


