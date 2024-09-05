package com.example.culturegram

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
//import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
//import coil.compose.AsyncImage
import java.io.File
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors

class Camera {

    @Composable
    fun Content() {
        val context = LocalContext.current
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        var hasCameraPermission by remember { mutableStateOf(false) }

        // カメラパーミッションのランチャー
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // カメラパーミッションの確認
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                hasCameraPermission = true
            } else {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }

        if (hasCameraPermission) {
            // パーミッションが許可された場合、カメラプレビューと撮影機能を表示
            Box(modifier = Modifier.fillMaxSize()) {
                // カメラプレビューの表示
                CameraPreview(
                    onImageCaptured = { uri ->
                        imageUri = uri
                        Toast.makeText(context, "Image captured: $uri", Toast.LENGTH_SHORT).show()
                    },
                    onError = { message ->
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                )

                // 写真撮影ボタンをカメラプレビューの上に重ねる
                Button(
                    onClick = {
                        // ボタンクリック時の処理
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Take Picture")
                }
            }
        } else {
            // パーミッションが拒否された場合のメッセージや代替UI
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required to use this feature.")
            }
        }
    }

    @Composable
    fun CameraPreview(
        onImageCaptured: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = androidx.camera.view.PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()
                val preview = CameraPreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                    onError("Camera initialization failed.")
                }

                previewView
            }
        )

        Button(
            onClick = {
                val photoFile = File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture?.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onImageCaptured(Uri.fromFile(photoFile))
                        }

                        override fun onError(exc: ImageCaptureException) {
                            onError(exc.message ?: "Image capture failed")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Take Picture")
        }
    }
}