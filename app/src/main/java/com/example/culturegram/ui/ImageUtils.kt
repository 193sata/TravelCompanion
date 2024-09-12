package com.example.culturegram

import android.content.Context
import android.content.SharedPreferences

// 保存された画像のパスを取得する関数
fun getSavedImagePath(context: Context, heritageName: String): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("HeritageImages", Context.MODE_PRIVATE)
    return sharedPreferences.getString(heritageName, null)
}

// 画像パスを保存する関数
fun saveImagePath(context: Context, heritageName: String, imagePath: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("HeritageImages", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(heritageName, imagePath)
    editor.apply()
}