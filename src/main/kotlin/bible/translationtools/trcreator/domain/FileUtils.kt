package bible.translationtools.trcreator.domain

import com.wycliffeassociates.io.ArchiveOfHolding
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipFile

class FileUtils {
    fun unzip(zip: File): Maybe<File> {
        return Maybe.fromCallable {
            val dest: File = Files.createTempDirectory("tr_unzip").toFile()
            ZipFile(zip).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if(entry.isDirectory) {
                        File(dest, entry.name).mkdirs()
                        return@forEach
                    }
                    if(!File(dest, entry.name).parentFile.exists()) {
                        File(dest, entry.name).parentFile.mkdirs()
                    }
                    zip.getInputStream(entry).use { input ->
                        File(dest, entry.name).outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            dest
        }
    }

    fun move(src: File, dest: File): Completable {
        return Completable.fromAction {
            Files.move(src.toPath(), File(dest, src.name).toPath())
        }
    }

    fun createTr(dir: File, fromZip: Boolean): Maybe<File> {
        return Maybe.fromCallable {
            val aoh = ArchiveOfHolding(ArchiveOfHolding.OnProgressListener { progress ->
                println(progress)
            })
            aoh.createArchiveOfHolding(dir, true)

            if(fromZip) dir.deleteRecursively()
            File(dir.parent, dir.name + ".tr")
        }
    }
}