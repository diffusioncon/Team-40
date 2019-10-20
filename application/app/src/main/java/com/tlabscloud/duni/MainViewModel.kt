package com.tlabscloud.duni

import androidx.lifecycle.LiveData
import com.tlabscloud.duni.data.model.User
import com.tlabscloud.duni.utils.ScopedViewModel
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel constructor(private val userRepository: UserRepository) : ScopedViewModel() {
    val userLiveData: LiveData<User> = userRepository.get()

    suspend fun isLogin(): Boolean = withContext(IO) {
        userRepository.getUser() != null
    }

    fun logout() = launch(IO) {
        userRepository.wipeUserData()
    }
}
