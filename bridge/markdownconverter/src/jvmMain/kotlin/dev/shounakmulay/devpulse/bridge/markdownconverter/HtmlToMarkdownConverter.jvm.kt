package dev.shounakmulay.devpulse.bridge.markdownconverter

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import io.xberg.htmltomarkdown.HtmlToMarkdown
import org.koin.core.annotation.Factory

@Factory
class JvmHtmlToMarkdownConverter(
    private val dispatcherProvider: DispatcherProvider
) : HtmlToMarkdownConverter {
    override suspend fun convert(html: String): String? = dispatcherProvider.runCatchingOnDefault {
        val result = HtmlToMarkdown.convert(html)
        result.content
    }.getOrNull()
}