
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextInputDialog
import java.io.File
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths

class Model {
    val mapper = jacksonObjectMapper()

    private val views = ArrayList<IView>()
    var NOTEBOOK_DIR = File(Paths.get("src/main/resources/Notebooks").toUri())
    var currentOpenNotebook: Notebook? = null
    var currentNote: Note? = null
    var openNotes = ArrayList<Note>()
    val notebooks = ArrayList<Notebook>()

    fun initializeNotebooks() {
        // Initialize and create all the notebook objects from iterating through the Notebook directory
        NOTEBOOK_DIR.listFiles()?.forEach { notebook ->
            val newNotebook = createNotebook(notebook.name)
            newNotebook.filePath = notebook
            addNotebook(newNotebook)

            // For each notebook, also initialize all the notes in the notebook
            notebook.listFiles()?.forEach { note ->
                val newNote = Note(note)
                newNote.notebook = newNotebook
                newNote.setContents()
                newNotebook.addNote(newNote)
            }
        }

        notifyViews()
    }

    // view management
    fun addView(view: IView) {
        views.add(view)
    }

    fun notifyViews() {
        for (view in views) {
            view.update()
        }
    }

    fun getNotebookByTitle(title: String): Notebook? {
        for (notebook in notebooks) {
            if (notebook.title == title) {
                return notebook
            }
        }

        return null
    }

    fun createNotebookPopup() {
        val popup = TextInputDialog()
        popup.title = "Paninotes"

        popup.headerText = "Create Notebook"
        popup.contentText = "Enter name for new notebook:"

        //show the popup
        val result = popup.showAndWait()
        if (result.isPresent) {
            val notebookNameResult = result.get()
            createNotebookWithName(notebookNameResult)
        }
    }

    private fun createNotebookWithName(notebookName: String) {
        val newNotebookFolder = File(NOTEBOOK_DIR.resolve(notebookName).toString())

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

            // set to current notebook
            if (newNotebookFolder != null) {
                currentOpenNotebook = newNotebook
            }
            notifyViews()
        }
    }

    fun createNotePopup(notebook: Notebook){
        val directory: File? = notebook.filePath

        if (directory != null) {
            val popup = TextInputDialog()
            popup.title = "Paninotes"
            val currentFileOrDir = directory
            if (currentFileOrDir?.canWrite() == true) {

                popup.headerText = "Create a new note inside ${directory?.name}"
                popup.contentText = "Enter name for new Note file"

                //show the popup
                val result = popup.showAndWait()
                if (result.isPresent) {
                    val noteFileName = result.get() + ".html"
                    setCurrentNote(noteFileName, notebook)
                }
            }
        }
    }

    fun setCurrentNote(noteFileName: String, notebook: Notebook) {
        val newNoteFile = File(notebook.filePath!!.resolve(noteFileName).toString())
        if (newNoteFile.exists()) {
            println("Error: ${newNoteFile.name} already exists")
            generateAlertDialogPopup(
                Alert.AlertType.ERROR, "Creation Error", "$newNoteFile already exists, " +
                        "try choosing a different name"
            )
        } else {
            // set to current file
            val newNote = Note(newNoteFile)
            newNote.notebook = notebook
            notebook.addNote(newNote)
            openNote(newNote)
            notifyViews()
        }

    }

    fun openNote(note: Note?) {
        currentNote = note
        currentNote?.setContents()
        currentNote?.setMetaData()

        if (note != null) {
            if (!openNotes.contains(note)) {
                openNotes.add(note)
            }
        }

        notifyViews()
    }

    fun closeNote(closedNote: Note?) {
        openNotes.remove(closedNote)

        // if there are 0 openNotes, then the currentNote is null
        if (openNotes.size == 0) {
            currentNote = null
        }

        notifyViews()
    }

    // NOTEBOOKS --------------------------------------------------------------------------------------------------

    fun createNotebook(title: String): Notebook {
        return Notebook(title)
    }

    fun addNotebook(notebook: Notebook) {
        notebooks.add(notebook)
        notifyViews()
    }

    fun getAllNotebooks(): ArrayList<Notebook> {
        return notebooks
    }

    private fun generateAlertDialogPopup(type: Alert.AlertType, title: String, content: String) {
        val fileExistsAlert = Alert(type)
        fileExistsAlert.title = title
        val errorContent = Label(content)
        errorContent.isWrapText = true
        fileExistsAlert.dialogPane.content = errorContent
        fileExistsAlert.showAndWait()
    }

    // Server

    fun makeBackup() {
        val client = HttpClient.newBuilder().build()
        val requestBody = mapper.writeValueAsString(currentOpenNotebook)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/backupNotebook"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                println("Success ${response.statusCode()}")
                print(response.body().toString())
                //TODO need integrate with view
    //            now we want to add ids to note objects
                val notebookWithID: Notebook = mapper.readValue(response.body().toString())
                //map notes back to notebook
                notebookWithID.notes.forEach { it.notebook = notebookWithID }
                notebookWithID.notes.forEach { it.notebookId = notebookWithID.id }
                val idx = notebooks.indexOfFirst{it.title == notebookWithID.title}
                notebooks[idx] = notebookWithID
                currentOpenNotebook = notebookWithID
                notifyViews()
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
            }
        } catch (e:ConnectException){
            println("Server is not running")
        }
    }

    fun testSendNote() {
        val client = HttpClient.newBuilder().build()
        val path = Paths.get(System.getProperty("user.dir")).resolve("src/main/resources/testNotebook1/fancynotes.html")
        val testFile = File(path.toUri())
        val testNote: Note = Note(testFile)
        testNote.setContents()
        testNote.setMetaData()
        val requestBody = mapper.writeValueAsString(testNote)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/new"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        try{
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() == 200){
                println("Success ${response.statusCode()}")
                print(response.body().toString())
    //                val noteList: List<Note> = mapper.readValue(response.body().toString())
    //                print(noteList.size)
    //                print(noteList.toString())
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
            }
        } catch (e:ConnectException){
            println("Server is not running")
        }
    }

    fun restoreBackup() {
        //TODO Create a dialog box that confirms overwrite

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/notebooks"))
            .GET()
            .build()

        try{
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() == 200){
                println("Success ${response.statusCode()}")
                print(response.body().toString())
                    val notebookList: List<Notebook> = mapper.readValue(response.body().toString())
                    print(notebookList.size)
                    print(notebookList.toString())

                    notebookList.forEach { notebook ->
                        // For each notebook, also initialize all the notes in the notebook
                        notebook.notes.forEach { note ->
                            note.notebook = notebook
                            note.htmlText?.let { note.saveNote(it) }
                        }
                        //remove previous notebooks if any
                        notebooks.removeAll{ it.title == notebook.title}
                        //add the backed up notebook
                        addNotebook(notebook)
                    }

                notifyViews()
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
            }
        } catch (e:ConnectException){
            println("Server is not running")
        }
    }
}