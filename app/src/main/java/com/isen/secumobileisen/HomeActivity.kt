package com.isen.secumobileisen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

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
}
