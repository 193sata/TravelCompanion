package com.example.culturegram

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlin.math.abs
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp


class Shorts {
    @Composable
    fun Content() {
        val images = listOf(
            R.drawable.castle,
            R.drawable.maiko,
            R.drawable.zyoumon,
            R.drawable.mountain,
            R.drawable.river
        )

        var currentIndex by remember { mutableStateOf(0) }
        var nextIndex by remember { mutableStateOf((currentIndex + 1) % images.size) }
        var dragOffset by remember { mutableStateOf(0f) }
        var directionLocked by remember { mutableStateOf(false) }
        val screenHeight = 800f  // 仮の画面高さ

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // 指を離したときに次の画像に切り替え
                            if (dragOffset > screenHeight / 2) {
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
                            } else if (dragOffset < -screenHeight / 2) {
                                currentIndex = (currentIndex + 1) % images.size
                            }
                            dragOffset = 0f  // オフセットをリセット
                            directionLocked = false  // 次のドラッグに備えて方向をリセット
                        },
                        onDrag = { _, dragAmount ->
                            dragOffset += dragAmount.y  // ドラッグ量をオフセットに反映

                            if (!directionLocked) {
                                // ドラッグの方向に基づいて次の画像を決定（スクロール方向がロックされるまで一度だけ実行）
                                nextIndex = if (dragAmount.y > 0) {
                                    if (currentIndex > 0) currentIndex - 1 else images.size - 1
                                } else {
                                    (currentIndex + 1) % images.size
                                }
                                directionLocked = true  // 一度方向を決定したらドラッグ終了まで固定
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // 現在の画像を表示（指の動きに合わせて移動）
            Image(
                painter = painterResource(id = images[currentIndex]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))  // 4つ角を16dpの丸みでクリップ
                    .graphicsLayer(
                        translationY = dragOffset,  // 指の動きに合わせて画像を移動
                        alpha = 1f - abs(dragOffset) / screenHeight  // スクロールで徐々にフェードアウト
                    ),
                contentScale = ContentScale.Crop
            )

            // 次の画像を表示（指の動きに合わせて反対側から移動）
            Image(
                painter = painterResource(id = images[nextIndex]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))  // 次の画像も4つ角を16dpの丸みでクリップ
                    .graphicsLayer(
                        translationY = if (dragOffset > 0) dragOffset - screenHeight else dragOffset + screenHeight,  // 新しい画像を画面外から移動
                        alpha = abs(dragOffset) / screenHeight  // スクロールで徐々にフェードイン
                    ),
                contentScale = ContentScale.Crop
            )
        }
    }
}
