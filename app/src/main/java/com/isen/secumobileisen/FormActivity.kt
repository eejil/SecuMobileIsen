package com.isen.secumobileisen

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_form.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class FormActivity : AppCompatActivity() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    var cloudFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        textview_date = this.inputDate
        choixDate()
        btn_date.setOnClickListener {
            addPatientToFirestore(setPatient())
        }
    }

    fun docReference() { // [START doc_reference]
        val alovelaceDocumentRef =
            db.collection("users").document("alovelace")
        // [END doc_reference]
    }

    private fun choixDate() {
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
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
        textview_date?.let {
            it.text = sdf.format(cal.getTime())
        }

    }

    private fun setPatient(): Patients {
        val key = "jdtestelekotlin"
        var name: String?
        var patho: String?
        var traitement: String?
        var description: String?
        var dateVisite: String?
        name = this.txt_name.editableText.toString()
        patho = this.txt_pathology.editableText.toString()
        traitement = this.txt_treatments.editableText.toString()
        description = this.txt_today.editableText.toString()
        dateVisite = textview_date.let { it?.text.toString() }

        name = encrypt(name, key)
        patho = encrypt(patho, key)
        traitement = encrypt(traitement, key)
        description = encrypt(description, key)
        dateVisite = encrypt(dateVisite, key)

        var newPatient =
            Patients(
                dateVisite.toString(),
                name.toString(),
                patho.toString(),
                traitement.toString(),
                description.toString()
            )



        return newPatient

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
            return getEncoder()
                .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    private fun addPatientToFirestore(newPatient: Patients) {
        cloudFirestore.collection("patients").document(newPatient.name)
            .set(newPatient)
            .addOnSuccessListener { Log.d("Success :", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Error :", "Error writing document", e) }
        cloudFirestore.collection("histopatients").document(newPatient.name)
            .set(newPatient)
            .addOnSuccessListener { Log.d("Success :", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Error :", "Error writing document", e) }

        // [START get_document]
        db.collection("listpatients")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var i = 0
                    var cant_write = false
                    for (document in task.result!!) {
                        Log.d(
                            "FormActivity",
                            document.id + " => " + document.data
                        )
                        if (newPatient.name == document.data.getValue("name")) {
                            cant_write = true
                        }
                    }

                    if (cant_write == false) {

                        val data: MutableMap<String, String> =
                            HashMap()
                        data["name"] = newPatient.name
                        cloudFirestore.collection("listpatients").document(newPatient.name)
                            .set(data)
                            .addOnSuccessListener { Log.d("Success :", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("Error :", "Error writing document", e) }
                    }
                }

                else {
                    val data: MutableMap<String, String> =
                        HashMap()
                    data["name"] = newPatient.name
                    cloudFirestore.collection("listpatients").document(newPatient.name)
                        .set(data)
                        .addOnSuccessListener { Log.d("Success :", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("Error :", "Error writing document", e) }
                }
            }
        // [END get_document]
        }
    }

