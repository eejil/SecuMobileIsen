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
import java.util.jar.Attributes


class MainActivity : AppCompatActivity() {

    lateinit var recycler_view: RecyclerView
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


        // [END handle_data_extras]

            val docRef = db.collection("patients").document("paul")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

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



    private inner class ProductViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
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
            db.collection("patients").document(patientName.text.toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            Log.d(TAG, "OUI")
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)

            return ProductViewHolder(view)
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

    companion object {
        private const val TAG = "MainActivity"
    }

}
