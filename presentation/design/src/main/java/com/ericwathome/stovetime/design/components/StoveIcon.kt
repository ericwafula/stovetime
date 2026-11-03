package com.ericwathome.stovetime.design.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.R
import com.ericwathome.stovetime.design.theme.StovetimeTheme

enum class StoveIcons(
    @param:DrawableRes val resource: Int,
) {
    Back(R.drawable.ic_back),
    Book(R.drawable.ic_book),
    Check(R.drawable.ic_check),
    ChevronRight(R.drawable.ic_chevron_right),
    Close(R.drawable.ic_close),
    Flame(R.drawable.ic_flame),
    Heart(R.drawable.ic_heart),
    Home(R.drawable.ic_home),
    Lock(R.drawable.ic_lock),
    Minus(R.drawable.ic_minus),
    Pause(R.drawable.ic_pause),
    Play(R.drawable.ic_play),
    Plus(R.drawable.ic_plus),
    Search(R.drawable.ic_search),
    Star(R.drawable.ic_star),
    Timer(R.drawable.ic_timer),
    User(R.drawable.ic_user),
}

@Composable
fun StoveIcon(
    icon: StoveIcons,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = StovetimeTheme.colors.textPrimary,
    size: Dp = 24.dp,
) {
    Image(
        painter = painterResource(icon.resource),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}
