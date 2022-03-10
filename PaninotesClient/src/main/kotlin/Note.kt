import com.fasterxml.jackson.annotation.JsonIgnore
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File

class Note(var filePath: File? =null ) {
    var id: Int? = null
    var title: String? = null
    var htmlText: String? = ""
    var fileMetadata: MutableMap<String,String>? = null
    @JsonIgnore
    var notebook: Notebook? = null

    init {
        this.title = resolveNameFromPath()
        this.filePath?.createNewFile()
    }

    fun resolveNameFromPath(): String? {
        return filePath?.nameWithoutExtension
    }
    fun setContents() {
        htmlText = readHTMLFile(filePath)
    }
    fun setMetaData() {
        fileMetadata = htmlText?.let { readMetaData(it) }
    }

    fun readHTMLFile(file:File?): String? {
        return file?.readText(Charsets.UTF_8)
    }

    fun readMetaData(HTMLString: String): MutableMap<String,String>{
        //JSoup docs
        val doc: Document = Jsoup.parse(HTMLString)
        val metaTags: Elements = doc.getElementsByTag("meta")
        val metadataMap = mutableMapOf<String,String>()
        //parsing metadata tags
        for (metaTag in metaTags) {
            val name: String = metaTag.attr("name")
            val content: String = metaTag.attr("content")
            metadataMap[name] = content
        }

        return metadataMap
    }

    fun saveNote(HTMLString:String){
        filePath?.writeText(HTMLString)
    }

    override fun equals(other: Any?): Boolean {
        return (other is Note)
            && other.title == title
                && other.notebook?.title == notebook?.title

    }
}