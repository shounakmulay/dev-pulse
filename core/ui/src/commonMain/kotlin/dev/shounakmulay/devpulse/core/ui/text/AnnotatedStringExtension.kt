package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.ui.text.AnnotatedString

fun AnnotatedString?.orEmpty() = if (isNullOrBlank()) AnnotatedString("") else this