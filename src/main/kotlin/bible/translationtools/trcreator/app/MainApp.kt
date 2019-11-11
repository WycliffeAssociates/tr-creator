package bible.translationtools.trcreator.app

import bible.translationtools.trcreator.app.mainview.MainView
import bible.translationtools.trcreator.app.mainview.MainViewStyles
import tornadofx.App
import tornadofx.launch

class MainApp : App(MainView::class, MainViewStyles::class)

fun main(args: Array<String>) {
    launch<MainApp>()
}