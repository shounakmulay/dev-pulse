package dev.shounakmulay.devpulse.bridge.markdownconverter

import android.util.Log
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import io.xberg.android.HtmlToMarkdown
import org.koin.core.annotation.Factory

@Factory
class AndroidHtmlToMarkdownConverter(
    private val dispatcherProvider: DispatcherProvider
) : HtmlToMarkdownConverter {
    override suspend fun convert(html: String): String? = dispatcherProvider.runCatchingOnDefault {
        Log.d("AndroidHtmlToMarkdownConverter", html)
        val result = HtmlToMarkdown.convertAsync(html)
        Log.d("AndroidHtmlToMarkdownConverter", result.content.ifNullOrBlank {
            "Failed to convert"
        })
        result.content
    }.getOrThrow()
}