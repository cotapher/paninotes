import animatefx.animation.SlideInLeft
import animatefx.animation.SlideOutLeft
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert
import jfxtras.styles.jmetro.FlatTextInputDialog
import jfxtras.styles.jmetro.JMetroStyleClass
import org.jsoup.Jsoup
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignM
import org.kordamp.ikonli.materialdesign2.MaterialDesignN

class SideIconPaneView(
    val model: Model,
    val htmlEditor: CustomHTMLEditor,
    val sideNotebookPaneView: SideNotebookPaneView,
    val stage: Stage
) : GridPane(),
    IView {

    private val notebookButton = Button()
    private val searchButton = Button()
    private val infoButton = Button()

    init {
        this.layoutView()
        this.add(notebookButton, 0, 0)
        this.add(searchButton, 0, 1)
        this.add(infoButton, 0, 2)

        // Default don't show the notebook pane
        sideNotebookPaneView.isVisible = false

        this.styleClass.add("front-pane")
        notebookButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        searchButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        infoButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
    }

    private fun layoutView() {
        notebookButton.id = "sideIconPane-notebook-button"
        searchButton.id = "sideIconPane-search-button"
        infoButton.id = "sideIconPane-info-button"

        // Set up the images and buttons for the sidebar
        notebookButton.setPrefSize(30.0, 40.0)
        searchButton.setPrefSize(30.0, 40.0)
        infoButton.setPrefSize(30.0, 40.0)

        // Kordamp Material Design icons
        // https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
        val notebookIcon = FontIcon(MaterialDesignN.NOTEBOOK_OUTLINE)
        val searchIcon = FontIcon(MaterialDesignM.MAGNIFY)
        val infoIcon = FontIcon(MaterialDesignI.INFORMATION_OUTLINE)

        notebookIcon.iconSize = 24
        searchIcon.iconSize = 24
        infoIcon.iconSize = 24

        notebookButton.graphic = notebookIcon
        searchButton.graphic = searchIcon
        infoButton.graphic = infoIcon

        // Button Actions
        notebookButton.setOnAction {
            // Toggle the side notebook pane view
            if (sideNotebookPaneView.isVisible) {
                val anim = SlideOutLeft(sideNotebookPaneView)
                anim.setSpeed(2.5)
                anim.setOnFinished { sideNotebookPaneView.isVisible = false }
                anim.play()

            } else {
                sideNotebookPaneView.isVisible = true
                val anim = SlideInLeft(sideNotebookPaneView)
                anim.setSpeed(2.5)
                anim.play()
            }
        }

        searchButton.setOnAction {
            searchText()
        }

        infoButton.setOnAction {
            usageStats()
        }
    }

    private fun usageStats() {
        val usageInfo = FlatAlert(Alert.AlertType.CONFIRMATION)
        usageInfo.initOwner(stage)
        usageInfo.headerText = "Usage Statistics:"
        usageInfo.title = "Paninotes"
        val noHtmlTags = Jsoup.parse(htmlEditor.htmlText).text()
        val delim = " "
        val list = noHtmlTags.split(delim)
        val textInParagraphs = Jsoup.parse(htmlEditor.htmlText).select("p")
        val emptyParagraphs = Jsoup.parse(htmlEditor.htmlText).select("p:empty")
        val paragraphs = textInParagraphs.size
        var characters = 0
        println(textInParagraphs)

        for (element in list) {
            for (j in element.indices) {
                characters++
            }
        }

        println(emptyParagraphs.size)

        usageInfo.contentText = "Words: ${list.size}\n" +
                "Characters (no spaces): ${characters}\n" +
                "Characters (with spaces) ${characters + (noHtmlTags.length - characters - paragraphs)}\n" +
                "Paragraphs: ${paragraphs}\n"

        //show the popup
        usageInfo.showAndWait()
    }

    private fun searchText() {
        val dialog = FlatTextInputDialog("")
        dialog.initOwner(stage)
        dialog.title = "Search"
        dialog.headerText = "Find Word"

        (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "Search"

        val result = dialog.showAndWait()
        if (result.isPresent) {
            val entered = result.get()
            if (entered.compareTo("") == 0) {
                (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "OK"
                dialog.show()
                dialog.headerText = "No Input"
            } else {
                val noHtmlTags = Jsoup.parse(htmlEditor.htmlText).text()
                var count = 0
                println(htmlEditor.htmlText)
                println(noHtmlTags)
                val text = replaceWord(htmlEditor.htmlText, entered, "<mark>$entered</mark>", true)

                val delim = " "
                val list = noHtmlTags.split(delim)

                var outputString = ""
                for (item in list) {
                    if (entered in item) {
                        count++
                    }
                }

                println(outputString)
                val oldText = htmlEditor.htmlText
                htmlEditor.htmlText = text
                (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "OK"
                dialog.headerText = "Found: $count"
                dialog.showAndWait()
                htmlEditor.htmlText = oldText
            }
        }
    }

    private fun replaceWord(html: String, word: String, new: String, highlight: Boolean): String {
        var replaced: String
        val doc = Jsoup.parse(html) // document
        val els = doc.body().allElements

        for (e in els) {
            val tnList = e.textNodes()
            for (tn in tnList) {
                val orig = tn.text()
                tn.text(orig.replace(word, new))
            }
        }
        replaced = doc.toString()

        if (highlight) {
            replaced = replaced.replace("&lt;mark&gt;", "<mark>")
            replaced = replaced.replace("&lt;/mark&gt;", "</mark>")
        }

        return replaced
    }

    override fun update() {
        this.layoutView()
        //add a condition to only show editor if there is file assigned to model.currentFile
        searchButton.isVisible = model.currentNote != null
        infoButton.isVisible = model.currentNote != null
    }
}