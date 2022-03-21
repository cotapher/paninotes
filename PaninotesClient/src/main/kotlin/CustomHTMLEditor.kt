import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignC
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.ws.rs.core.UriBuilder


// Referenced from: https://gist.github.com/dipu-bd/425a86105dbeb42ad31d
class CustomHTMLEditor: HTMLEditor() {
    private val TOP_TOOLBAR = ".top-toolbar"
    private val BOTTOM_TOOLBAR = ".bottom-toolbar"
    private val WEB_VIEW = ".web-view"

    private var webView: WebView? = null
    private var topToolBar: ToolBar? = null
    private var bottomToolBar: ToolBar? = null
    private var codeBlockButton: Button = Button()

    // https://stackoverflow.com/questions/12786004/how-to-getselectedtext-from-webview-in-javafx
    //https://developer.mozilla.org/en-US/docs/Web/API/Selection/deleteFromDocument
    //https://stackoverflow.com/questions/16606054/find-htmleditor-cursor-pointer-for-inserting-image

    // https://stackoverflow.com/questions/2213376/how-to-find-cursor-position-in-a-contenteditable-div
    val INSERT_HTML_AT_CURSOR = """
        (insertHtmlAtCursor = function (html) {
            var range, node;
            if (window.getSelection && window.getSelection().getRangeAt) {
                range = window.getSelection().getRangeAt(0);
                node = range.createContextualFragment(html);
                range.insertNode(node);
            } else if (document.selection && document.selection.createRange) {
                document.selection.createRange().pasteHTML(html);
            }
        }
    """

    val SELECT_HTML = """
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

    private fun onCodeBlockAction() {
        // Send the highlighted text to hilite.me, a text -> html syntax highlighter
        // http://hilite.me/

        val mockTextKotlin = "fun testFunction(str: String) {  int a }"
        val mockTextJava = "public void testFunction(String str) {/n/tint num = 5;/n}"
        val mockTextCplusplus = "cout << \"Hello world\""

        // Get the text that the user is highlighting (if they are highlighting any)
        val webView = this.lookup("WebView") as WebView
        val selection = webView.engine.executeScript("window.getSelection().toString()")

        // If the user is highlighting text, then we want to show the code block pop up with that text prepopulated
        if (selection is String) {
            showCodeBlockPopup(selection)
        }

        //this.htmlText = styledText
    }

    private fun showCodeBlockPopup(startingText: String = "") {
        // Create a custom popup that has a TextArea in it, so the user can enter the text they want syntax highlighting for
        val dialog: Dialog<Pair<ButtonType, String>> = Dialog()
        dialog.title = "Paninotes"

        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL);

        val vboxPane = VBox()
        val headerText = Text("Code:")
        val textArea = TextArea(startingText)
        vboxPane.children.addAll(headerText, textArea)

        dialog.dialogPane.content = vboxPane

        // Set the result of the dialog to the text that's entered in the text area
        dialog.setResultConverter {
            return@setResultConverter Pair<ButtonType, String>(it, textArea.text)
        }

        val result = dialog.showAndWait()

        if (result.isPresent) {
            val buttonTypeAndText = result.get()
            if (buttonTypeAndText.first == ButtonType.OK && buttonTypeAndText.second.isNotEmpty()) {
                val enteredText = buttonTypeAndText.second

                // Call the hilite.me api with the user's entered text, to get the html for the syntax highlighted text
                val syntaxHighlightedTextHtml = getSyntaxHighlightedText(enteredText)

                if (syntaxHighlightedTextHtml != null) {
                    insertHtmlAtCursor(syntaxHighlightedTextHtml)
                }
            }
        }
    }

    private fun insertHtmlAtCursor(html: String) {
        if (!html.isNullOrEmpty()) {
            val webView = this.lookup("WebView") as WebView
            val engine = webView.engine

            // Get the selected html
            val selected = engine.executeScript(SELECT_HTML)

            if (selected is String) {
                // Replace the selected html with the syntax highlighted html
                this.htmlText = this.htmlText.replace(selected, html)
            }

            /*

            // Go through the html string, and whenever there is a quotation mark, add a backslash before it,
            // so it escapes when we use it in JS
            val htmlWithBackslashes = html.replace("\"", "\\\"")

            val str = "" +
                    "var range, node;\n" +
                    "            if (window.getSelection && window.getSelection().getRangeAt) {\n" +
                    "                range = window.getSelection().getRangeAt(0);\n" +
                    "                node = range.createContextualFragment(`" + htmlWithBackslashes + "`);\n" +
                    "                range.insertNode(node);\n" +
                    "            } else if (document.selection && document.selection.createRange) {\n" +
                    "                document.selection.createRange().pasteHTML(`" + htmlWithBackslashes + "`);\n" +
                    "            }"
            println("HTML ")
            println(str)
            engine.executeScript(str)*/
        }
    }

    private fun getSyntaxHighlightedText(text: String): String? {
        if (text.isNotEmpty() && text.isNotBlank()) {
            val uriBuilder = UriBuilder.fromUri("http://hilite.me/api")
            uriBuilder.queryParam("code", URLEncoder.encode(text, "UTF-8").replace("+", "%20"))
            uriBuilder.queryParam("lexer", "kotlin")
            val uri: URI = uriBuilder.build()

            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() == 200){
                //this.htmlText = response.body().toString()
                return response.body().toString()
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
                return null
            }
        }

        return null
    }
}