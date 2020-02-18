package bible.translationtools.trcreator.app.mainview

import bible.translationtools.trcreator.domain.FileUtils
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.application.Platform
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Files

class MainView : View("My View") {

    private val viewModel: MainViewModel by inject()

    init {
        title = messages.getString("app_name")
        viewModel.trFileMessages.subscribe { message ->
            Platform.runLater {
                showPopup(message.type, message.title, message.text)
            }
        }
        viewModel.trFileComplete.subscribe { file ->
            Platform.runLater {
                saveAs(file)
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

        vbox {
            addClass(MainViewStyles.progress)
            visibleWhen { viewModel.processingProperty }

            progressbar {
                progressProperty().bind(viewModel.progressProperty)
                maxWidth = Double.MAX_VALUE
                padding = insets(50, 0)
            }

            label(viewModel.progressTitleProperty)
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

    private fun saveAs(srcFile: File) {
        val fileChooser = FileChooser()
        fileChooser.title = messages.getString("save_tr_as")
        fileChooser.initialFileName = "untitled.tr"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("TR Files", "*.tr")
        )

        val destFile = fileChooser.showSaveDialog(null)
        if(destFile != null) {
            viewModel.moveTrFile(srcFile, destFile)
        } else {
            viewModel.processingProperty.value = false
        }
    }

    private fun showPopup(type: MessageDialog.TYPE, title: String, message: String) {
        MessageDialog(type, title, message).show(root)
    }
}
