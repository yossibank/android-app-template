package com.yossibank.androidapptemplate.core

import java.text.Normalizer
import java.util.Locale

private val COMBINING_MARKS = "\\p{Mn}+".toRegex()

fun String.standardContains(
    other: String,
    locale: Locale = Locale.getDefault(),
): Boolean = folded(locale).contains(other.folded(locale))

private fun String.folded(locale: Locale): String = Normalizer
    .normalize(this, Normalizer.Form.NFD)
    .replace(COMBINING_MARKS, "")
    .lowercase(locale)
