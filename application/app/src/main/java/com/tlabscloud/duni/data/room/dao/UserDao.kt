package com.tlabscloud.duni.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.tlabscloud.duni.data.model.User


@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun create(user: User)

    @Update
    fun update(user: User)

    @Transaction
    fun replace(user: User) {
        deleteAll()
        create(user)
    }

    @Query("DELETE FROM User")
    fun deleteAll()

    @Query("SELECT * FROM User")
    fun load(): LiveData<User>

    @Query("SELECT * FROM User")
    fun getUser(): User?
}
