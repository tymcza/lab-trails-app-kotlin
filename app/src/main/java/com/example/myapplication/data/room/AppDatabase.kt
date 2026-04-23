package com.example.myapplication.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RouteRoom::class, RecordRoom::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRoutesDao(): RoomDao

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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_routes_name` ON `routes`(`name`)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `records` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `correspondingRouteId` INTEGER NOT NULL, 
                `registeredTimeSeconds` INTEGER NOT NULL, 
                `date` INTEGER NOT NULL, 
                FOREIGN KEY(`correspondingRouteId`) REFERENCES `routes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE 
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_records_correspondingRouteId` ON `records`(`correspondingRouteId`)")
            }
        }
    }
}