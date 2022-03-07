import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File

class Note(notePath: File) {
    var filePath: File? = null
    var fileName: String? = null
    var fileContents: String? = ""
    var fileMetadata: MutableMap<String,String>? = null
    init {
        this.filePath = notePath
        this.fileName = resolveNameFromPath()
        this.filePath?.createNewFile()
    }

    fun resolveNameFromPath(): String? {
        return filePath?.nameWithoutExtension
    }
    fun setContents() {
        fileContents = readHTMLFile(filePath)
    }
    fun setMetaData() {
        fileMetadata = fileContents?.let { readMetaData(it) }
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
        println(metadataMap.toString())
        return metadataMap
    }
    fun saveNote(HTMLString:String){
        filePath?.writeText(HTMLString)
    }

}