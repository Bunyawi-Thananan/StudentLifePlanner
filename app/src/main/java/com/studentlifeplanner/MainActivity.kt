package com.studentlifeplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.studentlifeplanner.ui.navigation.AppNavigation
import com.studentlifeplanner.ui.theme.StudentLifePlannerTheme
import com.studentlifeplanner.utils.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = PreferenceManager(this)
        val isLoggedIn = preferenceManager.isLoggedIn()

        setContent {
            StudentLifePlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(isLoggedIn = isLoggedIn)
                }
            }
        }
    }
}