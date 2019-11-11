package bible.translationtools.trcreator.domain

import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.*
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry


class FileUtilsTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun testMove() {
        val subscriber = TestObserver<Unit>()
        val srcFile: File = tempFolder.newFile("testfile")
        val destDir: File = tempFolder.newFolder("testfolder")

        FileUtils().move(srcFile, destDir)
            .subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
    }

    @Test
    fun testCreateTrFromDirectory() {
        val subscriber = TestObserver<File>()
        val srcDir: File = tempFolder.newFolder("testfolder")

        FileUtils().createTr(srcDir, false)
            .subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
        assert(srcDir.exists())
    }

    @Test
    fun testCreateTrFromUnzippedFolder() {
        val subscriber = TestObserver<File>()
        val srcDir: File = tempFolder.newFolder("testfolder")

        FileUtils().createTr(srcDir, true)
            .subscribe(subscriber)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
        assert(!srcDir.exists())
    }

    @Test
    fun testUnzip() {
        val subscriber = TestObserver<File>()
        val files = arrayOf(
            tempFolder.newFile("01.wav"),
            tempFolder.newFile("02.wav"),
            tempFolder.newFile("03.wav")
        )
        val zip = tempFolder.newFile("test.zip")
        makeZipFile(files, zip)

        FileUtils().unzip(zip)
            .subscribe(subscriber)

        subscriber.assertNoErrors()
        subscriber.assertComplete()
    }

    private fun makeZipFile(srcFiles: Array<File>, targetFile: File) {
        var out = ZipOutputStream(BufferedOutputStream(FileOutputStream(targetFile)))
        for (file in srcFiles) {
            var origin = BufferedInputStream(FileInputStream(file))
            var entry = ZipEntry(file.name)
            out.putNextEntry(entry)
            origin.copyTo(out, 1024)
            origin.close()
        }
        out.close()
    }
}