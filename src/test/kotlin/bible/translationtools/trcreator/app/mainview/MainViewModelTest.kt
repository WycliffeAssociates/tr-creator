package bible.translationtools.trcreator.app.mainview

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import tornadofx.ViewModel
import java.io.File


class MainViewModelTest: ViewModel() {
    private val mainViewModel: MainViewModel by inject()
    private val subscriber = TestObserver<Unit>()
    private val target: File = File("parent", "child")

    @Test
    fun createTrFromDir() {
        val observable: Observable<Unit> = Observable.fromCallable {
            mainViewModel.trFromDirectory(target)
        }

        observable.subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
    }

    @Test
    fun createTrFromZip() {
        val observable: Observable<Unit> = Observable.fromCallable {
            mainViewModel.trFromZip(target)
        }

        observable.subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
    }
}