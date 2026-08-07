package dev.shounakmulay.devpulse.bridge.markdownconverter

interface HtmlToMarkdownConverter{
    suspend fun convert(html: String): String?
}