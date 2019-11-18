package bible.translationtools.trcreator.app.mainview

import bible.translationtools.trcreator.domain.FileUtils
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import tornadofx.*
import java.io.File

class MainViewModel : ViewModel() {
    private var processing: Boolean by property(false)
    val processingProperty = getProperty(MainViewModel::processing)

    private var progress: Double by property(0.0)
    val progressProperty = getProperty(MainViewModel::progress)

    private var progressTitle: String by property("")
    val progressTitleProperty = getProperty(MainViewModel::progressTitle)

    private lateinit var parentDir: File

    val trFileMessages = PublishSubject.create<Pair<MessageDialog.TYPE, String>>()
    val trFileComplete = PublishSubject.create<File>()

    private val progressSubject = PublishSubject.create<Double>()

    init {
        progressSubject.subscribe {
            progress = it
        }
    }

    fun trFromZip(zip: File) {
        progress = 0.0
        progressTitle = "Unzipping..."
        processing = true
        defineTargetDir(zip)
        FileUtils(progressSubject).unzip(zip)
            .doOnSuccess { dir ->
                createTr(dir, true)
            }
            .onErrorComplete { error ->
                println(error.message)
                trFileMessages.onNext(
                    Pair(MessageDialog.TYPE.ERROR, ""+error.message)
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
        defineTargetDir(dir)
        createTr(dir, false)
    }

    private fun createTr(dir: File, fromZip: Boolean) {
        FileUtils(progressSubject).createTr(dir, fromZip)
            .doOnSuccess { trFile ->
                trFileComplete.onNext(trFile)
            }
            .onErrorComplete { error ->
                println(error.message)
                trFileMessages.onNext(
                    Pair(MessageDialog.TYPE.ERROR, ""+error.message)
                )
                true
            }
            .doOnComplete {
                processing = false
            }
            .doOnSubscribe {
                Platform.runLater {
                    progress = 0.0
                    progressTitle = "Generating TR file..."
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
                        Pair(MessageDialog.TYPE.ERROR, ""+error.message)
                    )
                },
                onComplete = {
                    processing = false
                    trFileMessages.onNext(
                        Pair(MessageDialog.TYPE.SUCCESS, messages.getString("success_message"))
                    )
                }
            )
    }
}