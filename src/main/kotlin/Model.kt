import java.io.File

class Model {

    private val views = ArrayList<IView>()
    var currentFile = File("OutputtedHTML.html")
    // view management
    fun addView(view: IView) {
        views.add(view)
    }

    fun notifyViews() {
        for (view in views) {
            view.update()
        }
    }

}