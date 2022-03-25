import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert
import org.slf4j.LoggerFactory

object StageUtils {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun saveOnClose(
        model: Model,
        stage: Stage,
        htmlEditor: CustomHTMLEditor
    ) {
        if (confirmClose(model, stage, htmlEditor)) stage.close()
    }

    fun confirmClose(
        model: Model,
        stage: Stage,
        htmlEditor: CustomHTMLEditor
    ): Boolean {
        if (model.currentNote != null && model.currentNote!!.htmlText != htmlEditor.htmlText) {
            val confirmationAlert = FlatAlert(Alert.AlertType.CONFIRMATION)
            confirmationAlert.initOwner(stage)
            confirmationAlert.contentText = "Save changes to ${model.currentNote?.title}?"
            confirmationAlert.buttonTypes.clear()
            val saveButton = ButtonType("Save", ButtonBar.ButtonData.YES)
            val discardButton = ButtonType("Discard", ButtonBar.ButtonData.NO)
            val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
            confirmationAlert.buttonTypes.addAll(saveButton, discardButton, cancelButton)

            //show the popup
            val result = confirmationAlert.showAndWait()

            if (result.isPresent) {
                logger.info(result.toString())
                logger.info(result.get().toString())
                if (result.get() == saveButton) {
                    logger.info(htmlEditor.htmlText)
                    model.currentNote?.saveNote(htmlEditor.htmlText)
                    return true
                }
                if (result.get() == discardButton) {
                    return true
                }
                return false
            }
        }
        return true
    }
}