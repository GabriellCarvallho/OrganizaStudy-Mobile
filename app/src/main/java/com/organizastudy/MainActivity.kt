package com.organizastudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.organizastudy.navigation.AppNavigation
import com.organizastudy.ui.theme.OrganizaStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrganizaStudyTheme {
                AppNavigation()
            }
        }
    }
}
