package com.example.culturegram

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Status{
    @Composable
    fun Content() {
        val sample:List<List<String>> = listOf(listOf("法隆寺地域の仏教建造物","0"),
            listOf("姫路城","1"),
            listOf("古都京都の文化財","0"),
            listOf("白川郷・五箇山の合掌造り集落","1"),
            listOf("原爆ドーム","0"),
            listOf("厳島神社","1"),
            listOf("古都奈良の文化財","0"),
            listOf("日光の社寺","1"),
            listOf("琉球王国のグスク及び関連遺産群","0"),
            listOf("紀伊山地の霊場と参詣道","1"),
            listOf("石見銀山遺跡とその文化的景観","0"),
            listOf("平泉‐仏国土（浄土）を表す建築・庭園及び考古学的遺跡群‐","1"),
            listOf("富士山‐信仰の対象と芸術の源泉‐","0"),
            listOf("富岡製糸場と絹産業遺産群","1"),
            listOf("明治日本の産業革命遺産","0"),
            listOf("ル・コルビュジエの建築作品‐近代建築運動への顕著な貢献‐","1"),
            listOf("「神宿る島」宗像・沖ノ島と関連遺産群","0"),
            listOf("長崎と天草地方の潜伏キリシタン関連遺産","1"),
            listOf("百舌鳥・古市古墳群‐古代日本の墳墓群‐","0"),
            listOf("北海道・北東北の縄文遺跡群","1"),
            listOf("佐渡島の金山","0"))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Column(modifier = Modifier.fillMaxSize()){
                Row {
                    Database.UserChara()
                    Database.Ratio(sample = sample)
                }
                HorizontalDivider(thickness = 24.dp)
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for (i in sample){
                        Row(modifier = Modifier.padding(10.dp)){
                            Database.ListImage(i = i)
                            Database.ListCheck(i = i)
                        }
                        HorizontalDivider(modifier = Modifier.padding(5.dp))
                    }
                }
            }
        }
    }
}

private object Database{
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
    fun Ratio(sample:List<List<String>>){
        var sum:Int = 0
        for (i in sample){
            if (i[1] == "1"){
                sum++
            }
        }
        Text(
            text = "$sum/26",
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 50.dp)
        )
    }

    @Composable
    fun ListCheck(i:List<String>){
        var check:String = ""
        if (i[1] == "1"){ check = "〇" }
        else{ check = "ｘ"}
        Text(
            text = i[0] + " : " + check,
            fontSize = 24.sp
        )
    }
    //設計途中
    @Composable
    fun ListImage(i:List<String>){
        var image:Int = 0
        if (i[1] == "1"){ image = R.drawable.mountain_00003 }
        else{ image = R.drawable.autumn_leaves_00014 }
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(50.dp,60.dp)
        )
    }
}