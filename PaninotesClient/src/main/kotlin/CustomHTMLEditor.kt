import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar
import javafx.scene.control.Tooltip
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import jfxtras.styles.jmetro.MDL2IconFont
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
        val codeBlockIcon = MDL2IconFont("\uE943")
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

        // Get the text that the user is highlighting
        val webView = this.lookup("WebView") as WebView
        //val selection = webView.engine.executeScript("window.getSelection().toString()")
        val selection = webView.engine.executeScript(SELECT_HTML)

        if (selection is String) {

        }

       /* val highlighter = TextHighlighter()
        highlighter.setLanguage(LibraryConstant.LanguageConstant.CPP)
        val styledText = highlighter.getHighlightedText(mockTextCplusplus)*/

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
        }

        //this.htmlText = styledText
    }
}