
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextInputDialog
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Model {

    private val views = ArrayList<IView>()
    val testNotebookDir = File(Paths.get("src/main/resources/testNotebook1").toUri())
    var currentNotebook: File? = null
    var currentOpenNotebook: Notebook? = null
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

    fun getNotebookAtFilePath(filePath: File?): Notebook? {
        if (filePath != null) {
            for (notebook in notebooks) {
                if (notebook.filePath?.path.equals(filePath.path)) {
                    return notebook
                }
            }
        }

        return null
    }

    fun createNotebookFolderPopup(parentDirectory: File?) {
        val popup = TextInputDialog()
        popup.title = "Paninotes"

        if (parentDirectory?.canWrite() == true) {
            popup.headerText = "Create Notebook"
            popup.contentText = "Enter name for new notebook:"

            //show the popup
            val result = popup.showAndWait()
            if (result.isPresent) {
                val notebookNameResult = result.get()
                createNotebookFolderWithName(notebookNameResult, parentDirectory)
            }
        }
    }

    private fun createNotebookFolderWithName(notebookName: String, parentDirectory: File) {
        val newNotebookFolder = File(parentDirectory.resolve(notebookName).toString())

        if (newNotebookFolder.exists()) {
            println("Error: ${newNotebookFolder.name} already exists")
            generateAlertDialogPopup(
                Alert.AlertType.ERROR, "Creation Error", "$newNotebookFolder notebook already exists, " +
                        "try choosing a different name"
            )
        } else {
            // actually create the folder in storage
            Files.createDirectories(Paths.get(newNotebookFolder.path))

            // create the notebook in the app
            val newNotebook = createNotebook(notebookName)
            newNotebook.filePath = newNotebookFolder
            addNotebook(newNotebook)

            // set to current folder
            if (newNotebookFolder != null) {
                currentNotebook = newNotebookFolder
            }
            notifyViews()
        }
    }

    fun createHTMLFilePopup(directory: File?){
        val popup = TextInputDialog()
        popup.title = "Create a new note inside ${directory?.name}"
        val currentFileOrDir = directory
        if (currentFileOrDir?.canWrite() == true) {

            popup.headerText = "Create note within $currentFileOrDir?"
            popup.contentText = "Enter name for new Note file"

            //show the popup
            val result = popup.showAndWait()
            if (result.isPresent) {
                println(result)
                println(result.get())
                val textResult = result.get() + ".html"
                setCurrentFile(textResult, directory)
            }
        }
    }

    fun setCurrentFile(textResult: String, directory: File) {
        val newNoteFile = File(directory.resolve(textResult).toString())
        if (newNoteFile.exists()) {
            println("Error: ${newNoteFile.name} already exists")
            generateAlertDialogPopup(
                Alert.AlertType.ERROR, "Creation Error", "$newNoteFile already exists, " +
                        "try choosing a different name"
            )
        } else {
            // set to current file
            currentFile = newNoteFile

            createNoteFile(textResult, newNoteFile)
            notifyViews()
        }

    }

    fun createNoteFile(noteName: String, notePath: File) {
        // We open the notebook in the current open notebook
        notePath.writeText("") // Create an empty note/file
        val note = Note(noteName)
        note.filePath = notePath;
        currentOpenNotebook?.addNote(note)
        notifyViews()
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

    private fun generateAlertDialogPopup(type: Alert.AlertType, title: String, content: String) {
        val fileExistsAlert = Alert(type)
        fileExistsAlert.title = title
        val errorContent = Label(content)
        errorContent.isWrapText = true
        fileExistsAlert.dialogPane.content = errorContent
        fileExistsAlert.showAndWait()
    }
}