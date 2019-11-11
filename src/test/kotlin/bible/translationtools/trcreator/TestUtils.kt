package bible.translationtools.trcreator

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class TestUtils {
    companion object {
        fun makeZipFile(srcFiles: Array<File>, targetFile: File) {
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
}