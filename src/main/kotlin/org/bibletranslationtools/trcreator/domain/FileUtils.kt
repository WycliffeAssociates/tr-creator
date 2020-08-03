package org.bibletranslationtools.trcreator.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.subjects.PublishSubject
import org.wycliffeassociates.io.ArchiveOfHolding
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipFile

class FileUtils(private val publisher: PublishSubject<Double>? = null) {
    fun unzip(zip: File): Maybe<File> {
        return Maybe.fromCallable {
            val dest: File = Files.createTempDirectory("tr_unzip").toFile()
            ZipFile(zip).use { zip ->
                val length = zip.size().toDouble()
                var i = 1.0
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
                            val progress = (i++) / length
                            publisher?.onNext(progress)
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
            Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun createTr(dir: File): Maybe<File> {
        return Maybe.fromCallable {
            val aoh = ArchiveOfHolding(ArchiveOfHolding.OnProgressListener { progress ->
                publisher?.onNext(progress / 100.0)
            })
            aoh.createArchiveOfHolding(dir, true)

            dir.deleteRecursively()
            File(dir.parent, dir.name + ".tr")
        }
    }
}
