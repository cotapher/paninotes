import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignC

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
    val SELECT_HTML = """(getSelectionHTML = function () {
      var userSelection;
      if (window.getSelection) {
        // W3C Ranges
        userSelection = window.getSelection ();
        // Get the range:
        if (userSelection.getRangeAt)
          var range = userSelection.getRangeAt (0);
        else {
          var range = document.createRange ();
          range.setStart (userSelection.anchorNode, userSelection.anchorOffset);
          range.setEnd (userSelection.focusNode, userSelection.focusOffset);
        }
        // And the HTML:
        var clonedSelection = range.cloneContents ();
        var div = document.createElement ('div');
        div.appendChild (clonedSelection);
        return div.innerHTML;
      } else if (document.selection) {
        // Explorer selection, return the HTML
        userSelection = document.selection.createRange ();
        return userSelection.htmlText;
      } else {
        return '';
      }
    })()"""

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

        /*
        val uriBuilder = UriBuilder.fromUri("http://hilite.me/api")
        uriBuilder.queryParam("code", URLEncoder.encode(mockTextKotlin, "UTF-8").replace("+", "%20"))
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
            this.htmlText = response.body().toString()
        } else {
            print("ERROR ${response.statusCode()}")
            print(response.body().toString())
        }*/

        //this.htmlText = styledText
    }

    private fun showCodeBlockPopup(startingText: String = "") {
        // Create a custom popup that has a TextArea in it, so the user can enter the text they want syntax highlighting for
        val dialog: Dialog<String> = Dialog()
        dialog.title = "Paninotes"

        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL);

        val pane = Pane()
        val textArea = TextArea(startingText)
        pane.children.add(textArea)

        dialog.dialogPane.content = pane

        // Set the result of the dialog to the text that's entered in the text area
        dialog.setResultConverter {
            return@setResultConverter textArea.text
        }

        val result = dialog.showAndWait()

        if (result.isPresent) {
            val entered = result.get()
            if (entered.isNotEmpty()) {

            }
        }
    }
}