package com.ericwathome.stovetime.core.common.helpers

import java.text.Normalizer
import java.util.Locale

private val combiningMarks = Regex("\\p{Mn}+")

val String.foldedForSearch: String
    get() =
        Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace(combiningMarks, "")
            .lowercase(Locale.ROOT)
            .trim()

fun String.matchesSearch(query: String): Boolean {
    val folded = query.foldedForSearch
    return folded.isEmpty() || foldedForSearch.contains(folded)
}
