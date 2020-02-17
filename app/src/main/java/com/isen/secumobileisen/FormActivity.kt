package com.isen.secumobileisen

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.list_layout.*
import java.text.SimpleDateFormat
import java.util.*

class FormActivity : AppCompatActivity() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    var cloudFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        textview_date = this.inputDate
        choixDate()
        btn_date.setOnClickListener {
            addPatientToFirestore(setPatient())
        }
    }

    private fun choixDate(){
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        textview_date?.let {
            it.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    DatePickerDialog(
                        this@FormActivity,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

            })
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        textview_date?.let{
            it.text = sdf.format(cal.getTime())
        }

    }

    private fun setPatient(): Patients {
        var name = this.txt_name.editableText.toString()
        var patho = this.txt_pathology.editableText.toString()
        var traitement= this.txt_treatments.editableText.toString()
        var description= this.txt_today.editableText.toString()
        var dateVisite = textview_date.let{it?.text.toString()}

        var newPatient: Patients =
            Patients(
                dateVisite,
                name,
                patho,
                traitement,
                description
            )

        return newPatient

    }

    private fun addPatientToFirestore(newPatient: Patients){
        cloudFirestore.collection("patients").document(newPatient.name)
            .set(newPatient)
            .addOnSuccessListener { Log.d("Success :", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Error :", "Error writing document", e) }
    }
}
