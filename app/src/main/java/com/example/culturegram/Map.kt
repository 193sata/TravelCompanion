package com.example.culturegram

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Circle
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import java.io.IOException

class Map {
  var currentLocation by mutableStateOf(LatLng(32.81449335495487, 130.72729505562057)) // デフォルトの位置（熊本）
  private var heritages: MutableList<Heritage> = mutableListOf()
  private var heritagesInside: MutableList<Heritage> = mutableListOf()
  private var mapPins = MapPins()
  private var allowedR = 100000.0

  @Composable
  fun Content(navController: NavController) {
    val context = LocalContext.current
    val gps = remember { GPS(context) } // GPSクラスを記憶する

    // GPSから位置情報を取得する（非同期）
    gps.GetCurrentLocation()

    // GPSの位置情報が更新されたらcurrentLocationを更新
    gps.currentLocation.value?.let { location ->
      currentLocation = LatLng(location.latitude, location.longitude)
    }

    mapPins.setUserPosition(currentLocation.latitude, currentLocation.longitude)
    mapPins.readCsvFile(context)

    try {
      heritages = mapPins.getHeritages() // 全て入るように
      heritagesInside = mapPins.getHeritagesInside(allowedR)
    } catch (e: IOException) {
      e.printStackTrace()
    }

    // カメラポジションを設定
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(35.0, 139.0), 7f) // 初期位置を設定
    }

    // Google Mapの表示
    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState
    ) {
      // 現在地のピンを立てる（赤色）
      Marker(
        state = MarkerState(position = LatLng(currentLocation.latitude, currentLocation.longitude)),
        title = "現在地",
        snippet = "現在地",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
      )

      // 遺産のピンを立てる
      heritages.forEach { heritage ->
        Marker(
          state = MarkerState(position = LatLng(heritage.latitude, heritage.longitude)),
          title = heritage.name,
          icon = when {
            heritage.yet == 1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)  // 行った場所
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)  // 行ってない場所
          }
        )
      }

      // サークル内の遺産のピンを立てる
      heritagesInside.forEach { heritage ->
        Marker(
          state = MarkerState(position = LatLng(heritage.latitude, heritage.longitude)),
          title = heritage.name,
          icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
          onClick = {
            // マーカーをタップしたときに"camera"画面に遷移し、heritage.nameを渡す
            navController.navigate("camera/${heritage.name}")
            true
          }
        )
      }

// 現在地を中心に半径500mの円を描画
      Circle(
        center = LatLng(currentLocation.latitude, currentLocation.longitude),
        radius = allowedR, // 半径500m
        strokeColor = Color.Blue, // 円の輪郭の色 (Compose用のColor.Blueをそのまま使用)
        fillColor = Color(0x220000FF), // 透明な青 (Colorクラスをそのまま使用)
        strokeWidth = 2f // 円の輪郭の太さ
      )
    }
  }
}
