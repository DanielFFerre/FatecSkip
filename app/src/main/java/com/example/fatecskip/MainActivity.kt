package com.example.fatecskip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fatecskip.ui.navigation.AppNavHost
import com.example.fatecskip.ui.theme.FatecSkipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FatecSkipTheme {
                AppNavHost()
            }
        }
    }
}
