package com.tlabscloud.duni.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tlabscloud.duni.data.model.User
import com.tlabscloud.duni.data.room.AppDb.Companion.databaseName
import com.tlabscloud.duni.data.room.dao.UserDao
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val databaseName = "d-uni-db"
    }
}

val databaseModule = Kodein.Module("DatabaseModule") {
    bind<RoomDatabase>() with singleton {
        Room.databaseBuilder(instance(), AppDb::class.java, databaseName)
            .fallbackToDestructiveMigration()
            .build()
    }
}
