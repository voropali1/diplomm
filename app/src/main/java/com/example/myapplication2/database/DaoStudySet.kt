package com.example.myapplication2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication2.model.StudySet
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoStudySet {

    @Query("SELECT * FROM study_set_table ORDER BY id DESC")
    fun getAllStudySets(): LiveData<List<StudySet>>

    @Query("SELECT * FROM study_set_table ORDER BY id DESC")
    fun getAllStudySetsFlow(): Flow<List<StudySet>> // ✅ Новый вариант с Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySet(studySet: StudySet): Long

    @Update
    suspend fun updateStudySet(studySet: StudySet)

    @Delete
    suspend fun deleteStudySet(studySet: StudySet)

    @Query("DELETE FROM study_set_table WHERE id=:id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM study_set_table WHERE id=:id")
    fun getSpecificStudySet(id: Int): LiveData<StudySet>

    @Query("SELECT * FROM study_set_table WHERE id=:id")
    suspend fun getNoLiveDataSpecificStudySet(id: Int): StudySet? // ✅ Добавили `suspend`

    @Query("SELECT * FROM study_set_table WHERE sync_status=0")
    suspend fun getUnsyncedStudySet(): List<StudySet> // ✅ Добавили `suspend`

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(studySets: List<StudySet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudySet(studySet: StudySet)

    @Query("DELETE FROM study_set_table")
    suspend fun deleteAll()


}

