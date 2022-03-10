import java.io.File

class Notebook(id: Int, title: String) {
    var notebookId: Int = 0
    private set

    var title: String = ""
    val notes = mutableListOf<Note>()
    var filePath: File? = null

    init {
        this.title = title
        this.notebookId = id
    }

    fun addNote(note: Note) {
        notes.add(note)
    }

    fun getNoteByTitle(noteTitle: String): Note? {
        for (note in notes) {
            if (note.title == noteTitle) {
                return note
            }
        }

        return null
    }
}