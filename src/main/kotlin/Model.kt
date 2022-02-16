import java.io.File

class Model {

    private val views = ArrayList<IView>()
    var currentFile = File("newNote.html")
    var currentFileContents = "Hello <a href=\"https://github.com/TestFX/TestFX\">world</a>"
    // view management
    fun addView(view: IView) {
        views.add(view)
    }

    fun notifyViews() {
        for (view in views) {
            view.update()
        }
    }

    fun openAndReadHTMLFile(file: File?) {
        if (file != null) {
            currentFile = file
        }
        val htmlFileText = readHTMLFile(currentFile)
        currentFileContents = htmlFileText
        notifyViews()
    }

    private fun readHTMLFile(file:File):String{
        return file.readText(Charsets.UTF_8)
    }

}