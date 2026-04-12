package com.example.myapplication.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.room.RouteRoom

@Database(entities = [RouteRoom::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRoutesDao(): RoutesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "routes_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build().also {INSTANCE = it}
            }
        }

        private val MIGRATION_1_2 = object: Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS route_types")
            }
        }

        private val MIGRATION_2_3 = object: Migration(2,3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE routes_catalogue RENAME TO routes")
                db.execSQL("""
            DELETE FROM `routes` 
            WHERE `id` NOT IN (
                SELECT MIN(`id`) 
                FROM `routes` 
                GROUP BY `name`
            )
        """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_routes_name` ON `routes` (`name`)")
            }
        }
    }
}