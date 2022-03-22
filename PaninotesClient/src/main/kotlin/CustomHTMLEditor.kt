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

    // Map from the languages to show in the dropdown to the Pygments lexer short name (so the hilite.me API can process it)
    // https://pygments.org/docs/lexers/
    private val HILITEME_LANGUAGES_TO_LEXER = mapOf<String, String>(
        "ABAP" to "abap",
        "ActionScript" to "actionscript",
        "ActionScript 3" to "actionscript3",
        "Ada" to "ada",
        "ANTLR" to "antlr",
        "ANTLR With ActionScript Target" to "antlr-actionscript",
        "ANTLR With C# Target" to "antlr-csharp",
        "ANTLR With CPP Target" to "antlr-cpp",
        "ANTLR With Java Target" to "antlr-java",
        "ANTLR With ObjectiveC Target" to "antlr-objc",
        "ANTLR With Perl Target" to "antlr-perl",
        "ANTLR With Python Target" to "antlr-python",
        "ANTLR With Ruby Target" to "antlr-ruby",
        "ApacheConf" to "apacheconf",
        "AppleScript" to "applescript",
        "AspectJ" to "aspectj",
        "aspx-cs" to "aspx-cs",
        "aspx-vb" to "aspx-vb",
        "Asymptote" to "asymptote",
        "autohotkey" to "autohotkey",
        "AutoIt" to "autoit",
        "Awk" to "awk",
        "Base Makefile" to "basemake",
        "Bash" to "bash",
        "Bash Session" to "shell-session",
        "Batchfile" to "batch",
        "BBCode" to "bbcode",
        "Befunge" to "befunge",
        "BlitzMax" to "blitzmax",
        "Boo" to "boo",
        "Brainfuck" to "brainfuck",
        "Bro" to "bro",
        "BUGS" to "bugs",
        "C" to "c",
        "C#" to "c#",
        "C++" to "c++",
        "C Objdump" to "c-objdump",
        "ca65" to "ca65",
        "CBM BASIC V2" to "cbmbas",
        "Ceylon" to "ceylon",
        "CFEngine3" to "cfengine3",
        "Cheetah" to "cheetah",
        "Clojure" to "clojure",
        "CMake" to "cmake",
        "COBOL" to "cobol",
        "COBOLFree" to "cobolfree",
        "CoffeeScript" to "coffeescript",
        "Coldfusion HTML" to "cfm",
        "Common Lisp" to "common-lisp",
        "Coq" to "coq",
        "C++ Objdump" to "cpp-objdump",
        "Croc" to "croc",
        "CSS" to "css",
        "CSS+Django/Jinja" to "css+django",
        "CSS+Genshi Text" to "css+genshitext",
        "CSS+Lasso" to "css+lasso",
        "CSS+Mako" to "css+mako",
        "CSS+Myghty" to "css+myghty",
        "CSS+PHP" to "css+php",
        "CSS+Ruby" to "css+ruby",
        "CSS+Smarty" to "css+smart",
        "CUDA" to "cuda",
        "Cython" to "cython",
        "D" to "d",
        "D Objdump" to "d-objdump",
        "Darcs Patch" to "dpatch",
        "Dart" to "dart",
        "Debian Control file" to "debcontrol",
        "Debian Sourcelist" to "debsources",
        "Delphi" to "delphi",
        "Dg" to "dg",
        "Diff" to "diff",
        "Django/Jinja" to "django",
        "DTD" to "dtd",
        "Duel" to "duel",
        "Dylan" to "dylan",
        "Dylan Console" to "dylan-console",
        "DylanLID" to "dylan-lid",
        "eC" to "ec",
        "ECL" to "ecl",
        "Elixir" to "elixir",
        "Elixir iex session" to "iex",
        "Embedded Ragel" to "ragel-em",
        "ERB" to "erb",
        "Erlang" to "erlang",
        "Erlang erl session" to "erl",
        "Evoque" to "evoque",
        "Factor" to "factor",
        "Fancy" to "fancy",
        "Fantom" to "fantom",
        "Felix" to "felix",
        "Fortran" to "fortran",
        "FoxPro" to "foxpro",
        "FSharp" to "fsharp",
        "GAS" to "gas",
        "Genshi" to "genshi",
        "Genshi Text" to "genshitext",
        "Gettext Catalog" to "pot",
        "Gherkin" to "gherkin",
        "GLSL" to "glsl",
        "Gnuplot" to "gnuplot",
        "Go" to "go",
        "GoodData-CL" to "gooddata-cl",
        "Gosu" to "gosu",
        "Gosu Template" to "gst",
        "Groff" to "groff",
        "Groovy" to "groovy",
        "Haml" to "haml",
        "Haskell" to "haskell",
        "haXe" to "haxe",
        "HTML" to "html",
        "HTML+Cheetah" to "html+cheetah",
        "HTML+Django/Jinja" to "html+django",
        "HTML+Evoque" to "html+evoque",
        "HTML+Genshi" to "html+genshi",
        "HTML+Lasso" to "html+lasso",
        "HTML+Mako" to "html+mako",
        "HTML+Myghty" to "html+myghty",
        "HTML+PHP" to "html+php",
        "HTML+Smarty" to "html+smarty",
        "HTML+Velocity" to "html+velocity",
        "HTTP" to "http",
        "Hxml" to "hxml",
        "Hybris" to "hybris",
        "IDL" to "idl",
        "INI" to "ini",
        "Io" to "io",
        "Ioke" to "ioke",
        "IRC logs" to "irc",
        "Jade" to "jade",
        "JAGS" to "jags",
        "Java" to "java",
        "Java Server Page" to "jsp",
        "JavaScript" to "javascript",
        "JavaScript+Cheetah" to "javascript+cheetah",
        "JavaScript+Django/Jinja" to "javascript+django",
        "JavaScript+Genshi Text" to "javascript+genshi",
        "JavaScript+Lasso" to "javascript+lasso",
        "JavaScript+Mako" to "javascript+mako",
        "JavaScript+Myghty" to "javascript+myghty",
        "JavaScript+PHP" to "javascript+php",
        "JavaScript+Ruby" to "javascript+ruby",
        "JavaScript+Smarty" to "javascript+smarty",
        "JSON" to "json",
        "Julia" to "julia",
        "Julia console" to "jlcon",
        "Kconfig" to "kconfig",
        "Koka" to "koka",
        "Kotlin" to "kotlin",
        "Lasso" to "lasso",
        "Lighttpd configuration file" to "lighttpd",
        "Literate Haskell" to "literate-haskell",
        "LiveScript" to "livescript",
        "LLVM" to "llvm",
        "Logos" to "logos",
        "Logtalk" to "logtalk",
        "Lua" to "lua",
        "Makefile" to "makefile",
        "Mako" to "mako",
        "MAQL" to "maql",
        "Mason" to "mason",
        "Matlab" to "matlab",
        "Matlab session" to "matlabsession",
        "MiniD" to "minid",
        "Modelica" to "modelica",
        "Modula-2" to "modula2",
        "MoinMoin/Trac Wiki markup" to "moin",
        "Monkey" to "monkey",
        "MOOCode" to "moocode",
        "MoonScript" to "moonscript",
        "Mscgen" to "mscgen",
        "MuPAD" to "mupad",
        "MXML" to "mxml",
        "Myghty" to "myghty",
        "MySQL" to "mysql",
        "NASM" to "nasm",
        "Nemerle" to "nemerle",
        "NewLisp" to "newlisp",
        "Newspeak" to "newspeak",
        "Nginx configuration file" to "nginx",
        "Nimrod" to "nimrod",
        "NSIS" to "nsis",
        "NumPy" to "numpy",
        "Objdump" to "objdump",
        "Objective-C" to "objectivec",
        "Objective-C++" to "objectivec++",
        "Objective-J" to "objectivej",
        "OCaml" to "ocaml",
        "Octave" to "octave",
        "Ooc" to "ooc",
        "Opa" to "opa",
        "OpenEdge ABL" to "openedge",
        "Perl" to "perl",
        "Perl6" to "perl6",
        "PHP" to "php",
        "PL/pgSQL" to "plpgsql",
        "PostgreSQL console (psql)" to "postgresql-console",
        "PostgreSQL SQL dialect" to "postgresql",
        "PostScript" to "postscript",
        "POVRay" to "pov",
        "PowerShell" to "powershell",
        "Prolog" to "prolog",
        "Properties" to "jproperties",
        "Protocol Buffer" to "protobuf",
        "Puppet" to "puppet",
        "PyPy Log" to "pypylog",
        "Python" to "python",
        "Python 3" to "python3",
        "Python 3.0 Traceback" to "py3tb",
        "Python console session" to "pycon",
        "Python2 Traceback" to "py2tb",
        "QML" to "qml",
        "Racket" to "racket",
        "Ragel" to "ragel",
        "Ragel in C Host" to "ragel-c",
        "Ragel in CPP Host" to "ragel-cpp",
        "Ragel in D Host" to "ragel-d",
        "Ragel in Java Host" to "ragel-java",
        "Ragel in Objective C Host" to "ragel-c",
        "Ragel in Ruby Host" to "ragel-ruby",
        "RConsole" to "rconsole",
        "Rd" to "rd",
        "REBOL" to "rebol",
        "Redcode" to "redcode",
        "Regedit" to "registry",
        "reStructuredText" to "restructuredtext",
        "RHTML" to "rhtml",
        "RobotFramework" to "robotframework",
        "RPMSpec" to "spec",
        "Ruby" to "ruby",
        "Ruby irb session" to "rbcon",
        "Rust" to "rust",
        "S" to "s",
        "Sass" to "sass",
        "Scala" to "scala",
        "Scalate Server Page" to "ssp",
        "Scaml" to "scaml",
        "Scheme" to "scheme",
        "Scilab" to "scilab",
        "SCSS" to "scss",
        "Shell Session" to "pwsh-session",
        "Smali" to "smali",
        "Smalltalk" to "smalltalk",
        "Smarty" to "smarty",
        "Snobol" to "snobol",
        "SourcePawn" to "sp",
        "SQL" to "sql",
        "Sqlite Console" to "sqlite3",
        "SquidConf" to "squidconf",
        "Stan" to "stan",
        "Standard ML" to "sml",
        "System Verilog" to "systemverilog",
        "Tcl" to "tcl",
        "Tcsh" to "tcsh",
        "Tea" to "tea",
        "TeX" to "tex",
        "Treetop" to "treetop",
        "TypeScript" to "typescript",
        "UrbiScript" to "urbiscript",
        "Vala" to "vala",
        "VB.net" to "vbnet",
        "Velocity" to "velocity",
        "Verilog" to "verilog",
        "VGL" to "vgl",
        "vhdl" to "vhdl",
        "VimL" to "vim",
        "XML" to "xml",
        "XML+Cheetah" to "xml+cheetah",
        "XML+Django/Jinja" to "xml+django",
        "XML+Evoque" to "xml+evoque",
        "XML+Lasso" to "xml+lasso",
        "XML+Mako" to "xml+mako",
        "XML+Myghty" to "xml+myghty",
        "XML+PHP" to "xml+php",
        "XML+Ruby" to "xml+ruby",
        "XML+Smarty" to "xml+smart",
        "XML+Velocity" to "xml+velocity",
        "XQuery" to "xquery",
        "XSLT" to "xslt",
        "Xtend" to "xtend",
        "YAML" to "yaml",
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

        val languagesKeys = HILITEME_LANGUAGES_TO_LEXER.keys
        val languagesList = FXCollections.observableArrayList<String>()
        languagesKeys.forEach { key ->
            languagesList.add(key)
        }
        val languagePicker = ComboBox(languagesList) // Add a dropdown with all the available languages for the hilite.me api
        languagePicker.selectionModel.select("Kotlin")

        vboxPane.children.addAll(headerText, textArea, languagePicker)

        dialog.dialogPane.content = vboxPane

        // Set the result of the dialog to the text that's entered in the text area
        dialog.setResultConverter {
            // <Button type, Code text, Language>
            val language = if (HILITEME_LANGUAGES_TO_LEXER[languagePicker.value] != null) HILITEME_LANGUAGES_TO_LEXER[languagePicker.value]!! else ""
            return@setResultConverter Triple<ButtonType, String, String>(it, textArea.text, language)
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
            uriBuilder.queryParam("lexer", language)
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