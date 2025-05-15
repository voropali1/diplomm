package com.example.myapplication2.repository

import androidx.lifecycle.LiveData
import com.example.myapplication2.database.DaoStudySet
import com.example.myapplication2.model.StudySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudySetRepository @Inject constructor(
    private val dao: DaoStudySet,
    private val firebaseRepository: FirebaseRepository,
) {
    val allStudySets: LiveData<List<StudySet>> = dao.getAllStudySets()

    suspend fun insertManyLocalOnly(studySets: List<StudySet>) {
        dao.insertManyStudySets(studySets)
    }

    suspend fun insert(studySet: StudySet): Long {
        val newStudySetId = dao.insertStudySet(studySet)
        val newStudySet = studySet.apply { id = newStudySetId.toInt() }

        firebaseRepository.addStudySet(studySet = newStudySet, onSuccess = {}, onError = {})
        return newStudySetId
    }

    suspend fun update(studySet: StudySet) {
        dao.updateStudySet(studySet)
        firebaseRepository.updateStudySet(studySet)
    }

    suspend fun delete(studySet: StudySet) {
        dao.deleteStudySet(studySet)
        firebaseRepository.deleteStudySet(studySet)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun getStudySetById(id: Long): StudySet? {
        return dao.getNoLiveDataSpecificStudySet(id.toInt())
    }

    suspend fun updateSetFinishedStatus(setId: Int) {
        val studySet = dao.getNoLiveDataSpecificStudySet(setId)
        studySet?.let {
            // Update the finished status to true (or some other status change)
            it.isFinished = true
            dao.updateStudySet(it)  // Assuming this method exists to update the study set in your DB
            firebaseRepository.updateSetFinished(setId)
        }
    }
}



