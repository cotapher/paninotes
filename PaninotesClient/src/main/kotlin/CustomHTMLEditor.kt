import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import jfxtras.styles.jmetro.FlatDialog
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

    private val HILITEME_LANGUAGES = FXCollections.observableArrayList(
        "ABAP",
        "ActionScript",
        "ActionScript 3",
        "Ada",
        "ANTLR",
        "ANTLR With ActionScript Target",
        "ANTLR With C# Target",
        "ANTLR With CPP Target",
        "ANTLR With Java Target",
        "ANTLR With ObjectiveC Target",
        "ANTLR With Perl Target",
        "ANTLR With Python Target",
        "ANTLR With Ruby Target",
        "ApacheConf",
        "AppleScript",
        "AspectJ",
        "aspx-cs",
        "aspx-vb",
        "Asymptote",
        "autohotkey",
        "AutoIt",
        "Awk",
        "Base Makefile",
        "Bash",
        "Bash Session",
        "Batchfile",
        "BBCode",
        "Befunge",
        "BlitzMax",
        "Boo",
        "Brainfuck",
        "Bro",
        "BUGS",
        "C",
        "C#",
        "C++",
        "c-objdump",
        "ca65",
        "CBM BASIC V2",
        "Ceylon",
        "CFEngine3",
        "cfstatement",
        "Cheetah",
        "Clojure",
        "CMake",
        "COBOL",
        "COBOLFree",
        "CoffeeScript",
        "Coldfusion HTML",
        "Common Lisp",
        "Coq",
        "cpp-objdump",
        "Croc",
        "CSS",
        "CSS+Django/Jinja",
        "CSS+Genshi Text",
        "CSS+Lasso",
        "CSS+Mako",
        "CSS+Myghty",
        "CSS+PHP",
        "CSS+Ruby",
        "CSS+Smarty",
        "CUDA",
        "Cython",
        "D",
        "d-objdump",
        "Darcs Patch",
        "Dart",
        "Debian Control file",
        "Debian Sourcelist",
        "Delphi",
        "dg",
        "Diff",
        "Django/Jinja",
        "DTD",
        "Duel",
        "Dylan",
        "Dylan session",
        "DylanLID",
        "eC",
        "ECL",
        "Elixir",
        "Elixir iex session",
        "Embedded Ragel",
        "ERB",
        "Erlang",
        "Erlang erl session",
        "Evoque",
        "Factor",
        "Fancy",
        "Fantom",
        "Felix",
        "Fortran",
        "FoxPro",
        "FSharp",
        "GAS",
        "Genshi",
        "Genshi Text",
        "Gettext Catalog",
        "Gherkin",
        "GLSL",
        "Gnuplot",
        "Go",
        "GoodData-CL",
        "Gosu",
        "Gosu Template",
        "Groff",
        "Groovy",
        "Haml",
        "Haskell",
        "haXe",
        "HTML",
        "HTML+Cheetah",
        "HTML+Django/Jinja",
        "HTML+Evoque",
        "HTML+Genshi",
        "HTML+Lasso",
        "HTML+Mako",
        "HTML+Myghty",
        "HTML+PHP",
        "HTML+Smarty",
        "HTML+Velocity",
        "HTTP",
        "Hxml",
        "Hybris",
        "IDL",
        "INI",
        "Io",
        "Ioke",
        "IRC logs",
        "Jade",
        "JAGS",
        "Java",
        "Java Server Page",
        "JavaScript",
        "JavaScript+Cheetah",
        "JavaScript+Django/Jinja",
        "JavaScript+Genshi Text",
        "JavaScript+Lasso",
        "JavaScript+Mako",
        "JavaScript+Myghty",
        "JavaScript+PHP",
        "JavaScript+Ruby",
        "JavaScript+Smarty",
        "JSON",
        "Julia",
        "Julia console",
        "Kconfig",
        "Koka",
        "Kotlin",
        "Lasso",
        "Lighttpd configuration file",
        "Literate Haskell",
        "LiveScript",
        "LLVM",
        "Logos",
        "Logtalk",
        "Lua",
        "Makefile",
        "Mako",
        "MAQL",
        "Mason",
        "Matlab",
        "Matlab session",
        "MiniD",
        "Modelica",
        "Modula-2",
        "MoinMoin/Trac Wiki markup",
        "Monkey",
        "MOOCode",
        "MoonScript",
        "Mscgen",
        "MuPAD",
        "MXML",
        "Myghty",
        "MySQL",
        "NASM",
        "Nemerle",
        "NewLisp",
        "Newspeak",
        "Nginx configuration file",
        "Nimrod",
        "NSIS",
        "NumPy",
        "objdump",
        "Objective-C",
        "Objective-C++",
        "Objective-J",
        "OCaml",
        "Octave",
        "Ooc",
        "Opa",
        "OpenEdge ABL",
        "Perl",
        "PHP",
        "PL/pgSQL",
        "PostgreSQL console (psql)",
        "PostgreSQL SQL dialect",
        "PostScript",
        "POVRay",
        "PowerShell",
        "Prolog",
        "Properties",
        "Protocol Buffer",
        "Puppet",
        "PyPy Log",
        "Python",
        "Python 3",
        "Python 3.0 Traceback",
        "Python console session",
        "Python Traceback",
        "QML",
        "Racket",
        "Ragel",
        "Ragel in C Host",
        "Ragel in CPP Host",
        "Ragel in D Host",
        "Ragel in Java Host",
        "Ragel in Objective C Host",
        "Ragel in Ruby Host",
        "Raw token data",
        "RConsole",
        "Rd",
        "REBOL",
        "Redcode",
        "reg",
        "reStructuredText",
        "RHTML",
        "RobotFramework",
        "RPMSpec",
        "Ruby",
        "Ruby irb session",
        "Rust",
        "S",
        "Sass",
        "Scala",
        "Scalate Server Page",
        "Scaml",
        "Scheme",
        "Scilab",
        "SCSS",
        "Shell Session",
        "Smali",
        "Smalltalk",
        "Smarty",
        "Snobol",
        "SourcePawn",
        "SQL",
        "sqlite3con",
        "SquidConf",
        "Stan",
        "Standard ML",
        "systemverilog",
        "Tcl",
        "Tcsh",
        "Tea",
        "TeX",
        "Text only",
        "Treetop",
        "TypeScript",
        "UrbiScript",
        "Vala",
        "VB.net",
        "Velocity",
        "verilog",
        "VGL",
        "vhdl",
        "VimL",
        "XML",
        "XML+Cheetah",
        "XML+Django/Jinja",
        "XML+Evoque",
        "XML+Lasso",
        "XML+Mako",
        "XML+Myghty",
        "XML+PHP",
        "XML+Ruby",
        "XML+Smarty",
        "XML+Velocity",
        "XQuery",
        "XSLT",
        "Xtend",
        "YAML",
    )

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
        dialog.title = "Paninotes"

        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL);

        val vboxPane = VBox()
        val headerText = Text("Enter your code:")
        val textArea = TextArea(startingText)
        val languagePicker = ComboBox(HILITEME_LANGUAGES) // Add a dropdown with all the available languages for the hilite.me api
        languagePicker.selectionModel.select("Kotlin")
        vboxPane.children.addAll(headerText, textArea, languagePicker)

        dialog.dialogPane.content = vboxPane

        // Set the result of the dialog to the text that's entered in the text area
        dialog.setResultConverter {
            // <Button type, Code text, Language>
            return@setResultConverter Triple<ButtonType, String, String>(it, textArea.text, languagePicker.value)
        }

        val result = dialog.showAndWait()

        if (result.isPresent) {
            val buttonTypeAndText = result.get()

            // Make sure the OK button is pressed
            if (buttonTypeAndText.first == ButtonType.OK && buttonTypeAndText.second.isNotEmpty()) {
                val enteredText = buttonTypeAndText.second
                val syntaxHighlightedTextHtml = getSyntaxHighlightedText(enteredText, buttonTypeAndText.third)

                // We will replace the user's highlighted text with the syntax highlighted text
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
            val selected = engine.executeScript(GET_SELECTED_HTML)

            if (selected is String) {
                // Replace the selected html with the syntax highlighted html
                this.htmlText = this.htmlText.replace(selected, html)
            }
        }
    }

    private fun getSyntaxHighlightedText(text: String, language: String): String? {
        if (text.isNotEmpty() && text.isNotBlank()) {
            val uriBuilder = UriBuilder.fromUri("http://hilite.me/api")
            uriBuilder.queryParam("code", URLEncoder.encode(text, "UTF-8").replace("+", "%20"))
            uriBuilder.queryParam("lexer", "Kotlin")
            val uri: URI = uriBuilder.build()

            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return if(response.statusCode() == 200){
                response.body().toString()
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
                null
            }
        }

        return null
    }
}