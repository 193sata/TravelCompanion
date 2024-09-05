package com.example.culturegram

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class Map {

  @Composable
  fun Content() {
    val context = LocalContext.current
    val gps = remember { GPS(context) } // GPSクラスを記憶する
    var currentLocation by remember { mutableStateOf<LatLng?>(null) } // 現在地を保持するための状態

    // GPSから位置情報を取得する（非同期）
    gps.GetCurrentLocation()

    // 位置情報が取得されたらcurrentLocationを更新
    gps.currentLocation.value?.let { location ->
      currentLocation = LatLng(location.latitude, location.longitude)
    }

    // 現在地が取得されるまでマップの初期表示を保留
    if (currentLocation != null) {
      // カメラポジションを設定
      val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
      }

      // Google Mapの表示
      GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
      )
    }
  }
}