package com.tlabscloud.duni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tlabscloud.duni.ui.main.MainFragment
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein by org.kodein.di.android.kodein()
    private val mainViewModel: MainViewModel by instance()
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_certificate -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        mainViewModel.userLiveData.observe(this, Observer {
            if (it == null && navController.currentDestination?.id != R.id.loginFragment) {
                navController.navigate(R.id.loginFragment)
            }else if(it !=null){
                navController.navigate(R.id.mainFragment)
            }
        })
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (R.id.loginFragment != destination.id) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (!mainViewModel.isLogin()) {
                        controller.navigate(R.id.loginFragment)
                    }
                }
            }
        }
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }*/
    }

    fun navBarVisibility(visibility: Boolean) {
        when(visibility){
            true -> navigation.visibility = View.VISIBLE
            else -> navigation.visibility = View.GONE
        }
    }
}
