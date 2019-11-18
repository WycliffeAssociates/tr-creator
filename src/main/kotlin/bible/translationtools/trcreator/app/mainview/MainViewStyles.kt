package bible.translationtools.trcreator.app.mainview

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.text.FontWeight
import tornadofx.*

class MainViewStyles : Stylesheet() {
    companion object {
        val root by cssclass()
        val heading by cssclass()
        val browse by cssclass()
        val buttons by cssclass()
        val progress by cssclass()
    }

    init {
        root {
            prefWidth = 500.px
            prefHeight = 400.px
            alignment = Pos.CENTER
            spacing = 40.px

            label {
                fontSize = 20.px
            }
        }

        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        browse {
            cursor = Cursor.HAND
        }

        buttons {
            padding = box(20.px)
            alignment = Pos.CENTER
            spacing = 100.px
        }

        progress {
            alignment = Pos.CENTER
            label {
                padding = box(20.px, 0.px)
            }
        }
    }
}