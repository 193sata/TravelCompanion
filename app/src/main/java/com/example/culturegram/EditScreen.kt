package com.example.culturegram

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import java.io.File
import java.io.IOException

class EditScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(
        navController: NavHostController,
        heritageName: String,
        imageList: List<String>,  // 画像パスのリスト
        onImageSelected: (String) -> Unit
    ) {
        val context = LocalContext.current  // Contextを取得
        var selectedImage by remember { mutableStateOf(imageList.firstOrNull()) }
        var imageSelected by remember { mutableStateOf(false) }  // 選択されたかどうかのフラグ

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = heritageName, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            selectedImage?.let { selected ->
                                if (selected != "no_image.jpg") {  // No-img以外の場合のみ保存
                                    onImageSelected(selected)
                                    imageSelected = true
                                }
                            }

                            // 選択された画像がある場合にMainScreen経由でStatus画面に戻る
                            if (imageSelected) {
                                navController.navigate("main?screen=status")  // Status画面に遷移
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = "決定")
                        }
                    }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // 選択した画像を大きく表示
                    selectedImage?.let { imagePath ->
                        val imageFile = File(imagePath)
                        if (imageFile.exists()) {
                            val bitmap = loadRotatedBitmap(imageFile)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = heritageName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.no_image),
                                contentDescription = "No Image",
                                modifier = Modifier.fillMaxWidth().height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),   // 列数を固定して横幅いっぱいに表示
                        contentPadding = PaddingValues(0.dp),  // 余白をなくす
                        modifier = Modifier.fillMaxSize()  // グリッド全体を最大サイズに
                    ) {
                        items(imageList) { imagePath ->
                            val imageFile = File(imagePath)

                            Box(
                                modifier = Modifier
                                    .border(0.5.dp, Color.Black)
                                    .aspectRatio(1f)  // 正方形に保つ
                                    .fillMaxWidth()   // 横幅いっぱいに広げる
                                    .clickable {
                                        selectedImage = imagePath  // 画像を選択
                                    }
                            ) {
                                if (imageFile.exists()) {
                                    val bitmap = loadRotatedBitmap(imageFile)
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "選択画像",
                                        modifier = Modifier.fillMaxSize(),  // 画像をタイル全体にフィット
                                        contentScale = ContentScale.Crop   // 画像をタイルにフィットさせる
                                    )
                                } else {
                                    // デフォルト画像を表示
                                    Image(
                                        painter = painterResource(id = R.drawable.no_image),
                                        contentDescription = "No Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // 画像リストが少ない場合、NO-imgを1つ表示
                        if (imageList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .border(0.5.dp, Color.Black)
                                        .aspectRatio(1f)
                                        .fillMaxWidth()  // 横幅いっぱいに広げる
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.no_image),
                                        contentDescription = "No Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // EXIFデータに基づいて画像を回転させる関数
    private fun loadRotatedBitmap(imageFile: File): Bitmap {
        val bitmap = BitmapFactory.decodeFile(imageFile.path)
        var rotatedBitmap = bitmap

        try {
            val exif = androidx.exifinterface.media.ExifInterface(imageFile.path)
            val orientation = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL)

            rotatedBitmap = when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rotatedBitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}