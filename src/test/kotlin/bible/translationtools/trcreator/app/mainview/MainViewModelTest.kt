package bible.translationtools.trcreator.app.mainview

import bible.translationtools.trcreator.TestUtils
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tornadofx.ViewModel
import java.io.File


class MainViewModelTest: ViewModel() {

    private val viewModel: MainViewModel by inject()

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun createTrFromDir() {
        val subscriber = TestObserver<Unit>()
        val srcDir: File = tempFolder.newFolder("testfolder")
        val observable = Observable.fromCallable {
            viewModel.trFromDirectory(srcDir)
        }

        observable.subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
    }

    @Test
    fun createTrFromZip() {
        val subscriber = TestObserver<Unit>()
        val files = arrayOf(
            tempFolder.newFile("01.wav"),
            tempFolder.newFile("02.wav"),
            tempFolder.newFile("03.wav")
        )
        val zip = tempFolder.newFile("test.zip")
        TestUtils.makeZipFile(files, zip)
        val observable = Observable.fromCallable {
            viewModel.trFromZip(zip)
        }

        observable.subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
    }
}