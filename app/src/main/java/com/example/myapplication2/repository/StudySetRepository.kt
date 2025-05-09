package com.example.myapplication2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication2.database.DaoStudySet
import com.example.myapplication2.database.StudySetDatabase
import com.example.myapplication2.model.StudySet
import javax.inject.Inject

class StudySetRepository @Inject constructor(
    private val dao: DaoStudySet,
    private val studySetDao: DaoStudySet // если это другой DAO, не путай с базой данных!
) {

    // Получаем список всех сетов как LiveData
    val allStudySets: LiveData<List<StudySet>> = dao.getAllStudySets()  // Здесь нет необходимости преобразовывать в List вручную

    // Метод для добавления нового сета
    suspend fun insert(studySet: StudySet): Long {
        return dao.insertStudySet(studySet) // предполагается, что insert возвращает Long (ID)
    }


    // Метод для обновления существующего сета
    suspend fun update(studySet: StudySet) {
        dao.updateStudySet(studySet)
        Log.d("StudySetRepository", "Updated: ${studySet.name}")
    }

    // Метод для удаления сета
    suspend fun delete(studySet: StudySet) {
        dao.deleteStudySet(studySet)
    }

    // Метод для удаления сета по его ID
    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    // Получение сета по ID без использования LiveData
    suspend fun getStudySetById(id: Long): StudySet? {
        return dao.getNoLiveDataSpecificStudySet(id.toInt())
    }

    suspend fun updateSetFinishedStatus(setId: Int) {
        val studySet = dao.getNoLiveDataSpecificStudySet(setId)
        studySet?.let {
            // Update the finished status to true (or some other status change)
            it.isFinished= true
            dao.updateStudySet(it)  // Assuming this method exists to update the study set in your DB
        }
    }



}



