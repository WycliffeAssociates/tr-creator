package org.bibletranslationtools.trcreator.app

import org.bibletranslationtools.trcreator.app.mainview.MainView
import org.bibletranslationtools.trcreator.app.mainview.MainViewStyles
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp : App(MainView::class, MainViewStyles::class) {
    override fun start(stage: Stage) {
        stage.icons.add(
            Image(javaClass.getResource("/launcher.png").openStream())
        )
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>()
}
