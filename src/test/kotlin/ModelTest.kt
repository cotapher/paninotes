
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ModelTest {

    val model = Model()

    @Test
    fun setCurrentOpenFolderTest() {
        //Changes the current open folder
        val dir = model.testNotebookDir
        model.setCurrentOpenFolder(dir)
        assertEquals(this.model.currentNotebook, dir)
    }

    @Test
    fun createHTMLFileWithNameTest() {
        val dir = model.testNotebookDir
        val testFileName: String = "THISISFROMTESTSUITE"
        model.createHTMLFileWithName(testFileName, dir)
        assertEquals(testFileName, model.currentFile?.name)
    }

    @Test
    fun openAndReadHTMLFileTest() {
    }

    @Test
    fun createNotebookTest() {
    }

    @Test
    fun addNotebookTest() {
    }

    @Test
    fun getAllNotebooksTest() {
    }

    @Test
    fun getNotebookByIdTest() {
    }

    @Test
    fun readHTMLFile() {
    }

    @Test
    fun readMetaData() {
    }
}