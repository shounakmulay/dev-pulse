package dev.shounakmulay.devpulse.readability.estimate

/**
 * Estimates reading time based on word count.
 *
 * Standard reading speed assumptions:
 * - Average adult reads ~238 words per minute (English)
 * - For code-heavy content, we use a slower rate (~150 wpm)
 */
object ReadingTimeEstimator {

    /** Words per minute for general prose */
    private const val WORDS_PER_MINUTE_PROSE = 238

    /** Words per minute for technical/code-heavy content */
    private const val WORDS_PER_MINUTE_TECHNICAL = 150

    /** Minimum reading time to report */
    private const val MIN_READING_TIME_MINUTES = 1

    /**
     * Estimate reading time from article text.
     *
     * @param text The plain text of the article
     * @param isTechnical If true, use slower reading speed
     * @return Estimated reading time in minutes
     */
    fun estimate(text: String, isTechnical: Boolean = false): Int {
        val wordCount = text.split(Regex("\\s+")).count { it.isNotEmpty() }
        val wpm = if (isTechnical) WORDS_PER_MINUTE_TECHNICAL else WORDS_PER_MINUTE_PROSE

        val minutes = maxOf(MIN_READING_TIME_MINUTES, (wordCount + wpm - 1) / wpm)
        return minutes
    }

    /**
     * Estimate reading time from character count.
     * Assumes average English word is ~5 characters.
     */
    fun estimateFromChars(charCount: Int, isTechnical: Boolean = false): Int {
        val wordCount = charCount / 5
        val wpm = if (isTechnical) WORDS_PER_MINUTE_TECHNICAL else WORDS_PER_MINUTE_PROSE
        return maxOf(MIN_READING_TIME_MINUTES, (wordCount + wpm - 1) / wpm)
    }
}
