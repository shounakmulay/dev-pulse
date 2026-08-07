package dev.shounakmulay.devpulse.readability.heuristics

/**
 * All regular expressions used in readability.
 * Compiled once as Regex objects, not re-created in loops.
 *
 * These match Readability.prototype.REGEXPS exactly.
 */
object RegexPatterns {

    /** Class/id substrings that make an element an UNLIKELY candidate for article content */
    val unlikelyCandidates = Regex(
        "-ad-|ai2html|banner|breadcrumbs|combx|comment|community|cover-wrap|disqus|extra|" +
        "footer|gdpr|header|legends|menu|related|remark|replies|rss|shoutbox|sidebar|" +
        "skyscraper|social|sponsor|supplemental|ad-break|agegate|pagination|pager|popup|yom-remote",
        RegexOption.IGNORE_CASE
    )

    /** Substrings that counterbalance an unlikely candidate match */
    val okMaybeItsACandidate = Regex(
        "and|article|body|column|content|main|shadow",
        RegexOption.IGNORE_CASE
    )

    /** Positive class/id patterns — boost score */
    val positive = Regex(
        "article|body|content|entry|hentry|h-entry|main|page|pagination|post|text|blog|story",
        RegexOption.IGNORE_CASE
    )

    /** Negative class/id patterns — penalize score */
    val negative = Regex(
        "-ad-|hidden|^hid$| hid$| hid |^hid |banner|combx|comment|com-|contact|footer|" +
        "gdpr|masthead|media|meta|outbrain|promo|related|scroll|share|shoutbox|sidebar|" +
        "skyscraper|sponsor|shopping|tags|widget",
        RegexOption.IGNORE_CASE
    )

    /** Extraneous page types */
    val extraneous = Regex(
        "print|archive|comment|discuss|e[\\-]?mail|share|reply|all|login|sign|single|utility",
        RegexOption.IGNORE_CASE
    )

    /** Byline detection patterns */
    val byline = Regex(
        "byline|author|dateline|writtenby|p-author",
        RegexOption.IGNORE_CASE
    )

    /** Font tag replacement */
    val replaceFonts = Regex("<(/?)font[^>]*>", RegexOption.IGNORE_CASE)

    /** Normalize multiple whitespace to single space */
    val normalize = Regex("\\s{2,}")

    /** Video provider URLs */
    val videos = Regex(
        "//(www\\.)?((dailymotion|youtube|youtube-nocookie|player\\.vimeo|v\\.qq)\\.com|" +
        "(archive|upload\\.wikimedia)\\.org|player\\.twitch\\.tv)",
        RegexOption.IGNORE_CASE
    )

    /** Share button element patterns */
    val shareElements = Regex("(\\b|_)(share|sharedaddy)(\\b|_)", RegexOption.IGNORE_CASE)

    /** "Next page" link text */
    val nextLink = Regex("(next|weiter|continue|>([^|]|\$)|»([^|]|\$))", RegexOption.IGNORE_CASE)

    /** "Previous page" link text */
    val prevLink = Regex("(prev|earl|old|new|<|«)", RegexOption.IGNORE_CASE)

    /** Word tokenizer */
    val tokenize = Regex("\\W+")

    /** All whitespace */
    val whitespace = Regex("^\\s*$")

    /** Has non-whitespace content */
    val hasContent = Regex("\\S$")

    /** Hash-only URLs */
    val hashUrl = Regex("^#.+")

    /** srcset URL parsing */
    val srcsetUrl = Regex("(\\S+)(\\s+[\\d.]+[xw])?(\\s*(?:,|\$))")

    /** Base64 data URLs */
    val b64DataUrl = Regex("^data:\\s*([^\\s;,]+)\\s*;\\s*base64\\s*,", RegexOption.IGNORE_CASE)

    /** Commas in various scripts (Latin, Sindhi, Chinese, etc.) */
    val commas = Regex("\\u002C|\\u060C|\\uFE50|\\uFE10|\\uFE11|\\u2E41|\\u2E34|\\u2E32|\\uFF0C")

    /** Schema.org Article types for JSON-LD */
    val jsonLdArticleTypes = Regex(
        "^Article|AdvertiserContentArticle|NewsArticle|AnalysisNewsArticle|AskPublicNewsArticle|" +
        "BackgroundNewsArticle|OpinionNewsArticle|ReportageNewsArticle|ReviewNewsArticle|Report|" +
        "SatiricalArticle|ScholarlyArticle|MedicalScholarlyArticle|SocialMediaPosting|BlogPosting|" +
        "LiveBlogPosting|DiscussionForumPosting|TechArticle|APIReference\$"
    )

    /** Ad-related words in multiple languages */
    val adWords = Regex(
        "^(ad(vertising|vertisement)?|pub(licité)?|werb(ung)?|广告|Реклама|Anuncio)\$",
        RegexOption.IGNORE_CASE
    )

    /** Loading indicator words in multiple languages */
    val loadingWords = Regex(
        "^((loading|正在加载|Загрузка|chargement|cargando)(…|\\.\\.\\.)?)\$",
        RegexOption.IGNORE_CASE
    )

