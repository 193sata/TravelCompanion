package com.example.culturegram

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapPins {
    private var heritages: MutableList<Heritage> = mutableListOf()
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0

    // 地球の半径 (メートル)
    private val earthRadius = 6371000.0

    public fun setUserPosition(tmpUserLatitude: Double, tmpUserLongitude: Double){
        userLatitude = tmpUserLatitude
        userLongitude = tmpUserLongitude
    }

    // 距離計算用関数 (ハーバサインの公式を使用)
    //ここは一番評価の高いものを表示
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    public fun getHeritages(distanceLimit: Double):MutableList<Heritage>{
        val result: MutableList<Heritage> = mutableListOf()
        for(i in heritages){
            if(i.distance <= distanceLimit) result.add(i)
        }

        return result
    }

    fun readCsvFile(context: Context) {
        try {
            // assetsフォルダ内のdata.csvファイルを開く
            val inputStream = context.assets.open("csvData.csv")
//            val inputStream = context.resources.openRawResource(R.raw.shelters_kuma)
//            val reader = BufferedReader(InputStreamReader(inputStream))
            //val inputStream = context.resources.openRawResource(R.raw.ramen_shop_data)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val header = reader.readLine() // ヘッダーを取得
            val headerTokens = header.split(",")

            // 必要なカラムのインデックスを取得
            val nameIndex = headerTokens.indexOf("名前")
            val latitudeIndex = headerTokens.indexOf("緯度")
            val longitudeIndex = headerTokens.indexOf("経度")
            val yetIndex = headerTokens.indexOf("bool値")

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // CSVファイルの各行をログに出力
                Log.d("CSVReader", line.toString())

                // 各列のデータを取得したい場合
//                val columns = line?.split(",")
//                val tmpName = columns?.get(0).toString()
//                val tmpLatitude = columns?.get(1).toString().toDouble()
//                val tmpLongitude = columns?.get(2).toString().toDouble()
//                val tmpType = columns?.get(3).toString()
//                val tmpReview = columns?.get(4).toString().toInt()

                val tokens = line!!.split(",")
                val name = tokens.getOrNull(nameIndex)
                val latitude = tokens.getOrNull(latitudeIndex)?.toDoubleOrNull()
                val longitude = tokens.getOrNull(longitudeIndex)?.toDoubleOrNull()
                val yet = tokens.getOrNull(yetIndex)?.toIntOrNull()

                // 例として、各列をログに出力
                Log.d("CSVReader", "Column 1: $name")
                heritages.add(Heritage(
                    name.toString(),
                    latitude.toString().toDouble(),
                    longitude.toString().toDouble(),
                    //calcDistance(tmpLatitude, tmpLongitude),
                    calculateDistance(latitude.toString().toDouble(), longitude.toString().toDouble(), userLatitude, userLongitude),
                    yet.toString().toInt()
                ))
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}