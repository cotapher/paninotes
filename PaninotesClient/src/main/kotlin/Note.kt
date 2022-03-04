import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.File
@JsonSerialize
@JsonDeserialize
class Note(title: String) {
    var id:Int? = null
    var title: String = ""
    var filePath: File? = null

    init {
        this.title = title
    }
}