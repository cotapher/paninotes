import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.Paths

@Serializable
data class ConfigFile(
    var width: Double,
    var height: Double,
    var x: Double,
    var y: Double,
    var isMaximized: Boolean,
    var darkTheme: Boolean
)

object Config {

    private var configFile = ConfigFile(800.0, 500.0, 0.0, 0.0, isMaximized = false, darkTheme = false)
    private val CONFIG_PATH = File(Paths.get("src/main/resources/config.json").toUri())

    init {
        if (CONFIG_PATH.exists()) {
            try {
                // load config from file
                configFile = Json.decodeFromString(CONFIG_PATH.readText(Charsets.UTF_8))
            } catch (e: SerializationException) {
                // existing config is wrongly formatted; create new config with defaults
                CONFIG_PATH.delete()
                CONFIG_PATH.createNewFile()
                CONFIG_PATH.writeText(Json.encodeToString(configFile))
            }
        } else {
            // create new config file with defaults if one does not exist
            CONFIG_PATH.createNewFile()
            CONFIG_PATH.writeText(Json.encodeToString(configFile))
        }
    }

    fun saveConfig() {
        CONFIG_PATH.writeText(Json.encodeToString(configFile))
    }

    // Defining getters/setters
    var width: Double
        get() = configFile.width
        set(value) { configFile.width = value }

    var height: Double
        get() = configFile.height
        set(value) { configFile.height = value }

    var x: Double
        get() = configFile.x
        set(value) { configFile.x = value }

    var y: Double
        get() = configFile.y
        set(value) { configFile.y = value }

    var isMaximized: Boolean
        get() = configFile.isMaximized
        set(value) { configFile.isMaximized = value }

    var darkTheme: Boolean
        get() = configFile.darkTheme
        set(value) { configFile.darkTheme = value }
}