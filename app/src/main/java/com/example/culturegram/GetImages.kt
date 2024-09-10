import android.content.Context
import android.os.Environment
import java.io.File

class GetImages {

    fun Content(context: Context): List<String> {
        val imageList = mutableListOf<String>()

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
                    imageList.add(file.absolutePath)  // 画像ファイルのパスをリストに追加
                }
            }
        } else {
            // ディレクトリが存在しない場合の処理
            println("Directory not found or is not accessible.")
        }

        return imageList
    }
}
