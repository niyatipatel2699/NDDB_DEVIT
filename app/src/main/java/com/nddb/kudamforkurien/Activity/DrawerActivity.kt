package com.nddb.kudamforkurien.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nddb.kudamforkurien.Activity.ui.login.LoginActivity
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.databinding.ActivityDrawerBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import com.nddb.kudamforkurien.backgroundservice.MotionService

import com.nddb.kudamforkurien.data.room.DatabaseBuilder
import com.nddb.kudamforkurien.data.room.DatabaseHelper
import com.nddb.kudamforkurien.data.room.DatabaseHelperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DrawerActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(this))

        setSupportActionBar(binding.appBarDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        binding.llLogout.setOnClickListener {
            when { //If drawer layout is open close that on back pressed
                binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                // Logout call..
                // APPLY FONT https://stackoverflow.com/questions/30668346/how-to-set-custom-typeface-to-items-in-navigationview
            }

            showAlertDialog()
//            MySharedPreferences.getMySharedPreferences()!!.isLogin = false
//            val i = Intent(this,LoginActivity :: class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(i)
        }

        var background =
            AppCompatResources.getDrawable(this, R.drawable.navigation_item_background_rounded_rect)
        if (background != null) {
            val tint = AppCompatResources.getColorStateList(
                this, R.color.orange
            )

            background = DrawableCompat.wrap(background.mutate())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background.setTintList(tint)
            } else {
                DrawableCompat.setTintList(background, tint)
            }
        }

        navView.itemBackground = background

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_drawer) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_event,
                R.id.nav_walked_history,
                R.id.nav_change_language,
                R.id.nav_gallery,
                R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.drawer, menu)
//        return true
//    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.are_you_sure_want_logout))
            .setPositiveButton(
                getString(R.string.label_yes)
            ) { dialogInterface, i ->
                dialogInterface.dismiss()
                MySharedPreferences.getMySharedPreferences()!!.isLogin = false
                //MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected = false
                val i = Intent(this,LoginActivity :: class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                //AutoStartService.stop
               /* val prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", MODE_PRIVATE)
                val editor = prfs.edit()
                editor.putString("steps", "0")
                editor.apply()*/
                var sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
                sharedPreferences.edit().putInt(MotionService.KEY_STEPS, 0).apply()
                //Database.getInstance(this).clearTableData()
                GlobalScope.launch (Dispatchers.Main) {
                dbHelper.deleteSteps()
                }
               /* val serviceAdmin = ServiceAdmin()
                serviceAdmin.stopService(this)*/
                val intent = Intent(activity, MotionService::class.java)
                intent.putExtra("stopped", true)
                activity?.startService(intent)

            }
            .setNegativeButton(
                getString(R.string.label_no)
            ) { dialogInterface, i -> dialogInterface.dismiss() }.show()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when { //If drawer layout is open close that on back pressed
            binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                super.onBackPressed() //If drawer is already in closed condition then go back
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // https://stackoverflow.com/a/58382422
        val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
        if (navHostFragment != null) {
            val childFragments = navHostFragment.childFragmentManager.fragments
            childFragments.forEach { fragment ->
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }

    }


}