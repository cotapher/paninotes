import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

       /* val highlighter = TextHighlighter()
        highlighter.setLanguage(LibraryConstant.LanguageConstant.CPP)
        val styledText = highlighter.getHighlightedText(mockTextCplusplus)*/

        val objectMapper = jacksonObjectMapper()

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