package com.loopcorp.loophero

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    var navController: NavHostController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivitiesIfAvailable(application)
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface() {
                    navController = rememberNavController()

                    NavHost(
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                        navController = navController!!,
                        startDestination = if (permissionsGranted(context = this)) "configurationScreen" else "permissionScreen"
                    ) {
                        composable("permissionScreen") {
                            PermissionScreen(navController = navController!!)
                        }
                        composable("successScreen") {
                            SuccessScreen(navController = navController!!)
                        }
                        composable("configurationScreen") {
                            ConfigurationScreen(navController = navController!!)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (navController != null) {
            if (permissionsGranted(context = this)) {
                navController!!.popBackStack()
                val firstSetup = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getBoolean("firstSetup", true)

                navController!!.navigate(if (firstSetup) "permissionScreen" else "configurationScreen")
            }
        }
    }
}

private fun permissionsGranted(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkAccessibilityService(context) && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        checkAccessibilityService(context)
    }
