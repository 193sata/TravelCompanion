package com.example.culturegram

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Circle
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
    var selectedHeritage by remember { mutableStateOf<Heritage?>(null) } // 選択された遺産を保存する状態

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
      position = CameraPosition.fromLatLngZoom(currentLocation, 7f) // 初期位置を設定
    }

    // Google Mapの表示
    Box(modifier = Modifier.fillMaxSize()) {
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
          val isInside = heritagesInside.contains(heritage) // heritagesInsideに含まれているか確認

          Marker(
            state = MarkerState(position = LatLng(heritage.latitude, heritage.longitude)),
            title = heritage.name, // デフォルトのタイトル（カスタムUIで表示するためここでの自動表示は抑制）
            icon = when {
              isInside -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)  // heritagesInsideに含まれている場合は黄色
              heritage.yet == 1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)  // 行った場所
              else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)  // 行ってない場所
            },
            onClick = {
              // クリック時にカスタムUIを表示する
              if (isInside) {
                selectedHeritage = heritage // カスタムUIで表示するための遺産を設定
              } else {
                selectedHeritage = null // クリックされた遺産がisInsideでない場合は選択をリセット
              }
              false // カスタムの動作を実行し、デフォルトの動作は無効化
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

      // カメラアイコンを表示する
      selectedHeritage?.let { heritage ->
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)  // 画面の中央下方に配置
            .padding(bottom = 30.dp)  // 画面下から少し上に配置
        ) {
          // 背景の丸いボックス（半透明）
          Box(
            modifier = Modifier
              .size(100.dp)  // 背景のサイズを設定
              .background(Color.Gray.copy(alpha = 0.5f), shape = CircleShape)  // 半透明の丸い背景を設定
          )

          // カメラアイコンのボタン（背景の上に配置）
          IconButton(
            onClick = {
              // カメラアイコンが押されたら"camera"画面へ遷移
              navController.navigate("camera/${heritage.name}")
            },
            modifier = Modifier.size(100.dp)  // アイコンボタンのサイズ
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "カメラアイコン",
              tint = Color.White,  // アイコンの色を白に設定
              modifier = Modifier.size(50.dp)  // アイコン自体のサイズ
            )
          }
        }
      }

    }
  }
}
