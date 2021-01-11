package com.nurbk.ps.projectm.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.iid.FirebaseInstanceId
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.ActivityMainBinding
import com.nurbk.ps.projectm.others.IS_SIGN_IN
import com.nurbk.ps.projectm.utils.PreferencesManager
import kotlinx.android.synthetic.main.activity_main.*


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
            mBinding.toolbar.isVisible = false
        }

    }

    override fun onBackPressed() {

        val navigationController = nav_host_fragment.findNavController()
        if (navigationController.currentDestination?.id == R.id.userListFragment) {
            finish()
        } else {
            super.onBackPressed()
        }

    }
}