import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert
import jfxtras.styles.jmetro.FlatDialog
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignC


// Referenced from: https://gist.github.com/dipu-bd/425a86105dbeb42ad31d
class CustomHTMLEditor : HTMLEditor() {
    var stage: Stage? = null

    private val TOP_TOOLBAR = ".top-toolbar"
    private val BOTTOM_TOOLBAR = ".bottom-toolbar"
    private val WEB_VIEW = ".web-view"

    private var webView: WebView? = null
    private var topToolBar: ToolBar? = null
    private var bottomToolBar: ToolBar? = null
    private var codeBlockButton: Button = Button()

    // https://stackoverflow.com/questions/12786004/how-to-getselectedtext-from-webview-in-javafx
    private val GET_SELECTED_HTML = """
        (getSelectionHtml = function () {
            var html = "";
            if (typeof window.getSelection != "undefined") {
                var sel = window.getSelection();
                if (sel.rangeCount) {
                    var container = document.createElement("div");
                    for (var i = 0, len = sel.rangeCount; i < len; ++i) {
                        container.appendChild(sel.getRangeAt(i).cloneContents());
                    }
                    html = container.innerHTML;
                }
            } else if (typeof document.selection != "undefined") {
                if (document.selection.type == "Text") {
                    html = document.selection.createRange().htmlText;
                }
            }
            return html;
        })()
    """

    init {
        identifyControls()
    }

    private fun identifyControls() {
        var nod: Node? = lookup(WEB_VIEW)
        if (nod is WebView) {
            webView = nod
        }

        nod = lookup(TOP_TOOLBAR)
        if (nod is ToolBar) {
            topToolBar = nod
        }

        nod = lookup(BOTTOM_TOOLBAR)
        if (nod is ToolBar) {
            bottomToolBar = nod
        }
    }

    fun addCustomButtons() {
        // add code block/syntax highlighting button
        val codeBlockIcon = FontIcon(MaterialDesignC.CODE_BRACES)
        codeBlockButton.graphic = codeBlockIcon

        codeBlockButton.tooltip = Tooltip("Code Block")
        codeBlockButton.setOnAction { onCodeBlockAction() }

        //add to top toolbar
        topToolBar!!.items.add(codeBlockButton)
        topToolBar!!.items.add(Separator(Orientation.VERTICAL))
    }

    // SYNTAX HIGHLIGHTING --------------------------------------------------------------------------------------------------

    private fun onCodeBlockAction() {
        // Send the highlighted text to hilite.me, a text -> html syntax highlighter

        // Get the text that the user is highlighting (if they are highlighting any)
        val webView = this.lookup("WebView") as WebView
        val selection = webView.engine.executeScript("window.getSelection().toString()")

        // If the user is highlighting text, then we want to show the code block pop up with that text prepopulated
        if (selection is String) {
            showCodeBlockPopup(selection)
        }
    }

    private fun showCodeBlockPopup(startingText: String = "") {
        // Create a custom popup that has a TextArea in it, so the user can enter the text they want syntax highlighting for
        val dialog: FlatDialog<Triple<ButtonType, String, String>> = FlatDialog()
        dialog.initOwner(stage)

        dialog.title = "Paninotes"

        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        val vboxPane = VBox(5.0)
        val headerText = Text("Enter your code:")
        val textArea = TextArea(startingText)

        val languagesKeys = HiliteMeUtils.HILITEME_LANGUAGES_TO_LEXER.keys
        val languagesList = FXCollections.observableArrayList<String>()
        languagesKeys.forEach { key ->
            languagesList.add(key)
        }
        val languagePicker =
            ComboBox(languagesList) // Add a dropdown with all the available languages for the hilite.me api
        languagePicker.selectionModel.select("Kotlin")

        vboxPane.children.addAll(headerText, textArea, languagePicker)

        dialog.dialogPane.content = vboxPane

        // Set the result of the dialog to the text that's entered in the text area
        dialog.setResultConverter {
            // <Button type, Code text, Language>
            val language = if (HiliteMeUtils.HILITEME_LANGUAGES_TO_LEXER[languagePicker.value] != null)
                HiliteMeUtils.HILITEME_LANGUAGES_TO_LEXER[languagePicker.value]!! else ""
            return@setResultConverter Triple<ButtonType, String, String>(it, textArea.text, language)
        }

        val result = dialog.showAndWait()

        if (result.isPresent) {
            val buttonTypeAndText = result.get()

            // Make sure the OK button is pressed
            if (buttonTypeAndText.first == ButtonType.OK && buttonTypeAndText.second.isNotEmpty()) {
                val enteredText = buttonTypeAndText.second
                val syntaxHighlightedTextHtml =
                    HiliteMeUtils.getSyntaxHighlightedText(enteredText, buttonTypeAndText.third)

                // We will replace the user's highlighted text with the syntax highlighted text
                if (syntaxHighlightedTextHtml != null) {
                    insertHtmlAtCursor(syntaxHighlightedTextHtml)
                } else {
                    // There was an error with the hilite.me api so show an error dialog
                    val alert = FlatAlert(AlertType.ERROR)
                    alert.initOwner(stage)
                    alert.title = "Paninotes"
                    alert.headerText = "Syntax Highlighting Error"
                    alert.contentText = "Sorry, there was an error with the hilite.me api for syntax highlighting :("

                    alert.showAndWait()
                }
            }
        }
    }

    private fun insertHtmlAtCursor(html: String) {
        if (html.isNotEmpty()) {
            val webView = this.lookup("WebView") as WebView
            val engine = webView.engine

            // Get the selected html that the user's highlighting
            val selectedHtml = engine.executeScript(GET_SELECTED_HTML)

            // Get the selected text that the user's highlighting
            val selectedText = webView.engine.executeScript("window.getSelection().toString()")

            if (selectedHtml is String && selectedText is String) {
                // If the user did select some text (we assume they selected code they want highlighted),
                // then replace that with the syntax highlighted code
                // Else, then just paste the syntax highlighted code at the cursor's position
                if (selectedHtml.isNotEmpty() && selectedText.isNotBlank()) {
                    this.htmlText = this.htmlText.replace(selectedHtml, html)
                } else {
                    // Go through the html string, and whenever there is a quotation mark, add a backslash before it,
                    // so it escapes when we use it in JS
                    val htmlWithBackslashes = html.replace("\"", "\\\"")

                    // JS script to insert the syntax highlighted html code at the user's cursor position
                    val insertAtCursorScript = "" +
                            "var range, node;\n" +
                            "            if (window.getSelection && window.getSelection().getRangeAt) {\n" +
                            "                range = window.getSelection().getRangeAt(0);\n" +
                            "                node = range.createContextualFragment(`" + htmlWithBackslashes + "`);\n" +
                            "                range.insertNode(node);\n" +
                            "            } else if (document.selection && document.selection.createRange) {\n" +
                            "                document.selection.createRange().pasteHTML(`" + htmlWithBackslashes + "`);\n" +
                            "            }"

                    engine.executeScript(insertAtCursorScript)
                }
            }
        }
    }
}