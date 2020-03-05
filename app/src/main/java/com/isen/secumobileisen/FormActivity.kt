package com.isen.secumobileisen

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_form.*
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec


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

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE,secretKey, IvParameterSpec)
            val cipherText = cipher.doFinal(strToEncrypt.toByteArray())

            val db = getSharedPreferences("user_db", Activity.MODE_PRIVATE)
            val doc_alias = "alias" + getEncoder().encodeToString(cipherText)
            val iv = cipher.iv.toString()
            Log.d("Alias", doc_alias)
            Log.d("iv", iv)

            val editor = db.edit()
            editor.putString(doc_alias,iv)
            editor.commit()

            return getEncoder().encodeToString(cipherText)





            /*val cipher = Cipher.getInstance("AES/ECB/NoPadding")

            var temp = strToEncrypt
            while (temp.toByteArray().size % 16 != 0)
                temp += "\u0020"


            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return getEncoder().encodeToString(cipher.doFinal(temp.toByteArray(charset("UTF-8"))))*/
            /*val cipheredString = getEncoder().encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))

            val ivParameterSpec = IvParameterSpec(cipher.iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey,ivParameterSpec )
            val decipheredString = String(cipher.doFinal(Base64.getDecoder().decode(cipheredString)))
            Log.d("Pourquoi ça marche pas?", decipheredString)

             */


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

