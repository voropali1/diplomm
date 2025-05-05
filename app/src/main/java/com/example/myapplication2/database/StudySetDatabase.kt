package com.example.myapplication2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication2.model.StudySet

@Database(entities = [StudySet::class], version = 1, exportSchema = false)
abstract class StudySetDatabase : RoomDatabase() {
    abstract fun studySetDao(): DaoStudySet
}



