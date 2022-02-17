import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.nio.file.Paths

class Model {

    private val views = ArrayList<IView>()
    private val testNotebookDir = Paths.get("${System.getProperty("user.dir")}/src/main/resources/testNotebook1")
    var currentNotebook = File(testNotebookDir.toUri())
    var currentFile: File? = null
    var currentFileContents = ""
    var currentFileMetadata = readMetaData(currentFileContents)
    private val notebooks = ArrayList<Notebook>()
    var currentNotebookIndex: Int = -1 // TODO maybe we save this in a file?

    // view management
    fun addView(view: IView) {
        views.add(view)
    }

    fun notifyViews() {
        for (view in views) {
            view.update()
        }
    }

    fun openAndReadHTMLFile(file: File?) {
        if (file != null) {
            currentFile = file
        }
        val htmlFileText = readHTMLFile(currentFile)
        val htmlMetadata = htmlFileText?.let { readMetaData(it) }
        if (htmlFileText != null) {
            currentFileContents = htmlFileText
        }
        if (htmlMetadata != null) {
            currentFileMetadata = htmlMetadata
        }
        notifyViews()
    }

    private fun readHTMLFile(file:File?): String? {
        return file?.readText(Charsets.UTF_8)
    }

    private fun readMetaData(HTMLString: String): MutableMap<String,String>{
        //JSoup docs
        val doc: Document = Jsoup.parse(HTMLString)
        val metaTags: Elements = doc.getElementsByTag("meta")
        val metadataMap = mutableMapOf<String,String>()
        //parsing metadat tags
        for (metaTag in metaTags) {
            val name: String = metaTag.attr("name")
            val content: String = metaTag.attr("content")
            metadataMap[name] = content
        }
        println(metadataMap.toString())
        return metadataMap
    }
    // NOTEBOOKS --------------------------------------------------------------------------------------------------

    fun createNotebook(title: String): Notebook {
        currentNotebookIndex++
        return Notebook(currentNotebookIndex, title)
    }

    fun addNotebook(notebook: Notebook) {
        notebooks.add(notebook)
        notifyViews()
    }

    fun getAllNotebooks(): ArrayList<Notebook> {
        return notebooks
    }

    fun getNotebookById(id: Int): Notebook? {
        for (notebook in notebooks) {
            if (notebook.notebookId == id) return notebook
        }

        return null
    }
}