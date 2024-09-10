package com.example.culturegram

import android.graphics.Camera
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.culturegram.ui.theme.CultureGramTheme
import com.google.ai.client.generativeai.common.shared.Content

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CultureGramTheme {
                // ナビゲーションの設定
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main", // 最初に表示する画面をMainScreenに設定
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // MainScreen
                        composable("main") {
                            val mainScreen = MainScreen()
                            mainScreen.Content(navController)  // MainScreenのContentを表示
                        }
                        // Camera with heritageName as an argument
                        composable(
                            route = "camera/{heritageName}",
                            arguments = listOf(navArgument("heritageName") { defaultValue = "Unknown" })
                        ) { backStackEntry ->
                            val heritageName = backStackEntry.arguments?.getString("heritageName")
                            val camera = Camera()
                            camera.Content(heritageName ?: "Unknown", navController)   // CameraのContentにheritageNameとnavControllerを渡す
                        }
                    }
                }
            }
        }
    }
}