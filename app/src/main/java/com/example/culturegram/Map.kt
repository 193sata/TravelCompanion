package com.example.culturegram

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Map {
  var currentLocation by mutableStateOf(LatLng(32.81449335495487, 130.72729505562057)) // デフォルトの位置（熊本）
  private var heritages: MutableList<Heritage> = mutableListOf()
  private var mapPins = MapPins()

  @Composable
  fun Content() {
    val context = LocalContext.current
    val gps = remember { GPS(context) } // GPSクラスを記憶する
    //var currentLocation by remember { mutableStateOf<LatLng?>(null) } // 現在地を保持するための状態

    // GPSから位置情報を取得する（非同期）
    gps.GetCurrentLocation()

    // GPSの位置情報が更新されたらcurrentLocationを更新
    gps.currentLocation.value?.let { location ->
      currentLocation = LatLng(location.latitude, location.longitude)
    }

    mapPins.setUserPosition(currentLocation.latitude, currentLocation.longitude)
    mapPins.readCsvFile(context)

    try {
      heritages = mapPins.getHeritages(10000000000.0) // 全て入るように

    } catch (e: IOException) {
      e.printStackTrace()
    }

    // 一番近い避難所を特定
    val closestShelter = heritages.minByOrNull { it.distance }

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
          //snippet = "${heritage.type} - レビュー: ${heritage.review}点",
          icon = when {
            heritage.yet == 1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)  // 行った場所
            heritage == closestShelter -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)  // 現在地
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)  // 行ってない場所
          }
        )
      }


    }
  }
}
