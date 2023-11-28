package com.example.powermanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.theme.PowerManagerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerManagerTheme {
                PowerManagerApp()
            }
        }
    }
}
