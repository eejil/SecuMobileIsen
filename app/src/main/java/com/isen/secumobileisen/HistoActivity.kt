package com.isen.secumobileisen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class HistoActivity : AppCompatActivity() {

    lateinit var recycler_view: RecyclerView
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histo)


        val query = db!!.collection("histopatients").orderBy("name", Query.Direction.ASCENDING)

        recycler_view = findViewById(R.id.histoView)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Patients>().setQuery(query, Patients::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        recycler_view.adapter = adapter
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


    private inner class ProductViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        internal fun setPatientName(Name: String) {
            val patientName = view.findViewById<TextView>(R.id.histoName)
            patientName.text = Name
        }
        internal fun setPatientDate(Date: String) {
            val patientDate = view.findViewById<TextView>(R.id.histoDate)
            patientDate.text = Date
        }
        internal fun setPatientPathology(Pathology: String) {
            val patientPathology = view.findViewById<TextView>(R.id.histoPathology)
            patientPathology.text = Pathology
        }
        internal fun setPatientTreatments(Treatments: String) {
            val patientTreatments = view.findViewById<TextView>(R.id.histoTreatments)
            patientTreatments.text = Treatments
        }
        internal fun setPatientToday(Today: String) {
            val patientToday = view.findViewById<TextView>(R.id.histoToday)
            patientToday.text = Today
        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Patients>) : FirestoreRecyclerAdapter<Patients, ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int, patients: Patients) {
            val name = decrypt(patients.name)
            val patho = decrypt(patients.pathology)
            val traitement = decrypt(patients.treatments)
            val description = decrypt(patients.today)
            val dateVisite = decrypt(patients.date)

            productViewHolder.setPatientName(name.toString())
            productViewHolder.setPatientDate(dateVisite.toString())
            productViewHolder.setPatientToday(description.toString())
            productViewHolder.setPatientPathology(patho.toString())
            productViewHolder.setPatientTreatments(traitement.toString())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.histo_layout, parent, false)

            return ProductViewHolder(view)
        }
    }

    fun decrypt(strToDecrypt: String?): String? {
        try {

            val Iv = "jdetestelekotlin"
            val IvParameterSpec = IvParameterSpec(Iv.toByteArray())

            val key ="{name=azeriopaz}"
            val skeySpec = SecretKeySpec(key.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, IvParameterSpec)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: java.lang.Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }
}
