import android.content.Context
import android.os.Environment
import java.io.File

class GetImages {

    fun Content(context: Context): List<Pair<String, String>> {
        val imageList = mutableListOf<Pair<String, String>>()

        // /storage/emulated/0/Android/data/com.example.culturegram/files/Pictures にアクセス
        val picturesDir = File(
            Environment.getExternalStorageDirectory(),
            "Android/data/com.example.culturegram/files/Pictures"
        )

        // ディレクトリが存在し、読み取り可能か確認
        if (picturesDir.exists() && picturesDir.isDirectory) {
            val files = picturesDir.listFiles()
            files?.forEach { file ->
                if (file.isFile && (file.extension == "jpg" || file.extension == "jpeg" || file.extension == "png")) {
                    // ファイル名を "-" で分割して前半部分のみを取得
                    val fileNamePart = file.name.split("-").firstOrNull() ?: file.name
                    // ファイルパスと画像名（ファイル名の前半部分）をペアでリストに追加
                    imageList.add(Pair(file.absolutePath, fileNamePart))
                }
            }
        } else {
            // ディレクトリが存在しない場合の処理
            println("Directory not found or is not accessible.")
        }

        return imageList
    }
}
