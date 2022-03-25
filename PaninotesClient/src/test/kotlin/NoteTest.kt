import backupState.BackupState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

internal class NoteTest {
    private val testNotePath = File(Paths.get("src/main/resources/UnitTestNotebooks/notebook1/note11.html").toUri())
    private val model = Model()

    @Test
    fun setContentsTest() {
        val htmlfileContents = testNotePath.readText()
        val note = Note(testNotePath)
        note.setContents()
        assertEquals(note.htmlText, htmlfileContents)
    }

    @Test
    fun saveNoteTest() {
        val htmlfileContents = testNotePath.readText()
        val testString = "test"
        val note = Note(testNotePath)
        note.saveNote(testString)
        assertEquals(note.htmlText, testString)
        assertEquals(note.backupState, BackupState.OUT_OF_SYNC)
        note.saveNote(htmlfileContents)
    }
}