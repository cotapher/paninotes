import java.io.File

class Notebook(title: String) {
    var id: Int? = null
    var title: String = ""
    val notes = mutableListOf<Note>()
    var filePath: File? = null

    init {
        this.title = title
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