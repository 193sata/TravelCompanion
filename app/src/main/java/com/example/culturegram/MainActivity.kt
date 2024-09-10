package com.example.culturegram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culturegram.ui.theme.CultureGramTheme

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
                        startDestination = "main", // 最初に表示する画面をMainScreenに変更
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // MainScreen
                        composable("main") {
                            val mainScreen = MainScreen()
                            mainScreen.Content(navController)  // MainScreenのContentを表示
                        }
                        // Camera
                        composable("camera") {
                            val camera = Camera()
                            camera.Content()   // cameraのContentを表示
                        }
                        // Shorts
                        /*composable("shorts") {
                            val shorts = Shorts()
                            shorts.Content()  // shortsのContentを表示
                        }
                        // Status
                        composable("status") {
                            val status = Status()
                            status.Content()  // statusのContentを表示
                        }*/

                    }
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(navController: NavController) {
    // ボタンを押すとMainScreenに遷移する
    Button(onClick = { navController.navigate("main") }) {
        Text(text = "Go to Main Screen")
    }
}
