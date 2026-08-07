package dev.shounakmulay.devpulse.core.data.db.converter

import androidx.room3.ColumnTypeConverter
import androidx.room3.ProvidedColumnTypeConverter
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalCompressedText
import org.koin.core.annotation.Single

@ProvidedColumnTypeConverter
@Single
class LocalCompressedTextTypeConverter(
    private val gzipCompressor: GzipCompressor
) {
    @ColumnTypeConverter
    fun fromByteArray(bytes: ByteArray): LocalCompressedText {
        return LocalCompressedText(gzipCompressor.decompress(bytes))
    }

    @ColumnTypeConverter
    fun fromCompressedText(text: LocalCompressedText): ByteArray {
        return gzipCompressor.compress(text.text)
    }
}