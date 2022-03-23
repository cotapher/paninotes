
class Launcher {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // JavaFx issue with exporting jars
            // See https://stackoverflow.com/a/52654791/3956070 for explanation
            PaninotesClient.main(args)
        }
    }
}