import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

internal class NotebookTest {
    private val testNotePath = File(Paths.get("src/main/resources/UnitTestNotebooks/notebook1/note11.html").toUri())
    private val model = Model()

    @Test
    fun addNoteTest() {
        val notebook = Notebook("test")
        val note = Note(testNotePath)
        notebook.addNote(note)
        assert(notebook.notes.isNotEmpty())
    }

    @Test
    fun getNoteByTitleTest() {
        val notebook = Notebook("test")
        val note = Note(testNotePath)
        notebook.addNote(note)

        val query = note.title?.let { notebook.getNoteByTitle(it) }

        assertEquals(query?.title, note.title)
    }
}