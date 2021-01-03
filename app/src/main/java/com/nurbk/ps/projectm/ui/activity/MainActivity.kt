package com.nurbk.ps.projectm.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.iid.FirebaseInstanceId
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.ActivityMainBinding
import com.nurbk.ps.projectm.others.IS_SIGN_IN
import com.nurbk.ps.projectm.utils.PreferencesManager


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        val navController = navHostFragment!!.navController

        val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupWithNavController(
            mBinding.toolbar, navController, appBarConfiguration
        )
        setSupportActionBar(mBinding.toolbar)

        navHostFragment.navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, arguments: Bundle? ->
            when (destination.id) {
                R.id.sigInFragment, R.id.signUpFragment -> {
                    mBinding.toolbar.isVisible = false
                }
            }
        }

        if (PreferencesManager(this).getPreferences()!!.getBoolean(IS_SIGN_IN, false)) {
            val graph = navHostFragment.navController
                .navInflater.inflate(R.navigation.nav_home)
            graph.startDestination = R.id.userListFragment
            navHostFragment.navController.graph = graph
        }

    }

    fun onCall() {
        Log.e("tttttt", "oncall")
    }
}