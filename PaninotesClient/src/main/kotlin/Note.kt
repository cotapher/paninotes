import BackupState.BackupState
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.File
import java.time.LocalDateTime

class Note(var filePath: File? = null) {
    var id: Int? = null
    var title: String? = null
    var htmlText: String? = ""
    var fileMetadata: MutableMap<String, String>? = null
    var notebookId: Int? = null

    @JsonIgnore
    var notebook: Notebook? = null
    var lastBackupTime: LocalDateTime? = null
    var backupState: BackupState = BackupState.NOT_BACKED_UP
    var isOpen: Boolean = false

    init {
        this.title = resolveNameFromPath()
        if (!this.filePath!!.parentFile.exists()) {
            this.filePath?.parentFile?.mkdirs()
        }
        if (!this.filePath!!.exists()) {
            this.filePath?.createNewFile()
        }

    }

    fun resolveNameFromPath(): String? {
        return filePath?.nameWithoutExtension
    }

    fun setContents() {
        htmlText = readHTMLFile(filePath)
    }

    fun readHTMLFile(file: File?): String? {
        return file?.readText(Charsets.UTF_8)
    }

    fun saveNote(HTMLString: String) {
        filePath?.writeText(HTMLString)
        if (HTMLString != htmlText) {
            //only update if text changed
            backupState = BackupState.OUT_OF_SYNC
        }
        setContents()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Note)
                && other.title == title
                && other.notebook?.title == notebook?.title
    }
}