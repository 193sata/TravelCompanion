package com.example.culturegram

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class Map {

  @Composable
  fun Content() {
    // カメラポジションを設定（例えば、東京タワーの位置）
    val tokyoTower = LatLng(35.6586, 139.7454)
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(tokyoTower, 10f)
    }

    // Google Mapを表示
    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState
    )
  }
}
