package com.example.myapplication2.repository

import android.util.Log
import com.example.myapplication2.model.StudySet
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val userProfilePreferencesManager: UserProfilePreferencesManager,
) {

    private val db = Firebase.firestore
    private val userDocumentId: String?
        get() = userProfilePreferencesManager.getUserDocumentId()

    fun createUser(email: String, onSuccess: () -> Unit, onError: () -> Unit) {
        db.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val userDocument = result.documents.firstOrNull { it.data?.containsValue(email) == true }

                if (userDocument != null) {
                    saveUserDocumentId(userDocument.id)
                    onSuccess()
                } else {
                    val user = hashMapOf(EMAIL_FIELD to email)
                    db.collection(USERS_COLLECTION)
                        .add(user)
                        .addOnSuccessListener { resultAdd ->
                            saveUserDocumentId(resultAdd.id)
                            onSuccess()
                        }
                        .addOnFailureListener { onError() }
                }
            }
            .addOnFailureListener { exception ->
                onError()
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun saveUserDocumentId(documentId: String) {
        userProfilePreferencesManager.setUserDocumentId(documentId)
    }

    fun addStudySet(studySet: StudySet, onSuccess: () -> Unit, onError: () -> Unit) {
        val studySetData = hashMapOf(
            "id" to studySet.id,
            "name" to studySet.name,
            "words" to studySet.words,
            "marked_words" to studySet.marked_words,
            "language_to" to studySet.language_to,
            "language_from" to studySet.language_from,
            "amount_of_words" to studySet.amount_of_words,
            "isFinished" to studySet.isFinished,
        )

        getStudySetsCollection()
            .add(studySetData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getAllStudySets(onSuccess: (List<StudySet>) -> Unit, onError: () -> Unit) {
        Log.d(TAG, "getAllStudySets() called")
        getStudySetsCollection()
            .get()
            .addOnSuccessListener { result ->
                val studySets = mutableListOf<StudySet>()
                for (document in result) {
                    val data = document.data
                    val studySet = StudySet(
                        id = (data[STUDY_SET_ID_FIELD] as Long).toInt(),
                        name = data["name"] as String,
                        words = data["words"] as String,
                        marked_words = data["marked_words"] as String,
                        language_to = data["language_to"] as String,
                        language_from = data["language_from"] as String,
                        amount_of_words = (data["amount_of_words"] as Long).toInt(),
                        isFinished = data["isFinished"] as Boolean
                    )
                    studySets.add(studySet)
                }
                onSuccess(studySets)
            }
            .addOnFailureListener { exception ->
                onError()
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun getStudySetsCollection(): CollectionReference {
        return db.collection(USERS_COLLECTION)
            .document(userDocumentId!!)
            .collection(STUDY_SETS_COLLECTION)
    }

    fun updateStudySet(studySet: StudySet) {
        getStudySetDocument(studySet.id) { document ->
            document
                .update(
                    mapOf(
                        "name" to studySet.name,
                        "words" to studySet.words,
                        "marked_words" to studySet.marked_words,
                        "language_to" to studySet.language_to,
                        "language_from" to studySet.language_from,
                        "amount_of_words" to studySet.amount_of_words,
                        "isFinished" to studySet.isFinished,
                    )
                )
                .addOnSuccessListener {
                    Log.d(TAG, "Study set updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating study set", e)
                }
        }
    }

    fun deleteStudySet(studySet: StudySet) {
        getStudySetDocument(studySet.id) { document ->
            document
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Study set deleted successfully")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting study set", e)
                }
        }
    }

    fun updateSetFinished(studySetId: Int) {
        getStudySetDocument(studySetId) { document ->
            document
                .update(mapOf("isFinished" to true))
                .addOnSuccessListener {
                    Log.d(TAG, "Study set updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating study set", e)
                }
        }
    }

    private fun getStudySetDocument(
        studySetId: Int,
        onDocumentOperation: (DocumentReference) -> Unit
    ) {
        getStudySetsCollection()
            .whereEqualTo(STUDY_SET_ID_FIELD, studySetId)
            .limit(1)
            .get()
            .addOnSuccessListener {
                val document = it.documents.firstOrNull()
                if (document != null) {
                    onDocumentOperation(getStudySetsCollection().document(document.id))
                } else {
                    Log.d(TAG, "No such document")
                }
            }
    }

    companion object {
        private const val TAG = "FirebaseRepository"
        private const val USERS_COLLECTION = "users"
        private const val STUDY_SETS_COLLECTION = "study_sets"
        private const val EMAIL_FIELD = "email"
        private const val STUDY_SET_ID_FIELD = "id"
    }
}