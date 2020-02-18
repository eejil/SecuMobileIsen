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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_layout.*

class HistoActivity : AppCompatActivity() {

    lateinit var recycler_view: RecyclerView
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val query = db!!.collection("histopatients").orderBy("name", Query.Direction.ASCENDING)

        recycler_view = findViewById(R.id.listView)
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
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Patients>) : FirestoreRecyclerAdapter<Patients, ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int, patients: Patients) {
            productViewHolder.setPatientName(patients.name)
            productViewHolder.setPatientDate(patients.date)
            productViewHolder.setPatientToday(patients.today)
            productViewHolder.setPatientPathology(patients.pathology)
            productViewHolder.setPatientTreatments(patients.treatments)
            //productViewHolder.setPatientImage(patients.image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.histo_layout, parent, false)

            return ProductViewHolder(view)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
