package com.ericwathome.stovetime.core.data.model.cook

import com.ericwathome.stovetime.core.data.model.progress.CookKind

data class CookPlan(
    val kind: CookKind,
    val dishId: String,
    val name: String,
    val photo: String?,
    val baseXp: Int,
    val steps: List<CookStep>,
)

data class CookStep(
    val text: String,
    val detail: String?,
    val timerSeconds: Int?,
    val doneness: String?,
    val tip: String?,
)
