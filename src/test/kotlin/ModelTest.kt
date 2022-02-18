
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ModelTest {

    val model = Model()

    val mockHtmlFileContents = "<html dir=\"ltr\"><head>\n" +
            "    <meta name=\"title\" content=\"THIS IS A HIDDEN TITLE\">\n" +
            "</head><body contenteditable=\"true\">Hello <a href=\"https://github.com/TestFX/TestFX\">world</a></body></html>"

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
        val testFileName = "THISISFROMTESTSUITE"
        model.createHTMLFileWithName(testFileName, dir)
        assertEquals(testFileName, model.currentFile?.name)
    }

    @Test
    fun openAndReadHTMLFileTest() {
        val dir = model.testNotebookDir
        val testFileName = "THISISFROMTESTSUITE"

        model.createHTMLFileWithName(testFileName, dir)
        assertEquals(testFileName, model.currentFile?.name)
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
    fun readMetaDataTest() {
        val expected = mutableMapOf<String,String>("title" to "THIS IS A HIDDEN TITLE")
        val actual = model.readMetaData(mockHtmlFileContents)
        assertEquals(expected,actual)
    }
}