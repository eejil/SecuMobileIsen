package com.isen.secumobileisen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_layout.*
import kotlinx.android.synthetic.main.list_layout.view.*
import java.security.KeyStore
import java.util.*
import java.util.jar.Attributes
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class MainActivity : AppCompatActivity() {

 /*   lateinit var recycler_view: RecyclerView
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val query = db!!.collection("patients").orderBy("name", Query.Direction.ASCENDING)

        recycler_view = findViewById(R.id.listView)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Patients>().setQuery(query, Patients::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        recycler_view.adapter = adapter

        btn_form.setOnClickListener {
            goToForm()
        }

        btn_histo.setOnClickListener {
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
            val patientName = view.findViewById<TextView>(R.id.patientName)
            patientName.text = Name
        }
        internal fun setPatientDate(Date: String) {
            val patientDate = view.findViewById<TextView>(R.id.patientDate)
            patientDate.text = Date
        }
        internal fun setPatientPathology(Pathology: String) {
            val patientPathology = view.findViewById<TextView>(R.id.patientPathology)
            patientPathology.text = Pathology
        }
        internal fun setPatientTreatments(Treatments: String) {
            val patientTreatments = view.findViewById<TextView>(R.id.patientTreatments)
            patientTreatments.text = Treatments
        }
        internal fun setPatientToday(Today: String) {
            val patientToday = view.findViewById<TextView>(R.id.patientToday)
            patientToday.text = Today
        }

        protected var btn_delete: ImageView

        init {
            btn_delete = itemView.findViewById(R.id.btn_delete)
            btn_delete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            db.collection("patients").document(encrypt(patientName.text.toString()).toString())
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
            var name = decrypt(patients.name)
            var patho = decrypt(patients.pathology)
            var traitement = decrypt(patients.treatments)
            var description = decrypt(patients.today)
            var dateVisite = decrypt(patients.date)

            productViewHolder.setPatientName(name.toString())
            productViewHolder.setPatientDate(patho.toString())
            productViewHolder.setPatientToday(traitement.toString())
            productViewHolder.setPatientPathology(description.toString())
            productViewHolder.setPatientTreatments(dateVisite.toString())
            //productViewHolder.setPatientImage(patients.image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
            return PatientViewHolder(view)
        }
    }

    private fun goToForm() {
        //start next activity
        val intent = Intent(this@MainActivity, FormActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun goToHisto() {
        //start next activity
        val intent = Intent(this@MainActivity, HistoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKey = (keyStore.getEntry(
                "pkpas",
                null
            ) as KeyStore.SecretKeyEntry).secretKey

            val IV = "jdetestelekotlin"
            val ivParameterSpec = IvParameterSpec(IV.toByteArray())

            val cipher =
                Cipher.getInstance("AES/CBC/PKCS7Padding")

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.getEncoder()
                .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
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
                "pkpas",
                null
            ) as KeyStore.SecretKeyEntry).secretKey

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

            val ivParameterSpec = IvParameterSpec(cipher.iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey,ivParameterSpec )
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: java.lang.Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }*/
}
