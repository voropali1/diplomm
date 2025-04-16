package com.example.myapplication2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication2.model.StudySet

@Database(entities = [StudySet::class], version = 2, exportSchema = false) // ✅ Обновили версию
abstract class StudySetDatabase : RoomDatabase() {
    abstract fun studySetDao(): DaoStudySet

    companion object {
        @Volatile
        private var INSTANCE: StudySetDatabase? = null

        fun getDatabase(context: Context): StudySetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudySetDatabase::class.java,
                    "study_set_database"
                )
                    .addMigrations(MIGRATION_1_2) // ✅ Добавляем миграцию
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // ✅ Миграция с версии 1 на 2: добавляем колонку sync_status
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE study_set_table ADD COLUMN sync_status INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}

