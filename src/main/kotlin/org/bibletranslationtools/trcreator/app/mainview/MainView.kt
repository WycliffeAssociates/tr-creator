package org.bibletranslationtools.trcreator.app.mainview

import javafx.application.Platform
import javafx.stage.FileChooser
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.io.File

class MainView : View() {

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
                    label(messages["directory"]).apply {
                        addClass(MainViewStyles.browse)

                        graphic = FontIcon("gmi-folder:50:PURPLE")

                        setOnMouseClicked {
                            chooseDirectory()
                        }
                    }
                }
                vbox {
                    label(messages["zip_file"]).apply {
                        addClass(MainViewStyles.browse)

                        graphic = FontIcon("fa-file-zip-o:50:PURPLE")

                        setOnMouseClicked {
                            chooseZip()
                        }
                    }
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
            arrayOf(FileChooser.ExtensionFilter(messages.getString("zip_file"), "*.zip")),
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
        fileChooser.initialFileName = viewModel.initialFileName()
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter(messages.getString("tr_files"), "*.tr")
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
