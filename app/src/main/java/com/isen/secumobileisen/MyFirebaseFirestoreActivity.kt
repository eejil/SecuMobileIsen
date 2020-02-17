package com.isen.secumobileisen


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Kotlin version of doc snippets.
 *
 */
abstract class MyFirebaseFirestoreActivity(val db: FirebaseFirestore) {

    companion object {

        private val TAG = "DocSnippets"

        private val EXECUTOR = ThreadPoolExecutor(
            2, 4,
            60, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
    }

    private fun setup() {
        // [START get_firestore_instance]
        val db = FirebaseFirestore.getInstance()
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]
    }

    private fun addAdaLovelace() {
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        val patient = hashMapOf(
            "date" to 2020,
            "name" to "Dupont",
            "pathology" to "Choléra",
            "treatments" to "Manger",
            "today" to "Oui"
        )

        // Add a new document with a generated ID
        db.collection("patients")
            .add(patient)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        // [END add_ada_lovelace]
    }
}
