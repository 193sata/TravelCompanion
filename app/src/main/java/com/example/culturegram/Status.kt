package com.example.culturegram

import android.content.Context
import android.os.FileObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

//class MyFileObserver(
//    private val context: Context,
//    private val filename: String,
//    private val onFileChanged: () -> Unit
//) : FileObserver(File(context.filesDir, filename).absolutePath, MODIFY) {
//    override fun onEvent(event: Int, path: String?) {
//        if (event == MODIFY) {
//            onFileChanged()
//        }
//    }
//}

class Status {
    @Composable
    fun Content(){
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            saveCsvFileToInternalStorage(
                context = context,
                data = readCsvFile(context),
                fileName = "internalCsvFile"
            ) }
        var heritageCsv by remember { mutableStateOf(loadCsvFileFromInternalStorage(context,"internalCsvFile")) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                println("3 seconds")
                heritageCsv = loadCsvFileFromInternalStorage(context,"internalCsvFile")
            }
        }
//        lateinit var fileObserver: MyFileObserver
//        val filename = "internalCsvFile"
//        fileObserver = MyFileObserver(context, filename) {
//            heritageCsv = loadCsvFileFromInternalStorage(context,filename)
//        }
//        fileObserver.startWatching()
//        override fun onDestroy() {
//            super.onDestroy()
//            // 監視を停止
//            fileObserver.stopWatching()
//        }

        val sample: List<List<String>> = listOf(
            listOf("法隆寺地域の仏教建造物", "0"),
            listOf("姫路城", "1"),
            listOf("古都京都の文化財", "0"),
            listOf("白川郷・五箇山の合掌造り集落", "1"),
            listOf("原爆ドーム", "0"),
            listOf("厳島神社", "1"),
            listOf("古都奈良の文化財", "0"),
            listOf("日光の社寺", "1"),
            listOf("琉球王国のグスク及び関連遺産群", "0"),
            listOf("紀伊山地の霊場と参詣道", "1"),
            listOf("石見銀山遺跡とその文化的景観", "0"),
            listOf("平泉‐仏国土（浄土）を表す建築・庭園及び考古学的遺跡群‐", "1"),
            listOf("富士山‐信仰の対象と芸術の源泉‐", "0"),
            listOf("富岡製糸場と絹産業遺産群", "1"),
            listOf("明治日本の産業革命遺産", "0"),
            listOf("ル・コルビュジエの建築作品‐近代建築運動への顕著な貢献‐", "1"),
            listOf("「神宿る島」宗像・沖ノ島と関連遺産群", "0"),
            listOf("長崎と天草地方の潜伏キリシタン関連遺産", "1"),
            listOf("百舌鳥・古市古墳群‐古代日本の墳墓群‐", "0"),
            listOf("北海道・北東北の縄文遺跡群", "1"),
            listOf("佐渡島の金山", "0")
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painterResource(id = R.drawable.white_00008),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.fillMaxSize()) {
                Row {
                    UserChara()
                    Ratio(sample = heritageCsv)
                }
                HorizontalDivider(thickness = 24.dp)
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for ((j, i) in heritageCsv.withIndex()) {
                        if (i[3] == "1"){
                            Row(modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Blue.copy(alpha = 0.2f))) {
                                ListImage(i = i, j = j, context)
                                ListCheck(i = i)
                            }
                        }
                        else if (i[3] == "0"){
                            Row(modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.LightGray.copy(alpha = 0.2f))) {
                                ListImage(i = i, j = j, context)
                                ListCheck(i = i)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(5.dp))
                    }
                }
            }
        }
    }

    //    object Database {
    @Composable
    fun UserChara() {
        Image(
            painter = painterResource(id = R.drawable.android_logo),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp, 200.dp)
                .padding(horizontal = 24.dp, vertical = 5.dp)
        )
    }

    @Composable
    fun Ratio(sample: List<List<String>>) {
        var sum = 0
        for (i in sample) {
            if (i[3] == "1") {
                sum++
            }
        }
        Text(
            text = "$sum/26",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 50.dp)
        )
    }

    @Composable
    fun ListCheck(i: List<String>) {
        val check: String
        if (i[3] == "1") {
            check = "〇"
        } else {
            check = "ｘ"
        }
        Text(
            text = i[0] + " : " + check,
            fontSize = 24.sp
        )
    }

    //設計途中
    @Composable
    fun ListImage(i: List<String>, j: Int, context: Context) {
        val image: Int
        if (i[3] == "1") {
            image = R.drawable.mountain_00003
        } else {
            image = R.drawable.sea_ocean_00001
        }
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp, 60.dp)
                .clickable {
                    accessCsvFile(j = j, context)
                }
        )
    }


    private fun saveCsvFileToInternalStorage(
        context: Context,
        data: List<List<String>>,
        fileName: String
    ) {
        val file = File(context.filesDir, fileName)
        file.delete()
        FileOutputStream(file,false).use { outputStream ->
            val csvContent = data.joinToString("\n") { it.joinToString(",") }
            outputStream.write(csvContent.toByteArray())
        }
    }

    private fun loadCsvFileFromInternalStorage(
        context: Context,
        fileName: String
    ): MutableList<MutableList<String>> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // ファイルが存在しない場合は空のリストを返すか、新しいファイルを作成するなどの処理を行う
            return mutableListOf()
        }
        return file.readLines().take(21)
            .map { it.split(",").map(String::trim).toMutableList() }
            .toMutableList()
    }

    private fun accessCsvFile(j: Int, context: Context) {
        val data = loadCsvFileFromInternalStorage(context, "internalCsvFile")
        val row = data.getOrNull(j)
        if (row != null && row.size > 3) {
            println(row[3])
            if (row[3] == "0") {
                row[3] = "1"
                println("changing...")
            }
            else{
                row[3] = "0"
            }
            println("saved successfully.")
        }
        saveCsvFileToInternalStorage(context, data, "internalCsvFile")
    }


    private fun readCsvFile(context: Context): List<List<String>> {
        val csvLines = mutableListOf<List<String>>()
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("csvData.csv")
            BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                lines.drop(1).take(21).forEach { line ->
                    // 各行をカンマで分割してList<String>に変換
                    val row = line.split(",").map { it.trim() }
                    csvLines.add(row)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return csvLines
    }
}
