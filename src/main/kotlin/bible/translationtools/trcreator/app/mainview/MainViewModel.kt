package bible.translationtools.trcreator.app.mainview

import bible.translationtools.trcreator.domain.FileUtils
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import tornadofx.*
import java.io.File

class MainViewModel : ViewModel() {
    private var processing: Boolean by property(false)
    val processingProperty = getProperty(MainViewModel::processing)

    private lateinit var parentDir: File

    val trFileMessages = PublishSubject.create<Pair<MessageDialog.TYPE, String>>()

    fun trFromZip(zip: File) {
        processing = true
        defineTargetDir(zip)
        FileUtils().unzip(zip)
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
        FileUtils().createTr(dir, fromZip)
            .doOnSuccess { trFile ->
                moveTrFile(trFile)
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

    private fun defineTargetDir(dirOrFile: File) {
        parentDir = dirOrFile.parentFile
    }

    private fun moveTrFile(trFile: File) {
        FileUtils().move(trFile, parentDir)
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