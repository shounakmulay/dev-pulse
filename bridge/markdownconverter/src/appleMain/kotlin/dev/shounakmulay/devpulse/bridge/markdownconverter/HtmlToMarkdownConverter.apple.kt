package dev.shounakmulay.devpulse.bridge.markdownconverter

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import org.koin.core.annotation.Factory

@Factory
class AppleHtmlToMarkdownConverter(
    private val dispatcherProvider: DispatcherProvider
) : HtmlToMarkdownConverter {
    override suspend fun convert(html: String): String? = dispatcherProvider.runCatchingOnDefault {
        requireNotNull(nativeHtmlToMarkdownConverter)
        nativeHtmlToMarkdownConverter?.convert(html)
    }.getOrNull()
}

interface HtmlToMarkdownConverterProtocol {
    fun convert(html: String): String?
}

private var nativeHtmlToMarkdownConverter: HtmlToMarkdownConverterProtocol? = null

fun setNativeHtmlToMarkdownConverter(converter: HtmlToMarkdownConverterProtocol) {
    nativeHtmlToMarkdownConverter = converter
}