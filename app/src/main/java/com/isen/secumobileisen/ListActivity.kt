package com.isen.secumobileisen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.listpatients_layout.*
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class ListActivity : AppCompatActivity() {

    lateinit var recycler_view: RecyclerView
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()
    //Encrypted shared pref
    private val preferencesName = "SharedPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


        val query = db!!.collection("listpatients").orderBy("name", Query.Direction.ASCENDING)

        recycler_view = findViewById(R.id.listViewP)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Patients>().setQuery(query, Patients::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        recycler_view.adapter = adapter

        btn_formP.setOnClickListener {
            goToForm()
        }

        btn_histoP.setOnClickListener {
            goToHisto()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }



    private inner class PatientViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        internal fun setPatientName(Name: String) {
            val patientName = view.findViewById<TextView>(R.id.listPName)
            patientName.text = Name
        }

        protected var btn_delete: ImageView

        init {
            btn_delete = itemView.findViewById(R.id.btn_deletep)
            btn_delete.setOnClickListener(this)
        }


        override fun onClick(v: View) {
            db.collection("listpatients").document(encrypt(listPName.text.toString()).toString())
                .delete()
            finish()
            startActivity(getIntent())
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Patients>) : FirestoreRecyclerAdapter<Patients, PatientViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: PatientViewHolder, position: Int, patients: Patients) {
            var name = decrypt(patients.name)

            productViewHolder.setPatientName(name.toString())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.listpatients_layout, parent, false)
            return PatientViewHolder(view)
        }
    }

    private fun goToForm() {
        //start next activity
        val intent = Intent(this@ListActivity, FormActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun goToHisto() {
        //start next activity
        val intent = Intent(this@ListActivity, HistoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKey = (keyStore.getEntry(
                "venotbg",
                null
            ) as KeyStore.SecretKeyEntry).secretKey

            val Iv = "jdetestelekotlin"
            val IvParameterSpec = IvParameterSpec(Iv.toByteArray())

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE,secretKey, IvParameterSpec)
            val cipherText = cipher.doFinal(strToEncrypt.toByteArray())

            //TODO: Change SharedPref
            //val db = getSharedPreferences(preferencesName, Activity.MODE_PRIVATE)
            val db = getSharedPreferences("user_db", Activity.MODE_PRIVATE)
            val doc_alias = "alias" + Base64.getEncoder().encodeToString(cipherText)
            val iv = cipher.iv.toString()

            val editor = db.edit()
            editor.putString(doc_alias,iv)
            editor.commit()

            return Base64.getEncoder().encodeToString(cipherText)

        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String?): String? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKey = (keyStore.getEntry(
                "venotbg",
                null
            ) as KeyStore.SecretKeyEntry).secretKey

            val Iv = "jdetestelekotlin"
            val IvParameterSpec = IvParameterSpec(Iv.toByteArray())

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: java.lang.Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }



}
