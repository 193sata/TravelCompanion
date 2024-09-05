package com.example.culturegram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

class MainScreen() {
    @Composable
    fun Content(navController: NavController) {
        // メインコンテンツを動的に変更するための変数
        val contentState = remember { mutableStateOf<@Composable () -> Unit>({ Text("Initial Content") }) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ヘッダー
            Header { buttonText ->
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
            Footer(navController) { button ->
                when (button) {
                    "shorts" -> {
                        // ChatScreenのコンテンツを表示
                        contentState.value = { Shorts().Content() }
                    }
                    "status" -> {
                        // ChatScreenのコンテンツを表示
                        contentState.value = { Status().Content() }
                    }
                }
            }
        }
    }

    @Composable
    fun Header(onButtonClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End // 右端に配置
        ) {
            IconButton(onClick = { onButtonClick("Settings") }) {
                Icon(
                    imageVector = Icons.Default.Settings, // Material Iconsの設定マークを使用
                    contentDescription = "Settings Icon"
                )
            }
        }
    }

    @Composable
    fun Footer(navController: NavController, onButtonClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // カメラアイコン
            IconButton(onClick = { navController.navigate("camera") }) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,  // カメラアイコン
                    contentDescription = "Camera Icon"
                )
            }

            // ショートアイコン
            IconButton(onClick = { onButtonClick("shorts") }) {
                Icon(
                    imageVector = Icons.Default.Folder,  // ショートアイコン
                    contentDescription = "Shorts Icon"
                )
            }

            // プロフィールアイコン
            IconButton(onClick = { onButtonClick("status") }) {
                Icon(
                    imageVector = Icons.Default.Person,  // プロフィールアイコン
                    contentDescription = "Status Icon"
                )
            }
        }
    }
}
