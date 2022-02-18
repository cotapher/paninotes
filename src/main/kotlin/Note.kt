import java.io.File

class Note(title: String) {
    var title: String = ""
    var filePath: File? = null

    init {
        this.title = title
    }
}