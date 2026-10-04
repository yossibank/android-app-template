package com.yossibank.androidapptemplate.feature.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.yossibank.shared.product.CatalogEntry
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

data class Chapter(
    val index: Int,
    val first: Int,
    val last: Int,
    val spreads: List<Spread>,
) {
    val numeral: String
        get() = numeral(index + 1)

    companion object {
        fun numeral(value: Int): String = buildString {
            var rest = value
            val symbols = listOf(
                1000 to "M",
                900 to "CM",
                500 to "D",
                400 to "CD",
                100 to "C",
                90 to "XC",
                50 to "L",
                40 to "XL",
                10 to "X",
                9 to "IX",
                5 to "V",
                4 to "IV",
                1 to "I",
            )
            for ((amount, symbol) in symbols) {
                while (rest >= amount) {
                    append(symbol)
                    rest -= amount
                }
            }
        }
    }
}

data class Spread(
    val lead: CatalogEntry,
    val pair: List<CatalogEntry>,
)

val ProductList.chapters: List<Chapter>
    get() = products.chunked(pageSize).mapIndexed { index, chapter ->
        Chapter(
            index = index,
            first = index * pageSize + 1,
            last = minOf((index + 1) * pageSize, total),
            spreads = chapter.chunked(3).map { Spread(lead = it.first(), pair = it.drop(1)) },
        )
    }

val ProductList.nextChapter: Chapter?
    get() = if (products.size % pageSize != 0) {
        null
    } else {
        Chapter(
            index = products.size / pageSize,
            first = products.size + 1,
            last = minOf(products.size + pageSize, total),
            spreads = emptyList(),
        )
    }

val CatalogEntry.paddedId: String
    get() = "%03d".format(Locale.ROOT, id)

fun CatalogEntry.priceText(locale: Locale = Locale.getDefault()): String = NumberFormat
    .getCurrencyInstance(locale)
    .apply {
        currency = Currency.getInstance("USD")
        minimumFractionDigits = currency.defaultFractionDigits
        maximumFractionDigits = currency.defaultFractionDigits
    }.format(price)

fun CatalogEntry.highlightedTitle(
    query: String,
    mark: Color,
    locale: Locale = Locale.getDefault(),
): AnnotatedString {
    val trimmed = query.trim()
    val start = if (trimmed.isEmpty()) -1 else title.lowercase(locale).indexOf(trimmed.lowercase(locale))

    return buildAnnotatedString {
        append(title)
        if (start >= 0) {
            addStyle(SpanStyle(background = mark), start, start + trimmed.length)
        }
    }
}
