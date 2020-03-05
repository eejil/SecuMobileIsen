package com.isen.secumobileisen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_layout.*
import kotlinx.android.synthetic.main.listpatients_layout.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ListActivity : AppCompatActivity() {
/*
    lateinit var recycler_view: RecyclerView
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()

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
            db.collection("listpatients").document(encrypt(listPName.text.toString(),key).toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            Log.d(TAG, "OUI")
            finish()
            startActivity(getIntent())
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Patients>) : FirestoreRecyclerAdapter<Patients, PatientViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: PatientViewHolder, position: Int, patients: Patients) {
            var name = decrypt(patients.name,key)

            productViewHolder.setPatientName(name.toString())
            //productViewHolder.setPatientImage(patients.image)
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

    fun encrypt(strToEncrypt: String, secret: String): String? {
        try {
            var key: ByteArray
            key = secret.toByteArray()
            key = Arrays.copyOf(key, 16)
            var secretKey = SecretKeySpec(key, "AES")
            var iv = "jdetestelekotlin"
            val ivParam = IvParameterSpec(iv.toByteArray())

            val cipher =
                Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParam)
            return Base64.getEncoder()
                .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String?, secret: String): String? {
        try {
            var key: ByteArray
            key = secret.toByteArray()
            key = Arrays.copyOf(key, 16)
            var secretKey = SecretKeySpec(key, "AES")
            var iv = "jdetestelekotlin"
            val ivParam = IvParameterSpec(iv.toByteArray())

            val cipher =
                Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParam)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: java.lang.Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }

*/

}
