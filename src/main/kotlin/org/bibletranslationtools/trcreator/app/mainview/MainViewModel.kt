package org.bibletranslationtools.trcreator.app.mainview

import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import org.bibletranslationtools.trcreator.domain.FileUtils
import tornadofx.ViewModel
import tornadofx.getProperty
import tornadofx.property
import java.io.File
import java.nio.file.Files
import org.apache.commons.io.FileUtils as FileUtilsIO

class MainViewModel : ViewModel() {
    private var processing: Boolean by property(false)
    val processingProperty = getProperty(MainViewModel::processing)

    private var progress: Double by property(0.0)
    val progressProperty = getProperty(MainViewModel::progress)

    private var progressTitle: String by property("")
    val progressTitleProperty = getProperty(MainViewModel::progressTitle)

    private lateinit var parentDir: File
    private lateinit var initialFileName: String

    val trFileMessages = PublishSubject.create<MessageDialog.Message>()
    val trFileComplete = PublishSubject.create<File>()

    private val progressSubject = PublishSubject.create<Double>()

    init {
        progressSubject.subscribe {
            progress = it
        }
    }

    fun trFromZip(zip: File) {
        progress = 0.0
        progressTitle = messages.getString("unzipping")
        processing = true
        initialFileName = zip.nameWithoutExtension
        defineTargetDir(zip)
        FileUtils(progressSubject).unzip(zip)
            .doOnSuccess { dir ->
                createTr(dir)
            }
            .onErrorComplete { error ->
                println(error.message)
                trFileMessages.onNext(
                    MessageDialog.Message(
                        MessageDialog.TYPE.ERROR,
                        messages.getString("error_occurred"),
                        "" + error.message
                    )
                )
                true
            }
            .doOnComplete {
                processing = false
            }
            .subscribeOn(Schedulers.computation())
            .subscribe()
    }

    fun trFromDirectory(dir: File) {
        processing = true
        initialFileName = dir.name
        val target: File = Files.createTempDirectory("tr_temp").toFile()
        FileUtilsIO.copyDirectoryToDirectory(dir, target)
        defineTargetDir(target)
        createTr(target)
    }

    private fun createTr(dir: File) {
        FileUtils(progressSubject).createTr(dir)
            .doOnSuccess { trFile ->
                trFileComplete.onNext(trFile)
            }
            .onErrorComplete { error ->
                println(error.message)
                trFileMessages.onNext(
                    MessageDialog.Message(
                        MessageDialog.TYPE.ERROR,
                        messages.getString("error_occurred"),
                        "" + error.message
                    )
                )
                true
            }
            .doOnComplete {
                processing = false
            }
            .doOnSubscribe {
                Platform.runLater {
                    progress = 0.0
                    progressTitle = messages.getString("generating_tr")
                }
            }
            .subscribeOn(Schedulers.computation())
            .subscribe()
    }

    private fun defineTargetDir(dirOrFile: File) {
        parentDir = dirOrFile.parentFile
    }

    fun moveTrFile(srcFile: File, destFile: File) {
        FileUtils().move(srcFile, destFile)
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onError = { error ->
                    processing = false
                    trFileMessages.onNext(
                        MessageDialog.Message(
                            MessageDialog.TYPE.ERROR,
                            messages.getString("error_occurred"),
                            "" + error.message
                        )
                    )
                },
                onComplete = {
                    processing = false
                    trFileMessages.onNext(
                        MessageDialog.Message(
                            MessageDialog.TYPE.SUCCESS,
                            messages.getString("success"),
                            messages.getString("success_message")
                        )
                    )
                }
            )
    }

    fun initialFileName(): String {
        return "$initialFileName.tr"
    }
}
