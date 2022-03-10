
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert
import jfxtras.styles.jmetro.FlatTextInputDialog
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Model (val stage: Stage? = null) {

    private val views = ArrayList<IView>()
    var NOTEBOOK_DIR = File(Paths.get("src/main/resources/Notebooks").toUri())
    var currentOpenNotebook: Notebook? = null
    var currentNote: Note? = null
    var openNotes = ArrayList<Note>()
    val notebooks = ArrayList<Notebook>()
    var currentNotebookIndex: Int = -1 // TODO maybe we save this in a file?

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

    fun createNotebookWithName(notebookName: String) {
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
            val popup = FlatTextInputDialog()
            popup.initOwner(stage)
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
        val fileExistsAlert = FlatAlert(type)
        fileExistsAlert.initOwner(stage)
        fileExistsAlert.title = title
        val errorContent = Label(content)
        errorContent.isWrapText = true
        fileExistsAlert.dialogPane.content = errorContent
        fileExistsAlert.showAndWait()
    }
}