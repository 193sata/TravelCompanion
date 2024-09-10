package com.example.culturegram

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
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

    // GPSの位置情報が更新されたらcurrentLocationを更新
    gps.currentLocation.value?.let { location ->
      currentLocation = LatLng(location.latitude, location.longitude)
    }

    // カメラポジションを設定
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(35.0, 139.0), 15f) // 初期位置を設定
    }

    // Google Mapの表示
    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState
    ) {
      // currentLocationがnullでなければ、動的にマーカーを表示
      currentLocation?.let { location ->
        Marker(
          state = MarkerState(position = location), // 現在地の座標
          title = "You are here"  // マーカーのタイトル
        )
        // カメラを現在地に移動
        LaunchedEffect(location) {
          cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
        }
      }
    }
  }
}
