package com.isen.secumobileisen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.firebase.firestore.FirebaseFirestore
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator


class HomeActivity : AppCompatActivity() {

    var cloudFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        //createMasterKey()
        pushMasterKey()
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

    fun createMasterKey() : Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)


        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "TheMasterKey",
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


    fun pushMasterKey() {
        val key  = "azertyuiopazerty"

        val data: MutableMap<String, String> =
            HashMap()
        data["name"] = key
        cloudFirestore.collection("masterKey").document("masterKey")
            .set(data)
    }

}


