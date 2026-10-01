package dev.shounakmulay.devpulse.core.data.feed.parser.html

import com.fleeksoft.ksoup.Ksoup
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import org.koin.core.annotation.Factory

@Factory
class ArticleFtsContentParser(
    private val ksop: Ksoup,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun parse(html: String): Result<String> = dispatcherProvider.runCatchingOnDefault {
        ksop.parse(html).body().text()
    }
}