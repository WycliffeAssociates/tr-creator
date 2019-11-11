package bible.translationtools.trcreator.app.mainview

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.application.Platform
import javafx.stage.FileChooser
import tornadofx.*

class MainView : View("My View") {

    private val viewModel: MainViewModel by inject()

    init {
        title = messages.getString("app_name")
        viewModel.trFileMessages.subscribe { message ->
            Platform.runLater {
                showPopup(message.first, message.second)
            }
        }
    }

    override val root = stackpane {
        vbox {
            addClass(MainViewStyles.root)
            hiddenWhen { viewModel.processingProperty }

            label(messages.getString("browse_project"))
            hbox {
                addClass(MainViewStyles.buttons)
                vbox {
                    add(FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT, "6em").apply {
                        addClass(MainViewStyles.browse)
                        setOnMouseClicked {
                            chooseDirectory()
                        }
                    })
                    label(messages.getString("directory"))
                }
                vbox {
                    add(FontAwesomeIconView(FontAwesomeIcon.FILE_ZIP_ALT, "6em").apply {
                        addClass(MainViewStyles.browse)
                        setOnMouseClicked {
                            chooseZip()
                        }
                    })
                    label(messages.getString("zip_file"))
                }
            }
        }

        progressbar {
            visibleWhen { viewModel.processingProperty }
        }
    }

    private fun chooseZip() {
        val zipFiles = chooseFile(
            messages.getString("browse_project"),
            arrayOf(FileChooser.ExtensionFilter("Zip File", "*.zip")),
            FileChooserMode.Single
        )
        for (file in zipFiles) {
            viewModel.trFromZip(file)
            break
        }
    }

    private fun chooseDirectory() {
        val directory = chooseDirectory(messages.getString("browse_project"))
        directory?.let {
            viewModel.trFromDirectory(it)
        }
    }

    private fun showPopup(type: MessageDialog.TYPE, message: String) {
        val title = if(type == MessageDialog.TYPE.SUCCESS) {
            messages.getString("success")
        } else {
            messages.getString("error_occurred")
        }
        MessageDialog(type, title, message).show(root)
    }
}
