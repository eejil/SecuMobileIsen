package com.isen.secumobileisen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_form.*
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap
import kotlin.collections.set


class FormActivity : AppCompatActivity() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    var cloudFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        mDatabase = FirebaseDatabase.getInstance().getReference();

        textview_date = this.inputDate
        choixDate()
        btn_date.setOnClickListener {
            addPatientToFirestore(setPatient())
        }
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

        name = encrypt(name)
        patho = encrypt(patho)
        traitement = encrypt(traitement)
        description = encrypt(description)
        dateVisite = encrypt(dateVisite)

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

            val key = "azertyuiopazerty"
            val skeySpec = SecretKeySpec(key.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec)
            val cipherText = cipher.doFinal(strToEncrypt.toByteArray())


            return getEncoder().encodeToString(cipherText)

        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    private fun addPatientToFirestore(newPatient: Patients) {
        cloudFirestore.collection("patients").document(newPatient.name.replace("/","A"))
            .set(newPatient)
        cloudFirestore.collection("histopatients").document(newPatient.name.replace("/","A"))
            .set(newPatient)

        // [START get_document]
        db.collection("listpatients")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var i = 0
                    var cant_write = false
                    for (document in task.result!!) {
                        if (newPatient.name == document.data.getValue("name")) {
                            cant_write = true
                        }
                    }

                    if (cant_write == false) {

                        val data: MutableMap<String, String> =
                            HashMap()
                        data["name"] = newPatient.name
                        cloudFirestore.collection("listpatients").document(newPatient.name.replace("/","A"))
                            .set(data)
                    }
                } else {
                    val data: MutableMap<String, String> =
                        HashMap()
                    data["name"] = newPatient.name
                    cloudFirestore.collection("listpatients").document(newPatient.name.replace("/","A"))
                        .set(data)
                }
            }
        // [END get_document]
    }

    private fun getMasterKey(): String {
        var masterKey = ""


        return masterKey
    }
}


