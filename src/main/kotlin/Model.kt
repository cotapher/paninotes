import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File

class Model {

    private val views = ArrayList<IView>()

    var currentFile = File("newNote.html")
    var currentFileContents = "<head>\n" +
            "    <meta name=\"title\" content=\"THIS IS A HIDDEN TITLE\">\n" +
            "</head>Hello <a href=\"https://github.com/TestFX/TestFX\">world</a>"
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
        val htmlMetadata = readMetaData(htmlFileText)
        currentFileContents = htmlFileText
        currentFileMetadata = htmlMetadata
        notifyViews()
    }

    private fun readHTMLFile(file:File):String{
        return file.readText(Charsets.UTF_8)
    }

    private fun readMetaData(HTMLString: String): MutableMap<String,String>{
        val doc: Document = Jsoup.parse(HTMLString)
        val metaTags: Elements = doc.getElementsByTag("meta")
        val metadataMap = mutableMapOf<String,String>()
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