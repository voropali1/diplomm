package com.example.myapplication2.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication2.database.DaoStudySet
import com.example.myapplication2.database.StudySetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StudySetDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StudySetDatabase::class.java,
            "study_set_database"
        ).build()
    }

    @Provides
    fun provideDao(database: StudySetDatabase): DaoStudySet {
        return database.studySetDao()
    }
}


