package com.tlabscloud.duni


import android.app.Application
import com.tlabscloud.duni.data.remote.authApiModule
import com.tlabscloud.duni.data.remote.backendModule
import com.tlabscloud.duni.data.remote.courseResultApiModule
import com.tlabscloud.duni.data.room.dao.UserDao
import com.tlabscloud.duni.data.room.dao.UserDao_Impl
import com.tlabscloud.duni.data.room.databaseModule
import com.tlabscloud.duni.ui.login.LoginViewModel
import com.tlabscloud.duni.ui.main.MainFragment
import com.tlabscloud.duni.utils.SPHelper
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class DUniApp : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@DUniApp))

        import(databaseModule)
        import(backendModule)
        import(authApiModule)
        import(courseResultApiModule)

        bind<UserDao>() with singleton { UserDao_Impl(instance()) }


        bind<SPHelper>() with singleton { SPHelper(instance()) }

        bind<UserRepository>() with singleton {
            UserRepository(
                instance(),
                instance(),
                instance()
                )
        }
       // bind<ClientRepository>() with singleton { ClientRepository(instance()) }

        bind<LoginViewModel>() with singleton { LoginViewModel(instance(), instance()) }
        bind<MainViewModel>() with singleton { MainViewModel(instance()) }

        bind<MainFragment>() with singleton {
            MainFragment()
        }
    }
}
