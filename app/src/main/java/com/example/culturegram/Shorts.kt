package com.example.culturegram

import GetImages
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import kotlin.math.abs


class Shorts {
    @Composable
    fun Content() {
        val context = LocalContext.current

        // GetImagesクラスのContentメソッドを呼び出して画像のリストを取得
        val getImages = GetImages()
        val images = getImages.Content(context)

        // 表示する画像が存在しない場合に対応
        if (images.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("No images found.")
            }
            return
        }

        var currentIndex by remember { mutableStateOf(0) }
        var nextIndex by remember { mutableStateOf((currentIndex + 1) % images.size) }
        var dragOffset by remember { mutableStateOf(0f) }
        var directionLocked by remember { mutableStateOf(false) }
        var isTextVisible by remember { mutableStateOf(true) }  // 初期状態でテキストを表示
        val screenHeight = 800f  // 仮の画面高さ

        // アニメーション化された透明度 (0fから1fまで)
        val alpha by animateFloatAsState(targetValue = if (isTextVisible) 1f else 0f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (dragOffset > screenHeight / 2) {
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
                            } else if (dragOffset < -screenHeight / 2) {
                                currentIndex = (currentIndex + 1) % images.size
                            }
                            dragOffset = 0f  // オフセットをリセット
                            directionLocked = false  // 次のドラッグに備えて方向をリセット
                            isTextVisible = true  // 画像切り替え後に再度テキストを表示
                        },
                        onDrag = { _, dragAmount ->
                            dragOffset += dragAmount.y  // ドラッグ量をオフセットに反映

                            if (!directionLocked) {
                                nextIndex = if (dragAmount.y > 0) {
                                    if (currentIndex > 0) currentIndex - 1 else images.size - 1
                                } else {
                                    (currentIndex + 1) % images.size
                                }
                                directionLocked = true  // 一度方向を決定したらドラッグ終了まで固定
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // タップされたときにテキストの表示状態をトグルする
                            isTextVisible = !isTextVisible
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // 現在の画像を表示し、画像名を下部に表示
            Box {
                Image(
                    painter = rememberImagePainter(data = images[currentIndex].first),  // 画像パスを指定
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
                // フェードイン・アウトするテキスト
                if (alpha > 0f) {
                    Text(
                        text = images[currentIndex].second,  // 画像名
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)  // テキストの周りにパディングを追加
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))  // 半透明の黒い背景と角丸
                            .padding(8.dp)  // 背景内のテキストに追加パディング
                            .alpha(alpha),  // フェードイン・アウトのための透明度
                        color = Color.White,  // 白い文字色
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 次の画像を表示
            Box {
                Image(
                    painter = rememberImagePainter(data = images[nextIndex].first),  // 画像パスを指定
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
}
