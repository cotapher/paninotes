
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import java.io.File
import java.nio.file.Paths

@ExtendWith(ApplicationExtension::class)
internal class ModelTest {
    val testNotebookDir = File(Paths.get("src/main/resources/testNotebook1").toUri())
    val testHTMLFile = File(Paths.get("src/main/resources/testNotebook1/testNote.html").toUri())

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
    fun setCurrentFileTest() {
        val dir = model.testNotebookDir
        val testFileName = "thiscanbeanyfile.html"
        model.setCurrentFile(testFileName, dir)
        assertEquals(testFileName, model.currentFile?.name)
    }

    @Test
    fun openAndReadHTMLFileTest() {
        val expectedMetadata = mutableMapOf<String,String>("title" to "THIS IS A HIDDEN TITLE")
        model.openAndReadHTMLFile(testHTMLFile)
        assertEquals(expectedMetadata,model.currentFileMetadata)
        assertEquals(mockHtmlFileContents,model.currentFileContents)
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
        val expectedMetadata = mutableMapOf<String,String>("title" to "THIS IS A HIDDEN TITLE")
        val actualMetadata = model.readMetaData(mockHtmlFileContents)
        assertEquals(expectedMetadata,actualMetadata)
    }
}