    /** Title separator characters */
    val titleSeparator = Regex(" [|\\-\\\\/>»] ")

    /** Hierarchical separator (for title splitting) */
    val hierarchicalSeparator = Regex(" [\\\\/>»] ")
}

/**
 * Scoring constants extracted from Readability.js.
 */
object ScoringConstants {
    /** Tags that receive base scores */
    val DEFAULT_TAGS_TO_SCORE = setOf(
        "SECTION", "H2", "H3", "H4", "H5", "H6", "P", "TD", "PRE"
    )

    /** Base scores per tag type */
    const val DIV_SCORE = 5
    const val PRE_TD_BLOCKQUOTE_SCORE = 3
    const val ADDRESS_LIST_FORM_SCORE = -3
    const val HEADING_TH_SCORE = -5

    /** Paragraph scoring */
    const val PARAGRAPH_BASE_SCORE = 1
    const val MIN_PARAGRAPH_LENGTH = 25
    const val MAX_LENGTH_SCORE = 3
    const val LENGTH_SCORE_INTERVAL = 100

    /** Ancestor score propagation */
    const val MAX_ANCESTOR_DEPTH = 5
    const val PARENT_SCORE_DIVIDER = 1.0
    const val GRANDPARENT_SCORE_DIVIDER = 2.0
    const val GREAT_GRANDPARENT_BASE_DIVIDER = 3.0

    /** Default thresholds */
    const val DEFAULT_CHAR_THRESHOLD = 500
    const val DEFAULT_N_TOP_CANDIDATES = 5

    /** Sibling content merging */
    const val SIBLING_SCORE_THRESHOLD_FACTOR = 0.2
    const val SIBLING_CLASS_NAME_BONUS_FACTOR = 0.2
    const val SIBLING_MIN_LENGTH = 80
    const val SIBLING_LINK_DENSITY_THRESHOLD = 0.25

    /** Alternative candidate ancestor merging */
    const val ALTERNATIVE_CANDIDATE_SCORE_RATIO = 0.75
    const val MINIMUM_TOP_CANDIDATES = 3

    /** Upward score threshold */
    const val UPWARD_SCORE_THRESHOLD_FACTOR = 1.0 / 3.0

    /** _clean conditionally thresholds */
    const val CLEAN_CONDITIONALLY_WEIGHT_THRESHOLD = 25
    const val CLEAN_CONDITIONALLY_LINK_DENSITY_LOW = 0.2
    const val CLEAN_CONDITIONALLY_LINK_DENSITY_HIGH = 0.5
    const val CLEAN_CONDITIONALLY_EMBED_CONTENT_LENGTH = 75

    /** Share element threshold */
    const val SHARE_ELEMENT_THRESHOLD = DEFAULT_CHAR_THRESHOLD

    /** Class weight */
    const val CLASS_WEIGHT_BONUS = 25
}

/**
 * HTML tag sets used throughout the algorithm.
 */
object TagSets {
    /** Elements that warrant converting their parent DIV to a P */
    val DIV_TO_P_ELEMS = setOf(
        "BLOCKQUOTE", "DL", "DIV", "IMG", "OL", "P", "PRE", "TABLE", "UL"
    )

    /** Elements to NOT alter to DIV */
    val ALTER_TO_DIV_EXCEPTIONS = setOf(
        "DIV", "ARTICLE", "SECTION", "P", "OL", "UL"
    )

    /** Phrasing content elements */
    val PHRASING_ELEMS = setOf(
        "ABBR", "AUDIO", "B", "BDO", "BR", "BUTTON", "CITE", "CODE",
        "DATA", "DATALIST", "DFN", "EM", "EMBED", "I", "IMG", "INPUT",
        "KBD", "LABEL", "MARK", "MATH", "METER", "NOSCRIPT", "OBJECT",
        "OUTPUT", "PROGRESS", "Q", "RUBY", "SAMP", "SCRIPT", "SELECT",
        "SMALL", "SPAN", "STRONG", "SUB", "SUP", "TEXTAREA", "TIME",
        "VAR", "WBR"
    )

    /** Unlikely ARIA roles — content to remove */
    val UNLIKELY_ROLES = setOf(
        "menu", "menubar", "complementary", "navigation", "alert",
        "alertdialog", "dialog"
    )

    /** HTML entity escape map */
    val HTML_ESCAPE_MAP = mapOf(
        "lt" to "<", "gt" to ">", "amp" to "&", "quot" to "\"", "apos" to "'"
    )

    /** Presentational attributes to strip */
    val PRESENTATIONAL_ATTRIBUTES = setOf(
        "align", "background", "bgcolor", "border", "cellpadding",
        "cellspacing", "frame", "hspace", "rules", "style", "valign", "vspace"
    )

    /** Elements with deprecated size attributes */
    val DEPRECATED_SIZE_ATTRIBUTE_ELEMS = setOf("TABLE", "TH", "TD", "HR", "PRE")
}
