package com.example.culturegram

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

class MainScreen {

    @Composable
    fun Content(navController: NavController) {
        // 背景色を動的に変更するための状態
        val backgroundColor = remember { mutableStateOf(Color.White) }

        // メインコンテンツを動的に変更するための変数
        val contentState = remember { mutableStateOf<@Composable () -> Unit>({ Map().Content(navController) }) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor.value)  // 動的に背景色を設定

        ) {
            // 背景色に応じてアイコンの色を切り替える
            val iconColor = if (backgroundColor.value == Color.White) Color.Black else Color.White

            // ヘッダー
            Header(iconColor) { buttonText ->
                contentState.value = { Text("Header Button Clicked: $buttonText") }
            }

            // メインコンテンツ
            Box(
                modifier = Modifier
                    .weight(1f)  // 残りの領域を占有
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                contentState.value()  // 動的に変更されるコンテンツを表示
            }

            // フッター
            Footer(navController, iconColor) { button ->
                when (button) {
                    "map" -> {
                        val map = Map()
                        contentState.value = { map.Content(navController) }
                        backgroundColor.value = Color.White // Map画面では背景を白に
                    }
                    "shorts" -> {
                        contentState.value = { Shorts().Content() }
                        backgroundColor.value = Color.Black // Shorts画面では背景を黒に
                    }
                    "status" -> {
                        contentState.value = { Status().Content() }
                        backgroundColor.value = Color.White // Status画面では背景を白に
                    }
                }
            }
        }
    }

    @Composable
    fun Header(iconColor: Color, onButtonClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { onButtonClick("Settings") }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Icon",
                    tint = iconColor  // アイコンの色を動的に設定
                )
            }
        }
    }

    @Composable
    fun Footer(navController: NavController, iconColor: Color, onButtonClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // カメラアイコン
            IconButton(onClick = { navController.navigate("camera") }) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Icon",
                    tint = iconColor  // 動的にアイコンの色を設定
                )
            }

            // マップアイコン
            IconButton(onClick = { onButtonClick("map") }) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Map Icon",
                    tint = iconColor  // 動的にアイコンの色を設定
                )
            }

            // ショートアイコン
            IconButton(onClick = { onButtonClick("shorts") }) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Shorts Icon",
                    tint = iconColor  // 動的にアイコンの色を設定
                )
            }

            // プロフィールアイコン
            IconButton(onClick = { onButtonClick("status") }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Status Icon",
                    tint = iconColor  // 動的にアイコンの色を設定
                )
            }
        }
    }
}
