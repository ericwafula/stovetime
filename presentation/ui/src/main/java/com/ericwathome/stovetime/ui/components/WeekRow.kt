package com.ericwathome.stovetime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.components.DayDot
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.ui.R
import com.ericwathome.stovetime.ui.helpers.narrowDayName
import kotlinx.datetime.LocalDate

@Immutable
data class WeekRowDay(
    val date: LocalDate,
    val cooked: Boolean,
    val isToday: Boolean,
)

@Composable
fun WeekRow(
    days: List<WeekRowDay>,
    cooks: Int,
    goal: Int,
    modifier: Modifier = Modifier,
) {
    val summary = pluralStringResource(R.plurals.week_row_summary, goal, cooks, goal)
    val cookedDays = days.count { it.cooked }
    val description = stringResource(R.string.week_row_description, summary, cookedDays)
    Column(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StoveText(text = summary, style = StovetimeTheme.typography.label)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            days.forEach { day ->
                DayDot(label = narrowDayName(day.date), cooked = day.cooked, isToday = day.isToday)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun WeekRowPreview() {
    StovetimePreviewTheme {
        WeekRow(
            days =
                (2..8).map { day ->
                    WeekRowDay(date = LocalDate(2026, 11, day), cooked = day in setOf(2, 4, 6), isToday = day == 6)
                },
            cooks = 3,
            goal = 4,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
